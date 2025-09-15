import React, { useEffect, useState } from "react";
import "./BackgroundSlideshow.css";

const BackgroundSlideshow = () => {
  const [photos, setPhotos] = useState([]);
  const [grid, setGrid] = useState([]);
  const [dimensions, setDimensions] = useState({ cols: 4, rows: 4 });

  // Responsive grid
  useEffect(() => {
    const handleResize = () => {
      const width = window.innerWidth;
      let cols = width < 640 ? 2 : width < 1024 ? 3 : 4;
      let rows = Math.ceil(12 / cols);
      setDimensions({ cols, rows });
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Fetch photos
  useEffect(() => {
    fetch("http://localhost:8080/api/photos")
      .then((res) => res.json())
      .then((data) => {
        setPhotos(data);
        const totalCells = dimensions.cols * dimensions.rows;
        setGrid(Array.from({ length: totalCells }, (_, i) => data[i % data.length]));
      })
      .catch((err) => console.error("Failed to load photos", err));
  }, [dimensions]);

  // Update grid when dimensions change
  useEffect(() => {
    if (!photos.length) return;
    const totalCells = dimensions.cols * dimensions.rows;
    setGrid(Array.from({ length: totalCells }, (_, i) => photos[i % photos.length]));
  }, [dimensions, photos]);

  // Pan transition effect
  useEffect(() => {
    if (!photos.length) return;

    const interval = setInterval(() => {
      const cellIndex = Math.floor(Math.random() * grid.length);
      const cell = document.querySelector(`[data-index="${cellIndex}"]`);
      if (!cell) return;

      const nextPhoto = photos[Math.floor(Math.random() * photos.length)];
      const directions = ["up", "down", "left", "right"];
      const dir = directions[Math.floor(Math.random() * directions.length)];

      // Create incoming photo
      const newFace = document.createElement("div");
      newFace.className = `grid-face incoming pan-${dir}`;
      newFace.style.backgroundImage = `url(${nextPhoto})`;

      cell.appendChild(newFace);

      // Trigger transition
      requestAnimationFrame(() => {
        newFace.classList.add("active");
      });

      // Finish transition
      setTimeout(() => {
        setGrid((prev) => {
          const newGrid = [...prev];
          newGrid[cellIndex] = nextPhoto;
          return newGrid;
        });
        cell.removeChild(newFace);
      }, 1000);
    }, 2000);

    return () => clearInterval(interval);
  }, [photos, grid.length]);

  return (
    <div
      className="bg-grid"
      style={{
        gridTemplateColumns: `repeat(${dimensions.cols}, 1fr)`,
        gridTemplateRows: `repeat(${dimensions.rows}, 1fr)`,
      }}
    >
      {grid.map((photo, idx) => (
        <div
          key={idx}
          data-index={idx}
          className="grid-cell"
          style={{ backgroundImage: `url(${photo})` }}
        />
      ))}
    </div>
  );
};

export default BackgroundSlideshow;
