import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';
import BackgroundSlideshow from "./BackgroundSlideshow";

const BASE_URL = process.env.REACT_APP_API_BASE_URL;
export default function Home() {
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        concertCount: 0,
        theaterCount: 0,
        cityCount: 0,
        countryCount: 0
    });
    

    useEffect(() => {
        fetch(`${BASE_URL}/api/stats`)
            .then(res => res.json())
            .then(data => setStats(data))
            .catch(console.error);
    }, []);

    return (
        <div className="relative w-full h-screen overflow-hidden">
            <BackgroundSlideshow />
            <div className="overlay" />
            <div className="home-content">
                <h1>🎶 Resonify 🎭</h1>
                <p>My personal journal of <b>concerts, theaters, and musical adventures</b>.</p>

                {/* Navigation cards */}
                <div className="nav-cards">
                    <div className="nav-card" >
                        <h3>🎼 Concerts</h3>
                        <h2>{stats.concertCount}</h2>
                    </div>

                    <div className="nav-card" >
                        <h3>🏛️ Theaters</h3>
                        <h2>{stats.theaterCount}</h2>
                    </div>

                    <div className="nav-card">
                        <h3>🏙️ Cities</h3>
                        <h2>{stats.cityCount}</h2>
                    </div>

                    <div className="nav-card">
                        <h3>🗺️ Countries</h3>
                        <h2>{stats.countryCount}</h2>
                    </div>
                </div>
                <button className="explore-button" onClick={() => navigate('/map')} >EXPLORE IT!</button>
                <footer className="home-footer">
                    Built with ❤️ for music & travel memories
                </footer>
            </div>
        </div >
    );
}