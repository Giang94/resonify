package com.app.resonify.model.record;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertSummaryRecord(
        UUID id,
        String name,
        LocalDate date,
        String type,
        UUID theaterId,
        String theaterName,
        double theaterLat,
        double theaterLng,
        String theaterPhoto,
        UUID cityId,
        String cityName,
        UUID countryId,
        String countryName,
        UUID continentId,
        String continentName
) {}