package com.app.resonify.utils;

import com.app.resonify.model.*;
import com.app.resonify.repository.*;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Log4j2
public class Seeders {

//    private void seedConcerts(ConcertRepository concertRepo, TheaterRepository theaterRepo) throws Exception {
//        concertRepo.deleteAll();
//        if (concertRepo.findByNameContainingIgnoreCase("Barbiere").isEmpty()) {
//            Theater theater = theaterRepo.findByNameContainingIgnoreCase("Scala").getFirst();
//            Concert concert = Concert.builder()
//                    .name("Il Barbiere di Siviglia")
//                    .date(LocalDate.parse("2023-09-06"))
//                    .theater(theater)
//                    .photos(List.of(getPhotoAsBase64("https://www.teatroallascala.org/static/upload/bar/barbiere-704189badg--ph-brescia-e-amisano---teatro-alla-scala.preset-1920x1080wp.jpg")))
//                    .build();
//            concertRepo.save(concert);
//        }
//
//        if (concertRepo.findByNameContainingIgnoreCase("Traviata").isEmpty()) {
//            Theater theater = theaterRepo.findByNameContainingIgnoreCase("Verona").getFirst();
//            Concert concert = Concert.builder()
//                    .name("La Traviata")
//                    .date(LocalDate.parse("2023-09-06"))
//                    .theater(theater)
//                    .artists(List.of("Anna Netrebko", "Freddie de Tommaso"))
//                    .photos(List.of(getPhotoAsBase64("https://www.opera-online.com/media/images/picture/production_locale/0001/5637/12686/xl_la-traviata_arena-di-verona_090923_ennevifoto_0277.jpg")))
//                    .build();
//            concertRepo.save(concert);
//        }
//
//        if (concertRepo.findByNameContainingIgnoreCase("Aida").isEmpty()) {
//            Theater theater = theaterRepo.findByNameContainingIgnoreCase("Verona").getFirst();
//            Concert concert = Concert.builder()
//                    .name("Aida")
//                    .date(LocalDate.parse("2023-09-03"))
//                    .theater(theater)
//                    .artists(List.of("Elena Stikhina"))
//                    .photos(List.of(getPhotoAsBase64("https://www.arena.it/site/assets/files/1118/aida-arena-di-verona.jpg")))
//                    .build();
//            concertRepo.save(concert);
//        }
//    }

    private String getPhotoAsBase64(String photoUrl) throws Exception {
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
            return "data:image/jpeg;base64," + base64Image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Unsafe OkHttpClient that trusts all certificates
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

    private void seedTheaters(TheaterRepository theaterRepo, CityRepository cityRepository) throws Exception {
        if (theaterRepo.findByNameContainingIgnoreCase("Scala").isEmpty()) {
            Theater laScala = Theater.builder()
                    .name("Teatro alla Scala")
                    .address("Via Filodrammatici, 2, 20121 Milano MI, Italy")
                    .lat(45.46740678287658)
                    .lng(9.189555766536706)
                    .city(cityRepository.findByName("Milan").orElse(null))
                    .photo("data:image/jpeg;base64,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")
                    .build();
            theaterRepo.save(laScala);
        }

        if (theaterRepo.findByNameContainingIgnoreCase("Verona").isEmpty()) {
            Theater laScala = Theater.builder()
                    .name("Arena di Verona")
                    .address("Via Filodrammatici, 2, 20121 Milano MI, Italy")
                    .lat(45.439002207466814)
                    .lng(10.994355895836454)
                    .city(cityRepository.findByName("Verona").orElse(null))
                    .photo(getPhotoAsBase64("https://d3s3zh7icgjwgd.cloudfront.net/AcuCustom/Sitename/DAM/069/arena-di-verona_Main.jpg"))
                    .build();
            theaterRepo.save(laScala);
        }
    }

    private void seedCities(CityRepository cityRepository, CountryRepository countryRepo) {
        if (cityRepository.findByName("Milan").isEmpty())
            cityRepository.save(new City(null, "Milan", countryRepo.findByName("Italy").orElse(null)));
        if (cityRepository.findByName("Bangkok").isEmpty())
            cityRepository.save(new City(null, "Bangkok", countryRepo.findByName("Thailand").orElse(null)));
        if (cityRepository.findByName("Verona").isEmpty())
            cityRepository.save(new City(null, "Verona", countryRepo.findByName("Italy").orElse(null)));
        if (cityRepository.findByName("Salzburg").isEmpty())
            cityRepository.save(new City(null, "Salzburg", countryRepo.findByName("Austria").orElse(null)));
    }

    public static void seedCountriesFromCSV(CountryRepository countryRepo, ContinentRepository continentRepo) {
        log.info("Starting to seed countries from CSV");
        List<Continent> continents = continentRepo.findAll();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("C:\\Users\\nhgiang\\Downloads\\archive\\countries.csv"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String continentName = data[0].replace("\uFEFF", "");
                String countryName = data[1];

                Optional<Country> existingCountry = countryRepo.findByName(countryName);

                if (existingCountry.isPresent()) {
                    log.info("Country " + countryName + " already exists, skipping.");
                    continue;
                }
                Continent continent = continents.stream()
                        .filter(c -> {
                            String cName = c.getName().trim();
                            return cName.equalsIgnoreCase(continentName.trim());
                        })
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Continent " + continentName + " not found"));

                log.info("Saving country: " + countryName + " in continent: " + continentName);
                countryRepo.save(new Country(countryName, continent));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void seedContinents(ContinentRepository continentRepo) {
        log.info("Starting to seed continents");
        if (continentRepo.count() == 0) {
            continentRepo.save(new Continent("Africa"));
            continentRepo.save(new Continent("Asia"));
            continentRepo.save(new Continent("Europe"));
            continentRepo.save(new Continent("North America"));
            continentRepo.save(new Continent("South America"));
            continentRepo.save(new Continent("Oceania"));
            continentRepo.save(new Continent("Antarctica"));
            log.info("Continents seeded");
        } else {
            List<Continent> continents = continentRepo.findAll();
            for (Continent continent : continents) {
                log.info("Existing continent: " + continent.getName());
            }
            log.info("Continents already exist, skipping seeding");
        }
    }
}
