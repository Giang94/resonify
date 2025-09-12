import { Marker } from "react-leaflet";
import { createCheckinIcon } from "../utils/leafletIcon";

export default function ConcertMarker({
  groupKey, items, zoomLevel, setZoomLevel,
  setMapCenter, setMapZoom, setModalOpen, setTheaterConcerts
}) {
  const first = items[0];
  const count = items.length;

  const zoomMapping = { country: 4, city: 7, cityDetail: 12 };

  return (
    <Marker
      key={groupKey}
      position={[first.lat, first.lng]}
      icon={createCheckinIcon(count)}
      eventHandlers={{
        click: (e) => {
          const map = e.target._map;

          if (zoomLevel === "country") {
            setZoomLevel("city");
            setMapCenter([first.theater.lat, first.theater.lng]);
            setMapZoom(zoomMapping["city"]);
          } else if (zoomLevel === "city") {
            setZoomLevel("theater");
            setMapCenter([first.theater.lat, first.theater.lng]);
            setMapZoom(zoomMapping["theater"]);
          } else if (zoomLevel === "theater") {
            setMapCenter([first.theater.lat, first.theater.lng]);
            setMapZoom(zoomMapping["theaterDetail"]);
            setTheaterConcerts(items);
            setModalOpen(true);
          }
        },
      }}
    />
  );
}
