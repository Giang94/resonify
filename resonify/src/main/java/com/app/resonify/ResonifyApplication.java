package com.app.resonify;

import com.app.resonify.model.ConcertPhoto;
import com.app.resonify.model.Theater;
import com.app.resonify.repository.*;
import com.app.resonify.utils.CsvImportHelper;
import com.app.resonify.utils.PhotoHelper;
import com.app.resonify.utils.Seeders;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Log4j2
@SpringBootApplication
public class ResonifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResonifyApplication.class, args);
    }

    @Bean
    CommandLineRunner resizeTheaterPhotos(ConcertPhotoRepository repository) {
        return args -> {
            List<ConcertPhoto> photos = repository.findAll();
            List<String> failedId = new ArrayList<>();
            for (ConcertPhoto photo: photos) {
                String base64 = photo.getPhoto();
                try {
                    PhotoHelper.ResizedImageResult result = PhotoHelper.resizeBase64Image(base64, photo.getId());
                    String resized = result.getBase64();
                    int sizeBytes = (int) Math.ceil((resized.length() * 3) / 4.0);
                    photo.setPhoto(resized);
                    photo.setPhotoSize(sizeBytes);
                    repository.save(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                    failedId.add("\n"+photo.getId());
                    log.error("Failed to decode Base64 image with ID: " + photo.getId());
                }
            }
            log.info("Processing {}, failed {}, list {}", photos.size(), failedId.size(), failedId);
        };
    }

    @Bean
    CommandLineRunner seedData(@Value("${app.seed-data:false}") boolean seedDataEnabled,
                               CountryRepository countryRepo,
                               ContinentRepository continentRepo,
                               CityRepository cityRepo,
                               TheaterRepository theaterRepo,
                               ConcertRepository concertRepo) {
        return args -> {
            if (!seedDataEnabled) {
                log.info("Seeding disabled via configuration, skipping seedData.");
                return;
            }

            log.info("Seeding initial data...");
            Seeders.seedContinents(continentRepo);
            Seeders.seedCountriesFromCSV(countryRepo, continentRepo);

            try (FileInputStream cityStream = new FileInputStream("C:\\Users\\nhgiang\\Documents\\personal\\resonify\\resonify\\src\\main\\resources\\data\\cities.csv")) {
                CsvImportHelper.importCities(cityStream, cityRepo, countryRepo);
            }

            // Theaters
            try (FileInputStream theaterStream = new FileInputStream("C:\\Users\\nhgiang\\Documents\\personal\\resonify\\resonify\\src\\main\\resources\\data\\theaters.csv")) {
                CsvImportHelper.importTheaters(theaterStream, theaterRepo, cityRepo);
            }

            // Concerts
            try (FileInputStream concertStream = new FileInputStream("C:\\Users\\nhgiang\\Documents\\personal\\resonify\\resonify\\src\\main\\resources\\data\\concerts.csv")) {
                CsvImportHelper.importConcerts(concertStream, concertRepo, theaterRepo);
            }
        };
    }
}