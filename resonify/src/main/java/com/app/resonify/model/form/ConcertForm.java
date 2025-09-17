package com.app.resonify.model.form;

import com.app.resonify.model.Concert;
import com.app.resonify.model.ConcertPhoto;
import com.app.resonify.model.enums.ConcertType;
import com.app.resonify.repository.TheaterRepository;
import com.app.resonify.utils.PhotoHelper;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.app.resonify.utils.PhotoHelper.isBase64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConcertForm {
    private UUID id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String ticket;
    private String artists;
    private List<ConcertPhoto> photos = new ArrayList<>();
    private List<UUID> photoIds = new ArrayList<>();
    private List<String> photoBase64 = new ArrayList<>();

    private List<UUID> photoDeleted = new ArrayList<>();// existing photo IDs as Strings
    private List<String> photoUrls = new ArrayList<>(); // URLs for photos (existing + new)
    private UUID theaterId;
    private ConcertType type;

    public List<ConcertPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ConcertPhoto> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(List<String> photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public List<UUID> getPhotoDeleted() {
        return photoDeleted;
    }

    public void setPhotoDeleted(List<UUID> photoDeleted) {
        this.photoDeleted = photoDeleted;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public List<UUID> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(List<UUID> photoIds) {
        this.photoIds = photoIds;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public UUID getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(UUID theaterId) {
        this.theaterId = theaterId;
    }

    public ConcertType getType() {
        return type;
    }

    public void setType(ConcertType type) {
        this.type = type;
    }

    public static ConcertForm fromEntity(Concert concert) {
        ConcertForm form = new ConcertForm();
        form.setId(concert.getId());
        form.setName(concert.getName());
        form.setDate(concert.getDate());
        form.setTicket(concert.getTicket());
        form.setType(concert.getType());
        form.setTheaterId(concert.getTheater() != null ? concert.getTheater().getId() : null);
        form.setArtists(concert.getArtists() != null ? String.join(",", concert.getArtists()) : "");

        if (concert.getPhotos() != null) {
            form.setPhotos(new ArrayList<>(concert.getPhotos()));
            form.setPhotoIds(new ArrayList<>(concert.getPhotos() != null ?
                    concert.getPhotos().stream()
                            .map(ConcertPhoto::getId)
                            .filter(Objects::nonNull)
                            .toList()
                    : new ArrayList<>()));
            form.setPhotoBase64(new ArrayList<>(concert.getPhotos() != null ?
                    concert.getPhotos().stream()
                            .map(ConcertPhoto::getPhoto)
                            .toList()
                    : new ArrayList<>()));
        }
        return form;
    }

    /**
     * Update the Concert entity's fields including photos.
     * Behavior:
     * - If a submitted photoId exists in concert.getPhotos(), update that entity's URL and keep it.
     * - If a submitted photo has no id (blank/empty), create a new ConcertPhoto.
     * - Any existing ConcertPhoto not referenced by submitted photoIds will be removed.
     */
    public void updateEntity(Concert concert, TheaterRepository theaterRepository) throws IOException {
        concert.setName(this.name);
        concert.setDate(this.date);
        concert.setTicket(this.ticket);
        concert.setType(this.type);

        if (this.artists != null && !this.artists.isBlank()) {
            concert.setArtists(new ArrayList<>(Arrays.stream(this.artists.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList()));
        } else {
            concert.setArtists(new ArrayList<>());
        }

        if (this.theaterId != null) {
            concert.setTheater(theaterRepository.findById(this.theaterId).orElse(null));
        }

        // --- photo merge logic ---
        // 1. Remove deleted photos
        if (this.getPhotoDeleted() != null) {
            concert.getPhotos().removeIf(p -> this.getPhotoDeleted().contains(p.getId()));
        }

        List<ConcertPhoto> updatedPhotos = new ArrayList<>();

        // 2. Add new photos from URLs
        if (this.getPhotoUrls() != null) {
            for (String url : this.getPhotoUrls()) {
                String base64 = PhotoHelper.getPhotoAsBase64(url);
                if (isBase64(base64)) {
                    ConcertPhoto currentPhoto = new ConcertPhoto();
                    currentPhoto.setPhoto(base64);
                    currentPhoto.setConcert(concert);
                    updatedPhotos.add(currentPhoto);
                }
            }
        }

        concert.getPhotos().addAll(updatedPhotos);
    }
}
