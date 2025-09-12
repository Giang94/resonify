import React, { useEffect, useState } from "react";
import "./BackgroundSlideshow.css";

const BackgroundGrid = () => {
  const [photos, setPhotos] = useState([]);
  const [grid, setGrid] = useState([]);
  const [dimensions, setDimensions] = useState({
    cols: 4,
    rows: 4
  });

  // Add window resize handler
  useEffect(() => {
    const handleResize = () => {
      const width = window.innerWidth;
      let cols;
      if (width < 640) cols = 2;      // mobile
      else if (width < 1024) cols = 3; // tablet
      else cols = 4;                   // desktop

      const rows = Math.ceil(16 / cols); // Maintain total cells around 16
      setDimensions({ cols, rows });
    };

    handleResize(); // Initial call
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Fetch photos
  useEffect(() => {
    fetch("http://localhost:8080/api/photos")
      .then((res) => res.json())
      .then((data) => {
        setPhotos(data);
        const totalCells = dimensions.cols * dimensions.rows;
        const initialGrid = Array.from(
          { length: totalCells },
          (_, i) => data[i % data.length]
        );
        setGrid(initialGrid);
      })
      .catch((err) => console.error("Failed to load photos", err));
  }, [dimensions]);

  // Update grid when dimensions change
  useEffect(() => {
    if (photos.length === 0) return;

    const totalCells = dimensions.cols * dimensions.rows;
    const newGrid = Array.from(
      { length: totalCells },
      (_, i) => photos[i % photos.length]
    );
    setGrid(newGrid);
  }, [dimensions, photos]);

  // Photo swap effect
  useEffect(() => {
    if (photos.length === 0) return;

    const interval = setInterval(() => {
      let howMany = Math.floor(Math.random() * 3) + 1;

      for (let i = 0; i < howMany; i++) {
        setTimeout(() => {
          setGrid((prev) => {
            const newGrid = [...prev];
            const cellIndex = Math.floor(Math.random() * newGrid.length);
            const newPhoto = photos[Math.floor(Math.random() * photos.length)];
            newGrid[cellIndex] = newPhoto;
            return newGrid;
          });
        }, i * 300);
      }
    }, 10000);

    return () => clearInterval(interval);
  }, [photos]);

  return (
    <div
      className="absolute inset-0 grid grid-cols-4 grid-rows-3 w-full h-full bg-grid"
      style={{
        gridTemplateColumns: `repeat(${dimensions.cols}, 1fr)`,
        gridTemplateRows: `repeat(${dimensions.rows}, 1fr)`
      }}
    >
      {grid.map((photo, idx) => (
        <div
          key={idx}
          className="w-full h-full bg-cover bg-center transition-opacity duration-1000 grid-cell"
          style={{ backgroundImage: `url(${photo})` }}
        />
      ))}
    </div>
  );
};

export default BackgroundGrid;
