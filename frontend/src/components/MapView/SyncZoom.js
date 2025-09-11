import { useEffect } from "react";
import { useMap } from "react-leaflet";

export function SyncZoom({ setZoomLevel, setMapZoom }) {
  const map = useMap();

  useEffect(() => {
    const handleZoom = () => {
      const z = map.getZoom();
      setMapZoom(z);

      if (z < 4) setZoomLevel("continent");
      else if (z < 7) setZoomLevel("country");
      else if (z < 12) setZoomLevel("city");
      else if (z < 18) setZoomLevel("theater");
      else setZoomLevel("theaterDetails")
    };

    map.on("zoomend", handleZoom);
    handleZoom();

    return () => {
      map.off("zoomend", handleZoom);
    };
  }, [map, setZoomLevel, setMapZoom]);

  return null;
}
