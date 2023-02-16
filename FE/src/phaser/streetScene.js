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
import playstore from '../assets/street/playstore.png'; 
import exit from '../assets/street/exit.png'; 


//animation
import jsonash from '../assets/character/ash.json'
import imageash from '../assets/character/ash.png'
import jsonlucy from '../assets/character/lucy.json'
import imagelucy from '../assets/character/lucy.png'

import mainstreet from '../assets/street/map.json';

// import popup from '../assets/street/popup.png'
import popup_s from '../assets/street/popup_s.png'
import popup_m from '../assets/street/popup_m.png'

import QR from '../assets/street/appQR.png'


import { getScene, getStreetPosition } from '../redux/gameSlice';
import { infoActions } from '../redux/infoSlice';
import { store } from '../redux/store';


let infoList = [
    {
        title: 'Exit',
        detail: `로그아웃을 원하시면 선택해주세요.\n선택 시 즉시 로그아웃됩니다.`,
        padding: 75, 
        url: '',
        shop: false
    },
    {
        title: 'App Store',
        detail: `TIPSY GAME 앱을 다운받아\n친구들과 미팅에서 게임을 즐겨보세요`,
        padding: 85,
        url: '',
        shop: false
    },    {
        title: 'Home',
        detail: `Home에 입장시 MyPage에서\n프로필 조회와 수정이 가능합니다`,
        padding: 72,
        url: 'mypage',
        shop: false
    },    {
        title: 'TIPSY Bar',
        detail: `Jazzbar TIPSY에서\n재즈와 함께 낭만적인 분위기를 느껴보세요\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 95,
        url: 'bar',
        shop: true
    },    {
        title: 'PUB TIPSY',
        detail: `신나는 음악과 함게\nPub TIPSY에서 맥주 한잔 어때신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 90,
        url: 'pub',
        shop: true
    },    {
        title: 'SSAFY 부울경캠퍼스',
        detail: `여기는 SSAFY 부울경캠퍼스가 위치한\n삼성전기 부산사업장입니다.\n이곳에서 SSAFY 친구들과 회식 어떠신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 96,
        url: 'ssafy',
        shop: true
    },    {
        title: 'SSAFY 구미캠퍼스',
        detail: `여기는 구미 인동에 위치한\n삼성전자 제2사업장 구미캠퍼스입니다.\n이곳에서 SSAFY 친구들과 회식 어떠신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 98,
        url: 'ssafy',
        shop: true
    },    {
        title: 'SSAFY 서울캠퍼스',
        detail: `여기는 역삼 캠퍼스에 위치한 멀티스퀘어입니다.\n이곳에서 SSAFY 친구들과 회식 어떠신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 96,
        url: 'ssafy',
        shop: true
    },    {
        
        title: 'SSAFY 대전캠퍼스',
        detail: `여기는 대전캠퍼스가 위치한\n삼성화재 유성연수원 교육동입니다.\n이곳에서 SSAFY 친구들과 회식 어떠신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 98,
        url: 'ssafy',
        shop: true
    },    {
        title: 'SSAFY 광주캠퍼스',
        detail: `여기는 SSAFY 광주캠퍼스가 있는\n삼성전자 광주 사업장입니다.\n이곳에서 SSAFY 친구들과 회식 어떠신가요?\n\n입장 인원 : 16 / 40 \n잔여테이블 : 17 / 20`,
        padding: 96,
        url: 'ssafy',
        shop: true
    }
]


