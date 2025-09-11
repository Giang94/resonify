import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker } from "react-leaflet";
import groupByLevel from "../utils/groupByLevel";
import { SyncZoom } from "./SyncZoom";
import { createCheckinIcon } from "../utils/leafletIcon";
import "./MapView.css";

export default function MapView() {
  const [concerts, setConcerts] = useState([]);
  const [zoomLevel, setZoomLevel] = useState("continent");
  const [mapCenter, setMapCenter] = useState([20, 0]);
  const [mapZoom, setMapZoom] = useState(3);

  const [theaterConcerts, setTheaterConcerts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/concerts")
      .then((res) => res.json())
      .then((data) => setConcerts(data))
      .catch(console.error);
  }, []);

  const dataToShow = groupByLevel(concerts, zoomLevel);

  // Panel open if theaterConcerts is set and zoomLevel is theaterDetails
  const panelOpen = zoomLevel === "theaterDetails" && theaterConcerts.length > 0;

  // Handler to close the panel
  const handleMapClick = () => {
    if (panelOpen) {
      setZoomLevel("theater");
      setTheaterConcerts([]);
    }
  };

  return (
    <div className={`mapview-container`}>
      {/* Only render panel if open */}
      {panelOpen && (
        <div className="concert-panel open">
          <h2>Concerts</h2>
          {theaterConcerts.map((concert) => (
            <div className="concert-item" key={concert.id}>
              <h3>{concert.name}</h3>
              <div>{concert.date}</div>
              <small>{concert.artist}</small>
            </div>
          ))}
        </div>
      )}

      {/* Map container, shifted when panel is open */}
      <div
        className={`map-container${panelOpen ? " shifted" : ""}`}
        onClick={handleMapClick}
      >
        <MapContainer
          worldCopyJump={true}
          center={mapCenter}
          zoom={mapZoom}
          minZoom={3}
          maxZoom={18}
          maxBounds={[
            [-85, -180],
            [85, 180],
          ]}
          style={{ height: "100vh", width: "100%" }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a>'
          />

          <SyncZoom setZoomLevel={setZoomLevel} setMapZoom={setMapZoom} />

          {Object.entries(dataToShow).map(([key, items]) => {
            const first = items[0];
            const count = items.length;

            if (
              !first.theater ||
              first.theater.lat == null ||
              first.theater.lng == null
            )
              return null;

            return (
              <Marker
                key={key}
                position={[first.theater.lat, first.theater.lng]}
                icon={createCheckinIcon(count)}
                eventHandlers={{
                  click: (e) => {
                    const map = e.target._map;

                    if (zoomLevel === "continent") {
                      setZoomLevel("country");
                      setMapCenter([first.theater.lat, first.theater.lng]);
                      setMapZoom(4);
                      map.flyTo([first.theater.lat, first.theater.lng], 4);
                    } else if (zoomLevel === "country") {
                      setZoomLevel("city");
                      setMapCenter([first.theater.lat, first.theater.lng]);
                      setMapZoom(7);
                      map.flyTo([first.theater.lat, first.theater.lng], 7);
                    } else if (zoomLevel === "city") {
                      setZoomLevel("theater");
                      setMapCenter([first.theater.lat, first.theater.lng]);
                      setMapZoom(12);
                      map.flyTo([first.theater.lat, first.theater.lng], 12);
                    } else if (zoomLevel === "theater") {
                      setZoomLevel("theaterDetails");
                      setMapCenter([first.theater.lat, first.theater.lng]);
                      setMapZoom(18);
                      map.flyTo([first.theater.lat, first.theater.lng], 18);
                      setTheaterConcerts(items); // Set concerts for panel
                    } else if (zoomLevel === "theaterDetails") {
                      setTheaterConcerts(items);
                    }
                  },
                  mouseover: (e) => {
                    let popupContent = '';

                    if (zoomLevel === "continent") {
                      popupContent = `<div style="text-align:center;">${first.theater.city.country.continent.name}</div>`;
                    } else if (zoomLevel === "country") {
                      popupContent = `<div style="text-align:center;">${first.theater.city.country.name}</div>`;
                    } else if (zoomLevel === "city") {
                      popupContent = `<div style="text-align:center;">${first.theater.city.name}</div>`;
                    } else if (zoomLevel === "theater" || zoomLevel === "theaterDetails") {
                      popupContent = `
                      <div style="text-align:center; max-width:250px;">
                        ${first.theater.photo
                          ? `<img src="${first.theater.photo}" style="width:100%; border-radius:8px;" />`
                          : ""
                        }
                        <div style="margin-top:5px;">${first.theater.name}</div>
                      </div>`;
                    }

                    const popup = e.target.bindPopup(popupContent, {
                      maxWidth: 300,
                      minWidth: 200,
                      autoPan: true
                    });
                    popup.openPopup();
                  },
                  mouseout: (e) => {
                    e.target.closePopup();
                  },
                }}
              />
            );
          })}
        </MapContainer>
      </div>
    </div>
  );
}