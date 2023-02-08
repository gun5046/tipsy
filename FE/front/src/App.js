import React from "react";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from "./pages/Home";
import GameView from "./pages/GameView";
import MainView from "./pages/MainView";
import About from "./pages/About";
import QRcode from "./components/QRcode";

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Routes>
          <Route path='/' element={<Home/>}/>
          <Route path='/about' element={<About/>}/>
          <Route path='/mainstreet' element={<MainView/>}/>
          <Route path='/phaser' element={<GameView/>}/>
          <Route path='/QR' element={<QRcode/>}/>
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
