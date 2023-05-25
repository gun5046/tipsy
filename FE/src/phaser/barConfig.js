import Phaser from "phaser";
import barScene from "./barScene";


const config = {
    type: Phaser.AUTO,
    parent: "game-container",
    backgroundColor: '#93cbee',
    scale: {
        mode: Phaser.Scale.ScaleModes.RESIZE,
        width: window.innerWidth,
        height: window.innerHeight,
      },
    scene: [barScene],
    physics:{
        default:"arcade",
        arcade:{
            debug: false,
        }
    }
};

export default config;