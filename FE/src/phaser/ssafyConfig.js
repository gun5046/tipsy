import Phaser from "phaser";
import ssafyScene from './ssafyScene';

// Phaser 환경 설정
const config = {
    type: Phaser.AUTO,
    parent: "game-container",                       // 게임 div의 id
    backgroundColor: '#93cbee',
    scale: {
        mode: Phaser.Scale.ScaleModes.RESIZE,       // ScaleModes.RESIZE은 사이즈 크기에 따라 유동적으로 변화
        width: window.innerWidth,
        height: window.innerHeight,
      },
    scene: [ssafyScene],                            // 사용할 scene들은 해당 배열에 넣어줘야 함
    physics:{
        default:"arcade",                           // arcade라는 물리 엔진을 사용
        arcade:{
            debug: false,
        }
    }
};

export default config;