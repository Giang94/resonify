package com.app.resonify.model.form;

import com.app.resonify.model.Artist;
import com.app.resonify.model.Concert;
import com.app.resonify.model.ConcertPhoto;
import com.app.resonify.model.enums.ConcertType;
import com.app.resonify.repository.ArtistRepository;
import com.app.resonify.repository.TheaterRepository;
import com.app.resonify.utils.PhotoHelper;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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

    // --- changed: artists are no longer simple strings ---
    private List<String> artists = new ArrayList<>();

    private List<ConcertPhoto> photos = new ArrayList<>();
    private List<UUID> photoIds = new ArrayList<>();
    private List<String> photoBase64 = new ArrayList<>();
    private List<UUID> photoDeleted = new ArrayList<>();
    private List<String> photoUrls = new ArrayList<>();

    private UUID theaterId;
    private ConcertType type;

    public static ConcertForm fromEntity(Concert concert) {
        ConcertForm form = new ConcertForm();
        form.setId(concert.getId());
        form.setName(concert.getName());
        form.setDate(concert.getDate());
        form.setTicket(concert.getTicket());
        form.setType(concert.getType());
        form.setTheaterId(concert.getTheater() != null ? concert.getTheater().getId() : null);

        // --- map artists to IDs ---
        if (concert.getArtists() != null) {
            form.setArtists(
                    concert.getArtists().stream()
                            .map(Artist::getName)
                            .toList()
            );
        }

        if (concert.getPhotos() != null) {
            form.setPhotos(new ArrayList<>(concert.getPhotos()));
            form.setPhotoIds(
                    concert.getPhotos().stream()
                            .map(ConcertPhoto::getId)
                            .filter(Objects::nonNull)
                            .toList()
            );
            form.setPhotoBase64(
                    concert.getPhotos().stream()
                            .map(ConcertPhoto::getPhoto)
                            .toList()
            );
        }
        return form;
    }

    /**
     * Update Concert entity fields including artists and photos.
     */
    public void updateEntity(Concert concert,
                             TheaterRepository theaterRepository,
                             ArtistRepository artistRepository) throws IOException {
        concert.setName(this.name);
        concert.setDate(this.date);
        concert.setTicket(this.ticket);
        concert.setType(this.type);

        // --- update artists ---
        if (this.artists != null && !this.artists.isEmpty()) {
            List<Artist> resolvedArtists = new ArrayList<>();
            for (String artistName : this.artists) {
                Optional<Artist> existingArtist = artistRepository.findByNameIgnoreCase(artistName);
                if (existingArtist.isEmpty()) {
                    Artist newArtist = new Artist();
                    newArtist.setName(artistName);
                    artistRepository.save(newArtist);
                    resolvedArtists.add(newArtist);
                } else {
                    resolvedArtists.add(existingArtist.get());
                }
            }

            concert.setArtists(new HashSet<>(resolvedArtists));
        } else {
            concert.setArtists(new HashSet<>());
        }

        if (this.theaterId != null) {
            concert.setTheater(theaterRepository.findById(this.theaterId).orElse(null));
        }

        // --- photo merge logic ---
        // 1. Remove deleted photos
        if (this.getPhotoDeleted() != null) {
            concert.getPhotos().removeIf(p -> this.getPhotoDeleted().contains(p.getId()));
        }

        // 2. Add new photos from URLs
        if (this.getPhotoUrls() != null) {
            for (String url : this.getPhotoUrls()) {
                String base64 = PhotoHelper.getPhotoAsBase64(url);
                if (isBase64(base64)) {
                    ConcertPhoto currentPhoto = new ConcertPhoto();
                    currentPhoto.setPhoto(base64);
                    currentPhoto.setConcert(concert);
                    concert.getPhotos().add(currentPhoto);
                }
            }
        }
    }
}
