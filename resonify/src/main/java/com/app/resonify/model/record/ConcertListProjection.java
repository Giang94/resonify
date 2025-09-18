package com.app.resonify.model.record;

import com.app.resonify.model.enums.ConcertType;

import java.time.LocalDate;
import java.util.UUID;

public interface ConcertListProjection {
    UUID getId();
    String getName();
    LocalDate getDate();
    ConcertType getType();
    String getPhoto(); // first photo only
}