export default function groupByLevel(concerts, level) {
  const grouped = {};

  (concerts || []).forEach((c) => {
    let key;

    switch (level) {
      case "country":
        key = c.theater?.city?.country?.name;
        break;
      case "city":
        key = c.theater?.city?.name;
        break;
      case "theater":
        key = c.theater?.name;
        break;
      case "theaterDetails":
        key = c.theater?.name;
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
