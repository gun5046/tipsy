import phaser from 'phaser';
import React, { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import barConfig from '../phaser/barConfig';
import { useSelector } from 'react-redux';
import axios from "axios";


const BarView = () => {
  const phaserEl = useRef(null);
  const [startGame, setStartGame] = useState()
  const navigate = useNavigate();

  const changeScene = useSelector((state) => state.game.scene)
  
  useEffect(() => {
    console.log(changeScene)
    if (changeScene ==='street') {
      navigate('/mainstreet')
    }
  }, [changeScene])

  
  useEffect(() => {
    if (!startGame && phaserEl.current) {
      const newGame = new phaser.Game(barConfig)
      phaserEl.current = newGame
      setStartGame(newGame)
    }
    return () => {
      startGame?.destroy(true)
    }
  }, [barConfig, phaserEl, startGame])

  
  return (
    <div>
      <div ref={phaserEl} className="game-container"></div>
    </div>
  );
};

export default BarView;