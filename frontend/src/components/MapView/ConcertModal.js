import React, { useState, useEffect, useRef } from "react";
import "./ConcertModal.css";

const BASE_URL = process.env.REACT_APP_API_BASE_URL;
const defaultArtistPhoto = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQACWAJYAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAkACQDASIAAhEBAxEB/8QAGAABAQEBAQAAAAAAAAAAAAAAAAcFBgP/xAAkEAACAgICAgICAwAAAAAAAAABAgADBBEFBhIhMUGxwRNRYf/EABYBAQEBAAAAAAAAAAAAAAAAAAABAv/EABURAQEAAAAAAAAAAAAAAAAAAAAB/9oADAMBAAIRAxEAPwCuREx+x8w3D8cLKlDX2t4V+XwPWyTNstiJwHEdtz15CtM60XUWMFbagFNnWxr8Tv4CIiAmJ2biLeX45Fx9G+l/NVJ15DWiJtMyojOxAVRsk/Qk35rs2XyV7pj2vRiA6VUOiw/tj+og9uK6pyVnIVNl47UUI4Z2cj2Ad6GpQ/uSDHzcrFtFlGTbW4+1cyhda508xjPXeAMqnXnr0HH0wH5ipG7ERCs3sDFOvZ5U6P8ACR+pK4iWJSdD0tivYQAfTUuD/vwYiBRYiJFf/9k=";

const ConcertModal = ({ concert, onClose }) => {
  // -------------------- State & Refs --------------------
  const [active, setActive] = useState(0);
  const [fullConcert, setFullConcert] = useState(null);
  const [atGalleryPage, setAtGalleryPage] = useState(false);

  const bodyRef = useRef(null);
  const galleryRef = useRef(null);

  // -------------------- Fetch full concert --------------------
  useEffect(() => {
    if (!concert) return; // guard inside hook
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

  // -------------------- Scroll listener for arrow --------------------
  useEffect(() => {
    const handleScroll = () => {
      if (!bodyRef.current || !galleryRef.current) return;
      const scrollTop = bodyRef.current.scrollTop;
      const galleryTop = galleryRef.current.offsetTop;
      setAtGalleryPage(scrollTop >= galleryTop - 10); // 10px tolerance
    };

    const bodyEl = bodyRef.current;
    if (bodyEl) bodyEl.addEventListener("scroll", handleScroll);
    return () => bodyEl && bodyEl.removeEventListener("scroll", handleScroll);
  }, []);

  // -------------------- Early return for rendering --------------------
  if (!concert) return null;

  const displayConcert = fullConcert || concert;
  const photos = displayConcert.photos || [];

  // -------------------- Arrow click --------------------
  const scrollToGallery = () => {
    if (!bodyRef.current || !galleryRef.current) return;
    bodyRef.current.scrollTo({
      top: galleryRef.current.offsetTop,
      behavior: "smooth",
    });
  };

  const scrollToArtists = () => {
    if (!bodyRef.current) return;
    bodyRef.current.scrollTo({ top: 0, behavior: "smooth" });
  };

  const prev = () => setActive((a) => (a - 1 + photos.length) % photos.length);
  const next = () => setActive((a) => (a + 1) % photos.length);

  // -------------------- JSX --------------------
  return (
    <div
      className="concert-modal-overlay"
      onClick={(e) => e.target === e.currentTarget && onClose()}
      role="dialog"
      aria-modal="true"
    >
      <div
        className="concert-modal"
        style={{ position: "relative" }}
        onClick={(e) => e.stopPropagation()} // <-- Add this line
      >
        <button
          className="concert-modal-close"
          onClick={onClose}
          aria-label="Close"
        >
          ✕
        </button>

        {/* Header */}
        <header className="concert-modal-header">
          <h2 className="concert-title">
            {displayConcert.type === "VISIT"
              ? `${displayConcert.type}`
              : `${displayConcert.type}: ${displayConcert.name}`}
          </h2>
          <div className="concert-meta">
            {displayConcert.date}{" "}
            {displayConcert.theater?.name ? `• ${displayConcert.theater.name}` : ""}
          </div>
        </header>

        {/* Modal body */}
        <div
          className="concert-modal-body"
          ref={bodyRef}
          style={{ height: "400px", overflowY: "auto", scrollBehavior: "smooth" }} // Set fixed height
        >
          {/* Artists Page */}
          <div className="modal-page page-artists">
            <h3>Artists</h3>
            <ul
              className={`artist-grid cols-${displayConcert.artists && displayConcert.artists.length > 0 ? Math.min(displayConcert.artists.length, 3) : 1}`}
            >
              {displayConcert.artists && displayConcert.artists.length > 0 ? (
                displayConcert.artists.map((artist) => (
                  <li key={artist.id} className="artist-item">
                    <img
                      src={artist.photo || defaultArtistPhoto}
                      alt={artist.name}
                      loading="lazy"
                    />
                    <span>{artist.name}</span>
                  </li>
                ))
              ) : (
                <li className="no-photo">No artists available</li>
              )}
            </ul>
          </div>

          {/* Gallery Page */}
          <div
            ref={galleryRef}
            className="modal-page page-gallery"
            style={{ minHeight: "400px", display: "flex", flexDirection: "column", justifyContent: "flex-start" }}
          >
            <h3>Gallery</h3>
            {photos.length ? (
              <div className="concert-main">
                <img
                  src={photos[active]}
                  alt={`${displayConcert.type}: ${displayConcert.name} ${active + 1}`}
                  loading="lazy"
                />
              </div>
            ) : (
              <div className="no-photo">No photos</div>
            )}

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
        {/* Single floating scroll button */}
        <button
          className="scroll-toggle"
          onClick={atGalleryPage ? scrollToArtists : scrollToGallery}
          aria-label={atGalleryPage ? "Go to artists" : "Go to gallery"}
          style={{
            position: "absolute",
            right: 24,
            bottom: 24,
            zIndex: 20,
          }}
        >
          {atGalleryPage ? (
            // Up arrow SVG
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M16 14l-4-4-4 4" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          ) : (
            // Down arrow SVG
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M8 10l4 4 4-4" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          )}
        </button>
      </div>
    </div>
  );
};

export default ConcertModal;
