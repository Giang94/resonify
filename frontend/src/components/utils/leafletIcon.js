import L from "leaflet";
import "leaflet/dist/leaflet.css";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
  iconUrl: require("leaflet/dist/images/marker-icon.png"),
  shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

// Check-in style icon: blue circle with white border, shadow, and number inside
export const createCheckinIcon = (count) =>
  L.divIcon({
    html: `
      <div style="
        position: relative;
        width: 30px;
        height: 40px;
        background: linear-gradient(135deg, #d32f2f 70%, #ef5350 100%);
        border-radius: 50% 50% 0 50%;
        transform: rotate(45deg);
        box-shadow: 0 2px 8px rgba(211, 47, 47, 0.3);
        border: 3px solid #fff;
      ">
        <div style="
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          transform: rotate(-45deg) translate(0px, -2px);
        ">
          <span style="
            color: #fff;
            font-weight: bold;
            font-style: italic;
            font-size: 16px;
            text-shadow: 0 1px 4px rgba(0,0,0,0.25);
          ">${count > 9 ? '9+' : count}</span>
        </div>
      </div>
    `,
    className: "",
    iconSize: [30, 40],
    iconAnchor: [15, 40],
    popupAnchor: [0, -40],
  });