// let current_building = -1;
// let buildingInfo = ['mypage', 'bar', 'pub', 'BU', 'daejeon', 'GJ', 'seoul', 'ssafy', 'hotel']


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
        this.load.image('playstore', playstore);
        this.load.image('exit', exit);
        
        this.load.image('popup_m', popup_m);
        this.load.image('popup_s', popup_s);
        this.load.image('QR', QR);

        
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
        // console.log(this.building_axios[0][0])

        
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
        const playstore = map.addTilesetImage("playstore", 'playstore');
        const exit = map.addTilesetImage("exit", 'exit');

        
        
        // 레이어 생성
        // 2배 확대 : setScale(2) -> setZoom 으로 대체
        const layer1 = map.createLayer('backgroundLayer', background, 0, 0)
        const layer2 = map.createLayer('floorLayer', [floor, floor_shadow], 0, 0)
        const layer3 = map.createLayer('storeLayer', [BU, daejeon, GJ, gumi, seoul, bar, hotel, house, pub, playstore, exit], 0, 0)
        

        //// 플레이어
        // JSON으로 불러온 캐릭터 적용
        // 캐릭터 선택
        this.characterKey = 'lucy'
        this.imageName = 'Lucy'

        const buildingLayer = map.getObjectLayer('storeObject');

        // 캐릭터 & 시작 위치 설정
        this.player = this.physics.add.sprite(buildingLayer.objects[2].x - 50, 577, this.characterKey).setScale(0.7).setDepth(32)

        //storeObject 레이어 생성
        buildingLayer.objects.forEach((buildingObj, i) => {
            var info = infoList[i]
            // dispatch로 이동할 url입력
            buildingObj.id = info.url

            // 정보 창 배경
            if(info.shop){
                var box = this.add.sprite(buildingObj.x + buildingObj.width / 2, buildingObj.y - 40 - 25, 'popup_m')
                const title_style = { font: "15px Arial", fill: '#ffffff'};
                if(info.title.length > 9){
                    var padding = 5.7
                } else { var padding = 4.5}
                var title = this.add.text(buildingObj.x + buildingObj.width / 2 - info.title.length * padding, buildingObj.y - 176 / 2 + 20 - 35,  info.title, title_style);
                // title.visible = false

                // 정보 창 상세내용
                const detail_style = { font: "10px Arial", fill: '#ffffff',  align: "center"};
                var detail = this.add.text(buildingObj.x + buildingObj.width / 2 - info.padding, buildingObj.y - 176 / 2 + 40 - 35,  info.detail, detail_style);  
                }else{
                var box = this.add.sprite(buildingObj.x + buildingObj.width / 2, buildingObj.y - 40, 'popup_s')
                const title_style = { font: "15px Arial", fill: '#ffffff'};
                if(info.title.length > 9){
                    var padding = 5.7
                } else { var padding = 4.5}
                var title = this.add.text(buildingObj.x + buildingObj.width / 2 - info.title.length * padding, buildingObj.y - 176 / 2 + 20 ,  info.title, title_style);
                // title.visible = false

                // 정보 창 상세내용
                const detail_style = { font: "10px Arial", fill: '#ffffff',  align: "center"};
                var detail = this.add.text(buildingObj.x + buildingObj.width / 2 - info.padding, buildingObj.y - 176 / 2 + 40,  info.detail, detail_style);
            }
            box.alpha = 0.7

            buildingObj.info = {
                box: box,
                title: title,
                detail: detail,
                // table: table
            }

        })

        // street의 빌딩들의 정보를 객체로 저장
        this.buildings = buildingLayer.objects
        this.buildings[1].qr = this.add.image(this.buildings[1].x + this.buildings[1].width / 2, this.buildings[1].y - 40 - 95,'QR')
        this.buildings[1].qr.visible = false
        
        //// 플레이어에 충돌 적용
        // 플레이어 월드 바깥 이동제한
        // this.player.setCollideWorldBounds(true);
        

        //// 키보드 입력기
        this.cursors = this.input.keyboard.createCursorKeys();
        this.spaceBar = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.SPACE)    
        this.Ctrl = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.CTRL)    

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
        this.buildings.forEach((building, i) => {
            // 건물 앞에 서있을 때 인식하기
             if (this.player.body.x > building.x && this.player.body.x < building.x + building.width ) {
                building.info.title.visible = true;
                building.info.detail.visible = true;
                building.info.box.visible = true;
                // if(building.info.table){
                //     building.info.table.visible = true;
                // }
                
                // spaceBar입력시 건물 입장
                if(Phaser.Input.Keyboard.JustDown(this.spaceBar)){
                    store.dispatch(getScene(building.id))
                    store.dispatch(getStreetPosition(this.player.body.x))
                    console.log(building.id);
                    if(i===1){
                        building.qr.visible = true;
                    }
                }
            }
            else{
                building.info.title.visible = false;
                building.info.detail.visible = false;
                building.info.box.visible = false;
                // if(building.info.table){
                //     building.info.table.visible = false;
                // }
                if(i===1){
                    building.qr.visible = false;
                }

            }

        })


        //// 속도 설정
        let speed = 180;
        // Shift 키를 누르면서 이동하면 빠르게 이동
        if (this.Ctrl.isDown) {speed = 280;}


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