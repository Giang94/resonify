package com.app.resonify;

import com.app.resonify.repository.*;
import com.app.resonify.utils.CsvImportHelper;
import com.app.resonify.utils.Seeders;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;


@Log4j2
@SpringBootApplication
public class ResonifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResonifyApplication.class, args);
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