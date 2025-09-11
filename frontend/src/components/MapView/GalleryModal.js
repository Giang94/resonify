import React, { useState } from "react";

export default function GalleryModal({ isOpen, onClose, concerts }) {
  const [index, setIndex] = useState(0);

  if (!isOpen || concerts.length === 0) return null;

  const current = concerts[index];
  const prev = () => setIndex((i) => (i > 0 ? i - 1 : concerts.length - 1));
  const next = () => setIndex((i) => (i < concerts.length - 1 ? i + 1 : 0));

  // Safe access nested properties
  const theater = current.theater || {};
  const city = theater.city?.name || "Unknown City";
  const country = theater.city?.country?.name || "Unknown Country";
  const date = current.date ? new Date(current.date).toLocaleDateString() : "Unknown Date";

  return (
    <div style={{
      position: "fixed", inset: 0, background: "rgba(0,0,0,0.7)",
      display: "flex", justifyContent: "center", alignItems: "center", zIndex: 2000
    }}>
      <div style={{
        background: "white", padding: 20, borderRadius: 8, width: "80%",
        maxWidth: 700, position: "relative"
      }}>
        <button
          onClick={onClose}
          style={{
            position: "absolute", top: 10, right: 10, background: "#f44336",
            color: "white", border: "none", borderRadius: "50%", width: 30, height: 30
          }}
        >✖</button>

        <h2>{current.name}</h2>
        <p>{theater.name} ({city}, {country}) — {date}</p>
        <p><strong>Artists:</strong> {current.artists?.join(", ") || "N/A"}</p>
        <p><strong>Ticket:</strong> {current.ticket || "N/A"}</p>

        <div style={{
          display: "flex", overflowX: "auto", gap: "10px", margin: "15px 0"
        }}>
          {current.photos?.map((p, i) => (
            <img key={i} src={p} alt={current.name}
              style={{ maxHeight: "200px", borderRadius: 6 }} />
          ))}
        </div>

        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <button onClick={prev}>⬅ Prev</button>
          <button onClick={next}>Next ➡</button>
        </div>
      </div>
    </div>
  );
}
