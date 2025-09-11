package com.app.resonify.utils;

import com.app.resonify.model.City;
import com.app.resonify.model.Concert;
import com.app.resonify.model.Country;
import com.app.resonify.model.Theater;
import com.app.resonify.repository.CityRepository;
import com.app.resonify.repository.ConcertRepository;
import com.app.resonify.repository.CountryRepository;
import com.app.resonify.repository.TheaterRepository;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Log4j2
public class CsvImportHelper {

    private static final int FIXED_HEIGHT = 200;

    // ----------------- City CSV -----------------
    public static void importCities(InputStream csvStream, CityRepository cityRepo, CountryRepository countryRepo) throws IOException {
        int count = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String cityName = parts[0].trim();
            String countryName = parts[1].trim();

            Country country = countryRepo.findByName(countryName)
                    .orElseThrow(() -> new RuntimeException("Country not found: " + countryName));

            if (!cityRepo.existsByNameAndCountry(cityName, country)) {
                City city = new City(null, cityName, country);
                cityRepo.save(city);
                count++;
            }
        }
        log.info("Imported {} new cities", count);
    }


    // ----------------- Theater CSV -----------------
    public static void importTheaters(InputStream csvStream, TheaterRepository theaterRepo, CityRepository cityRepo) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String name = parts[0].trim();
            String address = parts[1].trim();
            double lat = Double.parseDouble(parts[2].trim());
            double lng = Double.parseDouble(parts[3].trim());
            String cityName = parts[4].trim();
            String photoUrl = parts[5].trim();

            City city = cityRepo.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found: " + cityName));

            if (!theaterRepo.existsByNameAndCity(name, city)) {
                log.info("Importing theater: {} in city: {}", name, cityName);
                Theater theater = new Theater();
                theater.setName(name);
                theater.setAddress(address);
                theater.setLat(lat);
                theater.setLng(lng);
                theater.setPhoto(getPhotoAsBase64(photoUrl));
                theater.setCity(city);
                theaterRepo.save(theater);
                count++;
            }
        }

        log.info("Imported {} new theaters", count);
    }

    // ----------------- Concert CSV -----------------
    @SneakyThrows
    public static void importConcerts(InputStream csvStream, ConcertRepository concertRepo, TheaterRepository theaterRepo) throws Exception {
        int count = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String name = parts[0].trim();
            LocalDate date = LocalDate.parse(parts[1].trim());
            List<String> artists = Arrays.stream(parts[2].split(",")).map(String::trim).toList();
            String theaterName = parts[3].trim();
            //List<String> photos = Arrays.stream(parts[4].split(",")).map(String::trim).toList();

            Theater theater = theaterRepo.findByName(theaterName).orElseThrow(() -> new RuntimeException("Theater not found: " + theaterName));

            if (!concertRepo.existsByNameAndDateAndTheater(name, date, theater)) {
                Concert concert = new Concert();
                concert.setName(name);
                concert.setDate(date);
                concert.setArtists(artists);
                concert.setTheater(theater);
//                concert.setPhotos(
//                        photos.stream()
//                                .map(url -> {
//                                    try {
//                                        return CsvImportHelper.getPhotoAsBase64(url);
//                                    } catch (Exception e) {
//                                        throw new RuntimeException("Failed to fetch photo: " + url, e);
//                                    }
//                                })
//                                .toList()
//                );
                concertRepo.save(concert);
                count++;
            }
        }
        log.info("Imported {} new concerts", count);
    }

    private static String getPhotoAsBase64(String photoUrl) throws Exception {
        log.info("Fetching image from URL: {}", photoUrl);
        int fixedHeight = FIXED_HEIGHT; // Fixed height for all images
        // OkHttpClient to ignore SSL (your current Unsafe client)
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Request request = new Request.Builder()
                .url(photoUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            byte[] imageBytes = response.body().bytes();

            // Read image into BufferedImage
            InputStream is = new ByteArrayInputStream(imageBytes);
            BufferedImage original = ImageIO.read(is);

            // Calculate new width to keep aspect ratio
            int newHeight = fixedHeight;
            int newWidth = (int) (original.getWidth() * ((double) newHeight / original.getHeight()));

            // Resize image
            Image scaled = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(scaled, 0, 0, null);
            g2d.dispose();

            // Convert resized image to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

            return "data:image/png;base64," + base64Image;
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

}
