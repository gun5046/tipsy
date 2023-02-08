import phaser from 'phaser';
import React, { useRef, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import phaserConfig from '../phaser/Config';
// import { useGame } from '../hook/useGame';

// 게임 화면 뷰 영역 컴포넌트

// 스타일이 적용된 <section> 렌더링
// const GameViewContainer = styled.section`
//   z-index: -1;
//   position: absolute;
//   top: 0;
//   left: 0;
// `;

const GameView = () => {
  // 게임 화면 초기화
  const phaserEl = useRef(null);
  const [startGame, setStartGame] = useState()

  // 게임 실행 훅 함수 (만듬)
  // useGame(phaserConfig, phaserEl);

  useEffect(() => {
    // console.log('페이져 불러옴');
    if (!startGame && phaserEl.current) {
      const newGame = new phaser.Game(phaserConfig)
      // phaserEl레퍼런스에 phaserConfig 로 scene을 생성
      phaserEl.current = newGame
      setStartGame(newGame)
    }
    // 언마운트 시 destroy
    return () => {
      startGame?.destroy(true)
    }
  }, [phaserConfig, phaserEl, startGame])

  
  return (
    <div>
      <Link to="/">Home</Link>
      {/* <GameViewContainer> */}
        <div ref={phaserEl} className="game-container"></div>
      {/* </GameViewContainer> */}
    </div>
  );
};

export default GameView;