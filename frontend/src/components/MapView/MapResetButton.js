import { useMap } from 'react-leaflet';

export default function MapResetButton({ setZoomLevel, setMapZoom, setMapCenter, handlePanelClose }) {
  const map = useMap();

  const resetMap = () => {
    // Reset to initial values
    setZoomLevel("country");
    setMapZoom(3);
    setMapCenter([20, 0]);
    handlePanelClose();
    map.flyTo([20, 0], 3);
  };

  return (
    <div className="leaflet-control-container">
      <div className="leaflet-top leaflet-right">
        <div className="leaflet-control-zoom leaflet-bar leaflet-control" style={{ marginTop: '85px' }}>
          <button
            className="leaflet-control-zoom-in"
            title="Reset Map"
            aria-label="Reset Map"
            onClick={resetMap}
            style={{
              fontSize: '16px',
              fontWeight: 'bold',
              border: 'none',
              width: '30px',
              height: '30px',
              lineHeight: '30px',
              cursor: 'pointer',
              backgroundColor: '#fff',
              padding: 0,
              display: 'block',
              textAlign: 'center'
            }}
          >
            ⟲
          </button>
        </div>
      </div>
    </div>
  );
}