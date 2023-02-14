import Phaser from 'phaser';

import background from '../assets/street/background.jpg';
import floor from '../assets/street/floor.png';
import floor_shadow from '../assets/street/floor_shadow.png';

//campus
import BU from '../assets/street/BU.png'; 
import daejeon from '../assets/street/daejeon.png'; 
import GJ from '../assets/street/GJ.png'; 
import gumi from '../assets/street/gumi.png'; 
import seoul from '../assets/street/seoul.png';

//building
import bar from '../assets/street/bar.png'; 
import hotel from '../assets/street/hotel.png'; 
import house from '../assets/street/house.png'; 
import pub from '../assets/street/pub.png'; 

//animation
import jsonash from '../assets/character/ash.json'
import imageash from '../assets/character/ash.png'
import jsonlucy from '../assets/character/lucy.json'
import imagelucy from '../assets/character/lucy.png'

import mainstreet from '../assets/street/map.json';

import room1 from '../assets/roomInfo/room1.png';
import room2 from '../assets/roomInfo/room2.png';
import room3 from '../assets/roomInfo/room3.png';
import room4 from '../assets/roomInfo/room4.png';


import { getScene } from '../redux/gameSlice';
import { store } from '../redux/store';
import axios from 'axios';


let current_building = -1;
let buildingInfo = ['mypage', 'bar', 'pub', 'BU', 'daejeon', 'GJ', 'seoul', 'ssafy', 'hotel']


class streetScene extends Phaser.Scene {
    constructor () {
        super('streetmap');
    }
    
    preload ()
    {
        //// 캐릭터 불러오기
        // Json (키: ash or lucy)
        this.load.atlas('ash', imageash, jsonash)
        this.load.atlas('lucy', imagelucy, jsonlucy)
        
        // 타일맵 이미지 불러오기
        this.load.image('background', background);
        this.load.image('floor', floor);
        this.load.image('floor_shadow', floor_shadow);

        this.load.image('BU', BU);
        this.load.image('daejeon', daejeon);
        this.load.image('GJ', GJ);
        this.load.image('gumi', gumi);
        this.load.image('seoul', seoul);
        
        this.load.image('bar', bar);
        this.load.image('hotel', hotel);
        this.load.image('house', house);
        this.load.image('pub', pub);
        
        this.load.image('room1', room1);
        
        // this.load.image('tilestore', stores);
        // this.load.spritesheet('stores', stores, {
            //     frameWidth: 160,
            //     frameHeight: 160,
            // })
        
        // 타일맵 Json 불러오기
        this.load.tilemapTiledJSON('map', mainstreet)
            
        this.building_axios = store.getState().info.buildingInfo
        
        console.log('building_axios22222222222')
        console.log(this.building_axios)
        console.log(this.building_axios[0][0])

        
        }
        
