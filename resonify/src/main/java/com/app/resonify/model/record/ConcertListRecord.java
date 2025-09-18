package com.app.resonify.model.record;

import com.app.resonify.model.enums.ConcertType;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertListRecord(
        UUID id,
        String name,
        LocalDate date,
        ConcertType type,
        String photo // first photo as base64 or URL
) {}