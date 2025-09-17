import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker } from "react-leaflet";
import groupByLevel from "../utils/groupByLevel";
import { SyncZoom } from "./SyncZoom";
import GalleryModal from "./GalleryModal";
import { createCheckinIcon } from "../utils/leafletIcon";

export default function MapView() {
  const [concerts, setConcerts] = useState([]);
  const [zoomLevel, setZoomLevel] = useState("country");
  const [mapCenter, setMapCenter] = useState([20, 0]);
  const [mapZoom, setMapZoom] = useState(3);

  const [modalOpen, setModalOpen] = useState(false);
  const [theaterConcerts, setTheaterConcerts] = useState([]);
  const BASE_URL = process.env.REACT_APP_API_BASE_URL;
  useEffect(() => {
    fetch(`${BASE_URL}/api/concerts`)
      .then((res) => res.json())
      .then((data) => setConcerts(data))
      .catch(console.error);
  }, []);

  const dataToShow = groupByLevel(concerts, zoomLevel);

  return (
    <>
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

          if (!first.theater || first.theater.lat == null || first.theater.lng == null) return null;

          return (
            <Marker
              key={key}
              position={[first.theater.lat, first.theater.lng]}
              icon={createCheckinIcon(count)}
              eventHandlers={{
                click: (e) => {
                  const map = e.target._map;

                  if (zoomLevel === "country") {
                    setZoomLevel("city");
                    setMapCenter([first.theater.lat, first.theater.lng]);
                    setMapZoom(6);
                    map.flyTo([first.theater.lat, first.theater.lng], 6);
                  } else if (zoomLevel === "city") {
                    setZoomLevel("theater");
                    setMapCenter([first.theater.lat, first.theater.lng]);
                    setMapZoom(10);
                    map.flyTo([first.theater.lat, first.theater.lng], 10);
                  } else if (zoomLevel === "theater") {
                    setZoomLevel("theaterDetails");
                    setMapCenter([first.theater.lat, first.theater.lng]);
                    setMapZoom(16);
                    map.flyTo([first.theater.lat, first.theater.lng], 18);
                  } else if (zoomLevel === "theaterDetails") {
                    setTheaterConcerts(items);
                    setModalOpen(true);
                  }
                },
                mouseover: (e) => {
                  if (zoomLevel === "theaterDetails") {
                    const popup = e.target.bindPopup(
                      `<div style="text-align:center; max-width:250px;">
                        ${first.theater.photo ? `<img src="${first.theater.photo}" style="width:100%; border-radius:8px;" />` : ""}
                        <div style="margin-top:5px;">${first.theater.name}</div>
                      </div>`,
                      { maxWidth: 300, minWidth: 200, autoPan: true }
                    );
                    popup.openPopup();
                  }
                },
                mouseout: (e) => {
                  e.target.closePopup();
                },
              }}
            />
          );
        })}
      </MapContainer>

      <GalleryModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        concerts={theaterConcerts}
      />
    </>
  );
}
