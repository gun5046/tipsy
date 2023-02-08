import Phaser from "phaser";
import MainstreetScene from './MainstreetScene';

// Phaser 환경 설정
const config = {
    type: Phaser.AUTO,
    parent: "game-container",                          // 게임 div의 id
    // parent: 'phaser-example',
    backgroundColor: '#93cbee',
    // zoom: 2,                                 // 타일 배율 설정
    scale: {
        // mode: Phaser.Scale.FIT,              // FIT은 브라우저 크기가 변해도 사이즈 비율 유지
        mode: Phaser.Scale.ScaleModes.RESIZE,   // ScaleModes.RESIZE은 사이즈 크기에 따라 유동적으로 변화
        width: window.innerWidth,
        height: window.innerHeight,
      },
    // width: 800,
    // height: 600,
    scene: [MainstreetScene],                      // 사용할 scene들은 해당 배열에 넣어줘야 함
    // pixelArt: true,                          // 타일 선명하게
    physics:{
        default:"arcade",                       // arcade라는 물리 엔진을 사용
        arcade:{
            debug: false,
        }

    }
};

export default config;