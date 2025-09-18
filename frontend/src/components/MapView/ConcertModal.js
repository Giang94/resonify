import React, { useState, useEffect } from "react";
import "./ConcertModal.css";

const BASE_URL = process.env.REACT_APP_API_BASE_URL;
const defaultArtistPhoto = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQACWAJYAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAkACQDASIAAhEBAxEB/8QAGAABAQEBAQAAAAAAAAAAAAAAAAcFBgP/xAAkEAACAgICAgICAwAAAAAAAAABAgADBBEFBhIhMUGxwRNRYf/EABYBAQEBAAAAAAAAAAAAAAAAAAABAv/EABURAQEAAAAAAAAAAAAAAAAAAAAB/9oADAMBAAIRAxEAPwCuREx+x8w3D8cLKlDX2t4V+XwPWyTNstiJwHEdtz15CtM60XUWMFbagFNnWxr8Tv4CIiAmJ2biLeX45Fx9G+l/NVJ15DWiJtMyojOxAVRsk/Qk35rs2XyV7pj2vRiA6VUOiw/tj+og9uK6pyVnIVNl47UUI4Z2cj2Ad6GpQ/uSDHzcrFtFlGTbW4+1cyhda508xjPXeAMqnXnr0HH0wH5ipG7ERCs3sDFOvZ5U6P8ACR+pK4iWJSdD0tivYQAfTUuD/vwYiBRYiJFf/9k=";

const ConcertModal = ({ concert, onClose }) => {
  const [active, setActive] = useState(0);
  const [fullConcert, setFullConcert] = useState(null);

  // Fetch full concert details when modal opens
  useEffect(() => {
    if (!concert) return; // just guard here
    const fetchConcertDetails = async () => {
      try {
        const res = await fetch(`${BASE_URL}/api/concerts/${concert.id}`);
        const data = await res.json();
        setFullConcert(data);
      } catch (err) {
        console.error("Failed to fetch concert details", err);
      }
    };

    fetchConcertDetails();
  }, [concert]);

  // If no concert, don't render anything
  if (!concert) return null;

  const displayConcert = fullConcert || concert;
  const photos = displayConcert.photos || [];

  const prev = () =>
    setActive((a) => (a - 1 + photos.length) % photos.length);
  const next = () => setActive((a) => (a + 1) % photos.length);

  return (
    <div
      className="concert-modal-overlay"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          e.stopPropagation();
          onClose();
        }
      }}
      role="dialog"
      aria-modal="true"
    >
      <div className="concert-modal" onClick={(e) => e.stopPropagation()}>
        <button
          className="concert-modal-close"
          onClick={onClose}
          aria-label="Close"
        >
          ✕
        </button>

        <header className="concert-modal-header">
          <h2 className="concert-title">
            {displayConcert.type}: {displayConcert.name}
          </h2>
          <div className="concert-meta">
            {displayConcert.date}{" "}
            {displayConcert.venue ? `• ${displayConcert.venue}` : ""}
          </div>

          <aside className="concert-details">
            <p>
              <strong>Theater:</strong> {displayConcert.theater?.name || "—"}
            </p>
            <p>
              <strong>Artists:</strong>{" "}
              <ul className="artist-list" style={{ padding: 0, margin: 0, listStyle: "none" }}>
                {displayConcert.artists && displayConcert.artists.length > 0 ? (
                  displayConcert.artists.map((artist) => (
                    <li key={artist.id} className="artist-item" style={{ padding: "0.25rem"}}>
                      <img
                        src={artist.photo || defaultArtistPhoto}
                        alt={artist.name}
                        width={36}
                        height={36}
                        style={{
                          borderRadius: "50%",
                          objectFit: "cover",
                          marginRight: "0.5rem",
                          verticalAlign: "middle",
                        }}
                      />
                      <span>{artist.name}</span>
                    </li>
                  ))
                ) : (
                  <li>N/A</li>
                )}
              </ul>
            </p>
          </aside>
        </header>

        <div className="concert-modal-body">
          <div className="concert-gallery">
            <div className="concert-main">
              {photos.length ? (
                <img
                  src={photos[active]}
                  alt={`${displayConcert.type}: ${displayConcert.name} ${active + 1
                    }`}
                  loading="lazy"
                />
              ) : (
                <div className="no-photo">No photos</div>
              )}
            </div>

            {photos.length > 1 && (
              <div className="concert-thumbs-row">
                <button
                  className="thumb-nav"
                  onClick={prev}
                  aria-label="Previous photo"
                >
                  ‹
                </button>
                <div className="thumb-list">
                  {photos.map((p, i) => (
                    <button
                      key={i}
                      className={`thumb ${i === active ? "active" : ""}`}
                      onClick={() => setActive(i)}
                      aria-label={`Show photo ${i + 1}`}
                    >
                      <img src={p} alt={`thumb ${i + 1}`} loading="lazy" />
                    </button>
                  ))}
                </div>
                <button
                  className="thumb-nav"
                  onClick={next}
                  aria-label="Next photo"
                >
                  ›
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConcertModal;
