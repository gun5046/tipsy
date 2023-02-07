import Phaser from 'phaser';

import background from '../assets/mainstreet/background.png';
import cloud from '../assets/mainstreet/cloud.png';
import floor from '../assets/mainstreet/floor.png';
import grass from '../assets/mainstreet/grass.png';
import stores from '../assets/mainstreet/stores.png';


// import face from './assets/map/face.png'
import jsonash from '../assets/character/ash.json'
import imageash from '../assets/character/ash.png'
import jsonlucy from '../assets/character/lucy.json'
import imagelucy from '../assets/character/lucy.png'

import mainstreet from '../assets/mainstreet/mainstreet.json';



class MainstreetScene extends Phaser.Scene {
    constructor () {
        super();
    }

    preload ()
    {
        // 플레이어 캐릭터 불러오기
        // image
        // this.load.image('character', face);
        
        // Json (키: ash or lucy)
        this.load.atlas('ash', imageash, jsonash)
        this.load.atlas('lucy', imagelucy, jsonlucy)

        // 타일맵 이미지 불러오기
        this.load.image('background', background);
        this.load.image('cloud', cloud);
        this.load.image('floor', floor);
        this.load.image('grass', grass);

        this.load.image('tilestore', stores);
        this.load.spritesheet('stores', stores, {
            frameWidth: 160,
            frameHeight: 160,
        })

        // 타일맵 Json 불러오기
        this.load.tilemapTiledJSON('map', mainstreet)

    }
    
    // 생성하기
    create ()
    {
        //// 맵 생성
        const map = this.make.tilemap({ key: "map", tileWidth: 16, tileHeight: 16});
        // 타일 생성
        const backgroundTileset = map.addTilesetImage("background",'background');
        const cloudTileset = map.addTilesetImage("cloud",'cloud');
        const floorTileset = map.addTilesetImage("floor",'floor');
        const grassTileset = map.addTilesetImage("grass", 'grass');
        const storesTileset = map.addTilesetImage("stores", 'stores');
        
        // 레이어 생성
        // 2배 확대 : setScale(2) -> setZoom 으로 대체
        // const layer1 = map.createLayer('floorLayer', floorTileset, 0, 0).setScale(2);
        const layer1 = map.createLayer('wallLayer', backgroundTileset, 0, 0)
        const layer2 = map.createLayer('floorLayer', floorTileset, 0, 0)
        const layer3 = map.createLayer('decoLayer', [cloudTileset, grassTileset], 0, 0)
        

        //// 플레이어
        // JSON으로 불러온 캐릭터 적용
        // 캐릭터 선택
        this.characterKey = 'lucy'
        this.imageName = 'Lucy'


        // 캐릭터 & 시작 위치 설정
        this.player = this.physics.add.sprite(100, 415, this.characterKey).setScale(0.8).setDepth(32)
        
        //chairObject 레이어 생성
        const storeLayer = map.getObjectLayer('storeObject');
        const stores = this.physics.add.staticGroup()
        storeLayer.objects.forEach((storeObj, i) => {
            const item = stores.get(storeObj.x + 160 * 0.5, storeObj.y - 160 * 0.5, 'stores', storeObj.gid - storesTileset.firstgid)
            const id = `${i}`
            item.id = id
            this.physics.add.overlap(this.player, item, ()=>console.log(item.id), null, this);
        })

        //// 플레이어에 충돌 적용
        // 왜 안돼!!!!!!
        // 플레이어 월드 바깥 이동제한
        this.player.setCollideWorldBounds(true);
        

        //// 키보드 입력기
        this.cursors = this.input.keyboard.createCursorKeys();
        

        //// 카메라 설정 ( 순서 중요!!!!!!!! )
        // 1. 경계 밖으로 카메라가 나가지 않도록 설정
        this.cameras.main.setBounds(0, 0, map.widthInPixels, map.heightInPixels);
        // 2. 플레이어를 중앙으로 카메라 이동
        this.cameras.main.startFollow(this.player);
    
        // 카메라로 맵 2배 확대 (setScale(2) 대신 가능)
        this.cameras.main.setZoom(1.5);

        // 애니메이션 함수 적용 (애니메이션 움직임을 createAnims함수로 만듬)
        this.createAnims(this.characterKey, this.imageName)  
    }
    
