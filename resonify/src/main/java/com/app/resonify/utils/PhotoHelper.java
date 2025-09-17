package com.app.resonify.utils;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Base64;

@Log4j2
public class PhotoHelper {

    public static final String BASE64_PREFIX = "data:image/jpeg;base64,";

    public static boolean isBase64(String photoUrl) {
        return photoUrl != null && !photoUrl.isBlank() && photoUrl.startsWith(BASE64_PREFIX);
    }

    public static String getPhotoAsBase64(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank() && photoUrl.startsWith(BASE64_PREFIX)) {
            return photoUrl; // Already a base64 string
        }

        try {
            // Create OkHttpClient that ignores SSL (for testing only)
            OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();

            Request request = new Request.Builder()
                    .url(photoUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                byte[] imageBytes = response.body().bytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                System.out.println("Base64 String:");
                return BASE64_PREFIX + base64Image;
            } catch (IOException e) {
                log.error("Error fetching image from URL: " + photoUrl, e);
            }
        } catch (Exception e) {
            log.error("Failed to fetch or encode image from URL: " + photoUrl, e);
        }
        return "";
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
}
