package com.app.resonify.utils;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Log4j2
public class PhotoHelper {

    public static final String BASE64_PREFIX = "data:image/jpeg;base64,";
    public static final int FIXED_HEIGHT = 300;

    public static boolean isBase64(String photoUrl) {
        return photoUrl != null && !photoUrl.isBlank() && photoUrl.startsWith(BASE64_PREFIX);
    }

    private static final OkHttpClient client;

    static {
        try {
            client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Ensure WebP plugin (and others) are registered
        ImageIO.scanForPlugins();
    }

    public static String getPhotoAsBase64(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) return "";

        Request request = new Request.Builder()
                .url(imageUrl)
                .header("User-Agent", "Mozilla/5.0") // helps with some servers
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download image: " + response);
            }

            // ✅ Accept JPG, PNG, WebP
            String contentType = response.header("Content-Type", "").toLowerCase();
            if (!contentType.startsWith("image/jpeg") &&
                    !contentType.startsWith("image/png") &&
                    !contentType.startsWith("image/webp")) {
                throw new IOException("Unsupported image MIME type: " + contentType + " | URL: " + imageUrl);
            }

            byte[] imageBytes = response.body().bytes();

            // Decode image (WebP plugin must be on classpath)
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (originalImage == null) {
                throw new IOException("Unsupported or invalid image format: " + imageUrl);
            }

            // Resize while maintaining aspect ratio
            int newWidth = (int) (((double) FIXED_HEIGHT / originalImage.getHeight()) * originalImage.getWidth());
            BufferedImage resizedImage = new BufferedImage(newWidth, FIXED_HEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setBackground(Color.WHITE); // fill transparency
            g2d.clearRect(0, 0, newWidth, FIXED_HEIGHT);
            g2d.drawImage(originalImage.getScaledInstance(newWidth, FIXED_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();

            // Encode to JPG Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            byte[] resizedBytes = baos.toByteArray();

            System.out.println("Resized image size: " + resizedBytes.length / 1024 + " KB");

            return BASE64_PREFIX + Base64.getEncoder().encodeToString(resizedBytes);
        }
    }
    static class UnsafeOkHttpClient {
        public static OkHttpClient getUnsafeOkHttpClient() throws Exception {
            final javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }
                    }
            };

            final javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (javax.net.ssl.X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        }
    }

    public static class ResizedImageResult {
        private final String base64;
        private final int sizeInBytes;

        public ResizedImageResult(String base64, int sizeInBytes) {
            this.base64 = base64;
            this.sizeInBytes = sizeInBytes;
        }

        public String getBase64() {
            return base64;
        }

        public int getSizeInBytes() {
            return sizeInBytes;
        }
    }

    public static ResizedImageResult resizeBase64Image(String base64Image, UUID id) throws IOException {
        if (base64Image == null || base64Image.isBlank()) {
            return new ResizedImageResult(base64Image, 0);
        }

        try {
            // 1️⃣ Strip prefix
            String base64Data = base64Image.contains(",")
                    ? base64Image.substring(base64Image.indexOf(",") + 1)
                    : base64Image;

            // 2️⃣ Remove whitespace / line breaks
            base64Data = base64Data.replaceAll("\\s", "");

            // Log original Base64 size
            System.out.println("Original Base64 length: " + base64Data.length() + " characters");
            System.out.println("Approx original bytes: " + (base64Data.length() * 3 / 4));

            // 4️⃣ Decode Base64 to bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            System.out.println("Decoded original image bytes: " + imageBytes.length);

            // 5️⃣ Decode image using Commons Imaging (supports CMYK/progressive JPEGs)
            BufferedImage originalImage = Imaging.getBufferedImage(imageBytes);
            if (originalImage == null) {
                throw new IOException("Failed to decode image ID: " + id);
            }

            // 6️⃣ Resize while keeping aspect ratio
            int newWidth = (int) (((double) FIXED_HEIGHT / originalImage.getHeight()) * originalImage.getWidth());
            Image scaledInstance = originalImage.getScaledInstance(newWidth, FIXED_HEIGHT, Image.SCALE_SMOOTH);

            BufferedImage resizedImage = new BufferedImage(newWidth, FIXED_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(scaledInstance, 0, 0, null);
            g2d.dispose();

            // 7️⃣ Encode back to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "JPG", baos);
            byte[] resizedBytes = baos.toByteArray();

            // Log resized size
            System.out.println("Resized image bytes: " + resizedBytes.length);
            System.out.println("Resized Base64 length: " + Base64.getEncoder().encodeToString(resizedBytes).length() + " characters");

            String resizedBase64 = Base64.getEncoder().encodeToString(resizedBytes);

            return new ResizedImageResult(BASE64_PREFIX + resizedBase64, resizedBytes.length);

        } catch (Exception e) {
            throw new IOException("Failed processing image ID: " + id, e);
        }
    }
}