        // 생성하기
        create ()
        {
        //// 맵 생성
        const map = this.make.tilemap({ key: "map", tileWidth: 16, tileHeight: 16});
        // 타일 생성
        const background = map.addTilesetImage("background",'background');
        const floor = map.addTilesetImage("floor",'floor');
        const floor_shadow = map.addTilesetImage("floor_shadow",'floor_shadow');
        
        const BU = map.addTilesetImage("BU", 'BU');
        const daejeon = map.addTilesetImage("daejeon", 'daejeon');
        const GJ = map.addTilesetImage("GJ", 'GJ');
        const gumi = map.addTilesetImage("gumi", 'gumi');
        const seoul = map.addTilesetImage("seoul", 'seoul');

        const bar = map.addTilesetImage("bar", 'bar');
        const hotel = map.addTilesetImage("hotel", 'hotel');
        const house = map.addTilesetImage("house", 'house');
        const pub = map.addTilesetImage("pub", 'pub');
        
        
        // 레이어 생성
        // 2배 확대 : setScale(2) -> setZoom 으로 대체
        const layer1 = map.createLayer('backgroundLayer', background, 0, 0)
        const layer2 = map.createLayer('floorLayer', [floor, floor_shadow], 0, 0)
        const layer3 = map.createLayer('storeLayer', [BU, daejeon, GJ, gumi, seoul, bar, hotel, house, pub], 0, 0)
        

        //// 플레이어
        // JSON으로 불러온 캐릭터 적용
        // 캐릭터 선택
        this.characterKey = 'lucy'
        this.imageName = 'Lucy'


        // 캐릭터 & 시작 위치 설정
        this.player = this.physics.add.sprite(100, 415, this.characterKey).setScale(0.7).setDepth(32)

        //storeObject 레이어 생성
        const buildingLayer = map.getObjectLayer('storeObject');
        buildingLayer.objects.forEach((buildingObj, i) => {
            buildingObj.id = buildingInfo[i]
            buildingObj.image = this.add.image(buildingObj.x + buildingObj.width / 2 + 10, buildingObj.y + buildingObj.height / 2 - 20, 'room1')
            buildingObj.image.visible = false
        })

        this.buildings = buildingLayer.objects
        
        //// 플레이어에 충돌 적용
        // 플레이어 월드 바깥 이동제한
        // this.player.setCollideWorldBounds(true);
        

        //// 키보드 입력기
        this.cursors = this.input.keyboard.createCursorKeys();
        this.spaceBar = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.SPACE)    
        this.keyZ = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.Z)    

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

        // 맵이동
        this.buildings.forEach((building) => {
             if (this.player.body.x > building.x && this.player.body.x < building.x + building.width ) {
                building.image.visible = true;
                if(Phaser.Input.Keyboard.JustDown(this.spaceBar)){
                    store.dispatch(getScene(building.id))
                    console.log(building.id);
                }
            }
            else{
                building.image.visible = false;
            }

        })


        //// 속도 설정
        let speed = 180;
        // Shift 키를 누르면서 이동하면 빠르게 이동
        if (this.keyZ.isDown) {speed = 280;}


        //// 이전 속도 (애니메이션 적용에 순서 중요!!!!)
        // 1.이전 속도(x,y) 저장
        const prevVelocity = this.player.body.velocity.clone();
        // 2.이전 프레임의 속도를 0으로 설정
        this.player.setVelocity(0);
        

        //// 플레이어 이동 & 애니메이션
        // 이동 & 애니메이션 적용 (좌우 이동 우선시)
        if (this.cursors.left.isDown) {
            // 플레이어 이동
            this.player.setVelocityX(-speed);
            // 애니메이션
            this.player.anims.play(`${this.characterKey}_run_left`, true);

        } else if (this.cursors.right.isDown) {
            this.player.setVelocityX(speed);
            this.player.anims.play(`${this.characterKey}_run_right`, true);
 
        } else {
            // 이동하다 멈추면, 사용할 프레임 선택 & idle상태로 전환
            // console.log(current_table)
            if (prevVelocity.x < 0) {this.player.anims.play(`${this.characterKey}_idle_left`, true)}
            else if (prevVelocity.x > 0) {this.player.anims.play(`${this.characterKey}_idle_right`, true)}
            else if (prevVelocity.y < 0) {this.player.anims.play(`${this.characterKey}_idle_up`, true)}
            else if (prevVelocity.y > 0) {this.player.anims.play(`${this.characterKey}_idle_down`, true)}
        }

        // if (this.cursors.up.isDown && this.player.body.touching.down)
        // {
        //     player.setVelocityY(-330);
        // }

    }

    //////////////////////// FUNCTIONS ////////////////////////

    // 애니메이션 움직임 함수 생성
    createAnims(characterKey,imageName) {
        this.anims.create({
            key: `${characterKey}_idle_right`,
            frames: this.anims.generateFrameNames(characterKey, {
            prefix: `${imageName}_idle_anim_`,
            suffix: '.png',
              start: 1,
              end: 6,
            }),
            repeat: -1,
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
            frameRate: 20,
        })
        
        this.anims.create({
            key: `${characterKey}_sit_left`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_left.png`}],
            frameRate: 20,
        })
    
        this.anims.create({
            key: `${characterKey}_sit_right`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_right.png`}],
            frameRate: 20,
        })
    
        this.anims.create({
            key: `${characterKey}_sit_up`,
            frames: [{ key: characterKey, frame:`${imageName}_sit_up.png`}],
            frameRate: 20,
        })
    }   
}

export default streetScene;