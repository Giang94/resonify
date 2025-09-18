import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, ZoomControl, GeoJSON, Marker } from "react-leaflet";
import groupByLevel from "../utils/groupByLevel";
import { SyncZoom } from "./SyncZoom";
import { createCheckinIcon } from "../utils/leafletIcon";
import MapResetButton from "./MapResetButton";
import "./MapView.css";
import ConcertModal from "./ConcertModal";
import worldGeoJson from "../data/world.geo.json";
import { styleCountry, onEachCountry } from "../utils/mapColoring";

const BASE_URL = process.env.REACT_APP_API_BASE_URL;

export default function MapView() {
  const [concerts, setConcerts] = useState([]);
  const [zoomLevel, setZoomLevel] = useState("country");
  const [mapCenter, setMapCenter] = useState([20, 0]);
  const [mapZoom, setMapZoom] = useState(3);

  const [theaterConcerts, setTheaterConcerts] = useState([]);
  const [selectedConcert, setSelectedConcert] = useState(null);
  const [expandedYears, setExpandedYears] = useState({});

  // Fetch summary data
  useEffect(() => {
    fetch(`${BASE_URL}/api/concerts/summary`)
      .then((res) => res.json())
      .then((data) => {
        const sorted = data.sort(
          (b, a) => new Date(a.date) - new Date(b.date) // newest first
        );
        setConcerts(sorted);
      })
      .catch(console.error);
  }, []);

  // Count concerts per country for coloring
  const concertCountsByCountry = concerts.reduce((acc, concert) => {
    const countryName = concert.countryName;
    if (!acc[countryName]) acc[countryName] = 0;
    acc[countryName]++;
    return acc;
  }, {});

  const dataToShow = groupByLevel(concerts, zoomLevel);

  const panelOpen = theaterConcerts.length > 0;

  const handlePanelClose = () => {
    setTheaterConcerts([]);
  };

  const handleMapClick = (e) => {
    if (
      e.target.classList.contains("leaflet-control") ||
      e.target.closest(".leaflet-control") ||
      e.target.classList.contains("leaflet-marker-icon")
    ) {
      return;
    }
    if (panelOpen) {
      handlePanelClose();
    }
  };

  const toggleYear = (year) => {
    setExpandedYears((prev) => ({
      ...prev,
      [year]: !prev[year],
    }));
  };

  // Group theater concerts by year
  const concertsByYear = theaterConcerts.reduce((acc, concert) => {
    const year = new Date(concert.date).getFullYear();
    if (!acc[year]) acc[year] = [];
    acc[year].push(concert);
    return acc;
  }, {});

  useEffect(() => {
    const allYears = concerts.reduce((acc, c) => {
      const year = new Date(c.date).getFullYear();
      acc[year] = true;
      return acc;
    }, {});
    setExpandedYears(allYears);
  }, [concerts]);

  return (
    <div className="mapview-container">
      {panelOpen && (
        <div className="concert-panel open">
          <h2>Concerts at {theaterConcerts[0].theaterName}</h2>

          {Object.keys(concertsByYear)
            .sort((a, b) => b - a)
            .map((year) => {
              const yearConcerts = concertsByYear[year];

              return (
                <div key={year} className="year-section">
                  <div className="year-header" onClick={() => toggleYear(year)}>
                    <span className="year-label">
                      {year} ({yearConcerts.length})
                    </span>
                    <span className="year-toggle">
                      {expandedYears[year] ? "▴" : "▾"}
                    </span>
                  </div>

                  {expandedYears[year] && (
                    <div className="concert-list">
                      {yearConcerts.map((concert) => (
                        <div
                          className="concert-item"
                          key={concert.id}
                          style={{
                            background: concert.photo
                              ? `linear-gradient(rgba(42,42,61,0.3), rgba(42,42,61,0.3)), url(${concert.photo}) center/cover no-repeat`
                              : "#2a2a3d",
                          }}
                          onClick={() => setSelectedConcert(concert)}
                        >
                          <h3>
                            {concert.type}: {concert.name}
                          </h3>
                          <div>{concert.date}</div>
                          <small>
                            {concert.artists?.map((artist) => (
                              <span key={artist.id} style={{ marginRight: "0.5rem" }}>
                                {artist.photo && (
                                  <img
                                    src={artist.photo}
                                    alt={artist.name}
                                    style={{
                                      width: "24px",
                                      height: "24px",
                                      borderRadius: "50%",
                                      objectFit: "cover",
                                      marginRight: "0.25rem",
                                      verticalAlign: "middle",
                                    }}
                                  />
                                )}
                                {artist.name}
                              </span>
                            ))}
                          </small>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
        </div>
      )}

      <div
        className={`map-container${panelOpen ? " shifted" : ""}`}
        onClick={handleMapClick}
      >
        <MapContainer
          worldCopyJump
          center={mapCenter}
          zoom={mapZoom}
          minZoom={3}
          maxZoom={18}
          maxBounds={[
            [-85, -180],
            [85, 180],
          ]}
          style={{ height: "100vh", width: "100%" }}
          zoomControl={false}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a>'
          />

          <GeoJSON
            data={worldGeoJson}
            style={styleCountry(concertCountsByCountry)}
            onEachFeature={onEachCountry(concertCountsByCountry)}
          />

          <div style={{ zIndex: 1000 }}>
            <ZoomControl position="topright" />
          </div>

          <SyncZoom setZoomLevel={setZoomLevel} setMapZoom={setMapZoom} />
          <MapResetButton
            setZoomLevel={setZoomLevel}
            setMapZoom={setMapZoom}
            setMapCenter={setMapCenter}
            handlePanelClose={handlePanelClose}
          />

          {Object.entries(dataToShow).map(([key, items]) => {
            const first = items[0];
            const count = items.length;

            if (first.theaterLat == null || first.theaterLng == null) return null;

            // Function to fetch concerts for this theater
            const fetchTheaterConcerts = async (theaterId) => {
              try {
                const res = await fetch(`${BASE_URL}/api/theaters/${theaterId}/concerts`);
                const data = await res.json();
                setTheaterConcerts(data);
              } catch (err) {
                console.error("Failed to fetch theater concerts", err);
              }
            };

            return (
              <Marker
                key={key}
                position={[first.theaterLat, first.theaterLng]}
                icon={createCheckinIcon(count)}
                eventHandlers={{
                  click: (e) => {
                    const map = e.target._map;

                    if (zoomLevel === "country") {
                      setZoomLevel("city");
                      setMapCenter([first.theaterLat, first.theaterLng]);
                      setMapZoom(7);
                      map.flyTo([first.theaterLat, first.theaterLng], 7);
                    } else if (zoomLevel === "city") {
                      setZoomLevel("theater");
                      setMapCenter([first.theaterLat, first.theaterLng]);
                      setMapZoom(12);
                      map.flyTo([first.theaterLat, first.theaterLng], 12);
                    } else if (zoomLevel === "theater") {
                      setZoomLevel("theaterDetails");
                      setMapCenter([first.theaterLat, first.theaterLng]);
                      setMapZoom(18);
                      map.flyTo([first.theaterLat, first.theaterLng], 18);
                      fetchTheaterConcerts(first.theaterId); // <-- fetch here
                    }
                  },
                }}
              />
            );
          })}

        </MapContainer>

        {selectedConcert && (
          <ConcertModal
            concert={selectedConcert}
            onClose={() => setSelectedConcert(null)}
          />
        )}
      </div>
    </div>
  );
}
