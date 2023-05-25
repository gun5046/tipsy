import Phaser from "phaser";
import streetScene from './streetScene';


const config = {
    type: Phaser.AUTO,
    parent: "game-container",
    backgroundColor: '#93cbee',
    scale: {
        mode: Phaser.Scale.ScaleModes.RESIZE,
        width: window.innerWidth,
        height: window.innerHeight,
      },
    scene: [streetScene],
    physics:{
        default:"arcade",
        arcade:{
            debug: false,
            // gravity: { y: 300 },
        }

    }
};

export default config;