    // 실시간 반영
    update() {

        // 디버그용 (1초 간격으로 플레이어 좌표를 콘솔에 출력)
        // console.log(this.player.body.x, this.player.body.y); 
        

        //// 이전 속도 (애니메이션 적용에 순서 중요!!!!)
        // 1.이전 속도(x,y) 저장
        const prevVelocity = this.player.body.velocity.clone();
        // 2.이전 프레임의 속도를 0으로 설정
        this.player.setVelocity(0);         // (setVelocityY(0);, setVelocityX(0);)
        

        //// 플레이어 이동 & 애니메이션
        // 앉는 애니메이션 적용 방향
        let sit = 0
        
        // 이동 & 애니메이션 적용 (좌우 이동 우선시)
        if (this.cursors.left.isDown) {
            // 플레이어 이동
            this.player.setVelocityX(-160);
            // 애니메이션
            this.player.anims.play(`${this.characterKey}_run_left`, true);
            this.sit = 1
        } else if (this.cursors.right.isDown) {
            this.player.setVelocityX(160);
            this.player.anims.play(`${this.characterKey}_run_right`, true);
            this.sit = 2
        } else {
            // this.player.anims.stop();
            // console.log(prevVelocity)

            // 이동하다 멈추면, 사용할 프레임 선택 & idle상태로 전환
            if (prevVelocity.x < 0) {this.player.anims.play(`${this.characterKey}_idle_left`, true)}
            else if (prevVelocity.x > 0) {this.player.anims.play(`${this.characterKey}_idle_right`, true)}
            else if (prevVelocity.y < 0) {this.player.anims.play(`${this.characterKey}_idle_up`, true)}
            else if (prevVelocity.y > 0) {this.player.anims.play(`${this.characterKey}_idle_down`, true)}
        }

    }

    //////////////////////// FUNCTIONS ////////////////////////


    // 애니메이션 움직임 함수 생성
    createAnims(characterKey,imageName) {
        this.anims.create({
            // key: 'ash_idle_right',
            key: `${characterKey}_idle_right`,
            frames: this.anims.generateFrameNames(characterKey, {
            prefix: `${imageName}_idle_anim_`,
            suffix: '.png',
              start: 1,
              end: 6,
            }),
            // 반복
            repeat: -1,
            // 프레임 속도
            frameRate: 6
            })
          
          this.anims.create({
            key: `${characterKey}_idle_up`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_idle_anim_`,
                suffix: '.png',
                start: 7,
                end: 12,
            }),
            repeat: -1,
            frameRate: 6
          })
          
          this.anims.create({
            key: `${characterKey}_idle_left`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_idle_anim_`,
                suffix: '.png',
                start: 13,
                end: 18,
            }),
            repeat: -1,
            frameRate: 6
          })
        
          this.anims.create({
            key: `${characterKey}_idle_down`,
            frames: this.anims.generateFrameNames(characterKey, {

                prefix: `${imageName}_idle_anim_`,
                suffix: '.png',
                start: 19,
                end: 24,
            }),
            repeat: -1,
            frameRate: 6
          })
   
        this.anims.create({
            key: `${characterKey}_run_right`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_run_`,
                suffix: '.png',
                start: 1,
                end: 6,
            }),
            // 반복
            repeat: -1,
            // 프레임 속도
            frameRate: 10,
        })
        
        this.anims.create({
            key: `${characterKey}_run_up`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_run_`,
                suffix: '.png',
                start: 7,
                end: 12,
            }),
            repeat: -1,
            frameRate: 10,
        })
        
        this.anims.create({
            key: `${characterKey}_run_left`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_run_`,
                suffix: '.png',
                start: 13,
                end: 18,
            }),
            repeat: -1,
            frameRate: 10,
        })
        
        this.anims.create({
            key: `${characterKey}_run_down`,
            frames: this.anims.generateFrameNames(characterKey, {
                prefix: `${imageName}_run_`,
                suffix: '.png',
                start: 19,
                end: 24,
            }),
            repeat: -1,
            frameRate: 10,
        })

        this.anims.create({
            key: `${characterKey}_sit_down`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_down.png`}],
            // repeat: 0,
            frameRate: 20,
        })
        
        this.anims.create({
            key: `${characterKey}_sit_left`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_left.png`}],
            // repeat: 0,
            frameRate: 20,
        })
    
        this.anims.create({
            key: `${characterKey}_sit_right`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_right.png`}],
            // repeat: 0,
            frameRate: 20,
        })
    
        this.anims.create({
            key: `${characterKey}_sit_up`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_up.png`}],
            // repeat: 0,
            frameRate: 20,
        })
    }   
}

export default MainstreetScene;