export default function groupByLevel(concerts, level) {
  const grouped = {};

  (concerts || []).forEach((c) => {
    let key;

    switch (level) {
      case "country":
        key = c.countryName;
        break;
      case "city":
        key = c.cityName;
        break;
      case "theater":
        key = c.theaterName;
        break;
      case "theaterDetails":
        key = c.theaterName;
        break;
      default:
        key = "unknown";
    }

    if (!key) key = "unknown";

    if (!grouped[key]) grouped[key] = [];
    grouped[key].push(c);
  });

  return grouped;
}
