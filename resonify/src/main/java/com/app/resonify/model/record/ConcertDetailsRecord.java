package com.app.resonify.model.record;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ConcertDetailsRecord(
        UUID id,
        String name,
        LocalDate date,
        String ticket,
        String type,
        TheaterRecord theater,
        List<ArtistRecord> artists,
        List<String> photos
) {
}