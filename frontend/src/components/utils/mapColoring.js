// src/utils/mapColoring.js

// Step-based color based on concert count
export const getCountryColor = (concertCountsByCountry, countryName) => {
  const count = concertCountsByCountry[countryName] || 0;

  if (count === 0) return "#eeeeee";      // gray for no concerts
  if (count < 5) return "#FFFF99";        // light yellow
  if (count < 10) return "#fabd42ff";     // orange
  return "#ff3333";                        // red for 10+ concerts
};

// GeoJSON style function for a country
export const styleCountry = (concertCountsByCountry) => (feature) => ({
  fillColor: getCountryColor(concertCountsByCountry, feature.properties.name),
  weight: 1,
  color: "white",
  fillOpacity: 0.7,
});

// Optional: bind popup for each country
export const onEachCountry = (concertCountsByCountry) => (feature, layer) => {
  const count = concertCountsByCountry[feature.properties.name] || 0;
  if (count > 0) {
    layer.bindPopup(`${feature.properties.name}: ${count} concert(s)`);
  }
};