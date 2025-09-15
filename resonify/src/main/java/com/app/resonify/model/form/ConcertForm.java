package com.app.resonify.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConcertForm {
    private UUID id;
    private String name;
    private LocalDate date;
    private String ticket;
    private String artists;
    private List<String> photos = new ArrayList<>();
}