import { useState, useEffect } from 'react'
import phaser from 'phaser'

// 게임 실행 훅
export function useGame (config, containerRef) {
  const [startGame, setStartGame] = useState()
  useEffect(() => {
    if (!startGame && containerRef.current) {
      const newGame = new phaser.Game({ ...config, parent: containerRef.current })
      setStartGame(newGame)
      console.log('페이져 실행 훅');
    }
    return () => {
      startGame?.destroy(true)
    }
  }, [config, containerRef, startGame])

  return startGame
}


// 게임 실행 (다른 페이지에서 안 없어짐)
// useEffect(() => {
//   console.log('페이져 불러옴');
//   // phaserEl레퍼런스에 phaserConfig 로 scene을 생성
//   // scene은 phaserEl레퍼런스에 HTMLcanvas를 그리는 식으로 생성된다.
//   phaserEl.current = new Phaser.Game(phaserConfig);
//   // 한 번만 실행될 수 있도록 주의!! 
//   // 두 번 실행되면 두 개의 게임 화면이 생긴다.
//   // 여기서는 useEffect 의 dependency array에 []를 넣어서 한 번만 실행되도록 한다.
// }, []);
