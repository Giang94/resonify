import React, { useState } from "react";
import "./ConcertModal.css";

const ConcertModal = ({ concert, onClose }) => {
  const [active, setActive] = useState(0);

  // If no concert, don't render anything
  if (!concert) return null;

  const photos = concert.photos || [];

  const prev = () => setActive((a) => (a - 1 + photos.length) % photos.length);
  const next = () => setActive((a) => (a + 1) % photos.length);

  return (
    <div
      className="concert-modal-overlay"
      onClick={(e) => {
        // Only dismiss modal if clicking the overlay itself
        if (e.target === e.currentTarget) {
          e.stopPropagation(); // Stop click from reaching map container
          onClose();
        }
      }}
      role="dialog"
      aria-modal="true"
    >
      <div className="concert-modal" onClick={(e) => e.stopPropagation()}>
        <button className="concert-modal-close" onClick={onClose} aria-label="Close">✕</button>

        <header className="concert-modal-header">
          <h2 className="concert-title">{concert.type}: {concert.name}</h2>
          <div className="concert-meta">{concert.date} {concert.venue ? `• ${concert.venue}` : ""}</div>

          <aside className="concert-details">
            <p><strong>Theater:</strong> {concert.theater?.name || "—"}</p>
            <p><strong>Artist:</strong> {concert.artists?.join(", ") || "N/A"}</p>
          </aside>
        </header>

        <div className="concert-modal-body">
          <div className="concert-gallery">
            <div className="concert-main">
              {photos.length ? (
                <img src={photos[active].photo} alt={`${concert.type}: ${concert.name} ${active + 1}`} loading="lazy" />
              ) : (
                <div className="no-photo">No photos</div>
              )}
            </div>

            {photos.length > 1 && (
              <div className="concert-thumbs-row">
                <button className="thumb-nav" onClick={prev} aria-label="Previous photo">‹</button>
                <div className="thumb-list">
                  {photos.map((p, i) => (
                    <button
                      key={i}
                      className={`thumb ${i === active ? "active" : ""}`}
                      onClick={() => setActive(i)}
                      aria-label={`Show photo ${i + 1}`}
                    >
                      <img src={p.photo} alt={`thumb ${i + 1}`} loading="lazy" />
                    </button>
                  ))}
                </div>
                <button className="thumb-nav" onClick={next} aria-label="Next photo">›</button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConcertModal;
