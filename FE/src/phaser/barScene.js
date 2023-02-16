import Phaser from 'phaser';

import bar_floor_table_deco from '../assets/barMap/bar_floor_table_deco.png';
import bar_floors from '../assets/barMap/bar_floors.png';
import bar_furniture from '../assets/barMap/bar_furniture.png';
import bar_furniture_deco from '../assets/barMap/bar_furniture_deco.png';
import bar_furniture_deco_32 from '../assets/barMap/bar_furniture_deco_32.png';
import bar_wineInterior from '../assets/barMap/bar_wineInterior.png';
import Classroom_and_library from '../assets/barMap/Classroom_and_library.png';
import Generic from '../assets/barMap/Generic.png';
import Room_Builder_Office from '../assets/barMap/Room_Builder_Office.png';
import Room_Builder_Walls from '../assets/barMap/Room_Builder_Walls.png';


import jsonash from '../assets/character/ash.json'
import imageash from '../assets/character/ash.png'
import jsonlucy from '../assets/character/lucy.json'
import imagelucy from '../assets/character/lucy.png'

import bar_map from '../assets/barMap/map.json';

import bu from '../assets/photo/김부경.png';
import popup from '../assets/street/popup.png'

import { getScene } from '../redux/gameSlice';
import { store } from '../redux/store';

let current_chair = -1
let current_table = -1
let chair_x = -1
let chair_y = -1
let table_array = new Array(11);
let roomTF = new Array(11);
//테이블 위치랑 axios 배열의 위치 대응
let roomIndex = new Array(11);
const roomTemp = [[0, 0], [0, 0],[0, 0],[0, 0],[10, 0],[0, 0],[0, 0],[0, 0],[0, 0],[0, 0],[0, 0]]

class barScene extends Phaser.Scene {
    constructor () {
        super('barmap');
    }

    preload ()
    {
        // 플레이어 캐릭터 불러오기
       
        // Json (키: ash or lucy)
        this.load.atlas('ash', imageash, jsonash)
        this.load.atlas('lucy', imagelucy, jsonlucy)

        // 타일맵 이미지 불러오기
        this.load.image('bar_floors', bar_floors);
        this.load.image('bar_furniture_deco', bar_furniture_deco);
        this.load.image('bar_furniture', bar_furniture);
        this.load.image('bar_wineInterior', bar_wineInterior);
        this.load.image('bar_floor_table_deco', bar_floor_table_deco);
        this.load.image('Classroom_and_library', Classroom_and_library);
        this.load.image('Generic', Generic);
        this.load.image('Room_Builder_Office', Room_Builder_Office);
        this.load.image('Room_Builder_Walls', Room_Builder_Walls);

        this.load.image('popup', popup)
        this.load.image('bu', bu)

        // chairObject의 의자 이미지 불러오기
        this.load.image('tilesChair', bar_furniture_deco_32);
        this.load.spritesheet('chairs', bar_furniture_deco_32, {
            frameWidth: 32,
            frameHeight: 32,
        })

        // 타일맵 Json 불러오기
        this.load.tilemapTiledJSON('map', bar_map)

        this.table1_axios = [
            {"title":"별이빛나는밤에","password":"1111","entrance":"off","silence":"off","time":"20230210155347","host":"6","max":4,
            "code":"uawm5103","current":2,
            "member":[
                {"uid":6,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxODA3MDRfMTk4%2FMDAxNTMwNzEyMDA4Mzg0.wOQo5UtyC0UgJXf2ARs4wYoVq7xKN-YFLFd8ALMTwjUg.Rd98r1apdrj6l13X7Oksb7OoOy_SeGB3VhB-770OGT8g.JPEG.kiko13%2FIMG_2521.JPG&type=a340",
                "gender":"male","interest":"개껌","reportcnt":"0","name":"",
                "nickname":"a","birth":"","position":"1","kakao_id":"2638215374"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTA0MzBfNjUg%2FMDAxNjE5Nzg3NjgzNjQ4.RSfPWBkbvlElEzX2Mmh1DCxHIlLpY4lwQ-BHdrxY1wkg.kz22rF5m2KNE_P5ioVXXaGHdmRRY_Toc6iYDCjknTzQg.JPEG.espart1226%2F2005a7f5c29ae260b899329501ffeb6f.jpg&type=a340",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"b","birth":"","position":"2","kakao_id":"2542925662"}],

                "hashtag":["친목좋아요","무드","감성"]},
            {"title":"술꾼도시여자들","entrance":"off","silence":"off","time":"20230210155418","host":"5","max":4,
            "code":"8vvak101","current":4,
            "member":[
                {"uid":6,"image":"https://post-phinf.pstatic.net/MjAyMTEwMTVfOTQg/MDAxNjM0MzA0ODMwMjAx.vPlmE3Q5CojBYPgTFJHGfMRTJSdeMD2N3TKzWmxB3j4g.eoJsC5dbz4G1wTo2EOUxpK-qpCcJN5Uny9vF31gZLssg.JPEG/%EC%9E%94%EB%93%A0%EB%AF%95.jpg?type=w1200",
                "gender":"male","interest":"개껌","reportcnt":"0","name":"",
                "nickname":"강지구","birth":"","position":"1","kakao_id":"2638215374"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjEyMTFfMTI4%2FMDAxNjcwNjg5NTUxOTYz._WO-fQMUBo-loq4ozfUHdP4xbt8rAyybWS1v7_QicaUg.MJpwLLOfneGM9vj_jTc6eXmaRH2JeJU0CdaemdbEYMcg.JPEG.cjdthddl83%2FIMG%25A3%25DF20221211%25A3%25DF012119%25A3%25DF280.jpg&type=a340",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"한지연","birth":"","position":"2","kakao_id":"2542925662"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzAxMTNfMTkz%2FMDAxNjczNjIwOTI1ODAx.7zFi4sggkorfte4aRgtsO7zGgBfOQse7G1lAWsOC5cEg.aSWjs6Fj_mq2YCpW1VNquaaq-T1DautbsUXQ3FCMEpwg.PNG.kws3128pdm%2F%25C1%25A6%25B8%25F1%25C0%25BB_%25C0%25D4%25B7%25C2%25C7%25CF%25BC%25BC%25BF%25E4_%25283%2529.png&type=a340",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"안소희","birth":"","position":"3","kakao_id":"2542925662"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fimgnews.naver.net%2Fimage%2F311%2F2022%2F12%2F23%2F0001538407_003_20221223095601555.jpg&type=a340",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"강북구","birth":"","position":"4","kakao_id":"2542925662"},
                ],
                
                "hashtag":["적시자","아우디", "오복집"]},
            {"title":"집사모임","entrance":"off","silence":"off","time":"20230210155418","host":"5","max":4,
            "code":"8vvak105","current":2,
            "member":[
                {"uid":6,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fpost.phinf.naver.net%2FMjAyMDA2MDlfMjEw%2FMDAxNTkxNjk1MTM4OTI4.LvldwiT0_pjrP8xtQeJUifXhtvO4WFXeEz6xwJmz0J8g.k9LyAkihXYNAGlO-LNG2aDh4MHT1uEw7jdZmssrRddsg.JPEG%2FIUh3Uhbp3qRQKP34LfrZKVLLagB4.jpg&type=a340",
                "gender":"male","interest":"개껌","reportcnt":"0","name":"",
                "nickname":"c","birth":"","position":"2","kakao_id":"2638215374"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxOTAzMTRfMTg1%2FMDAxNTUyNTI4NjI5NzE3.aViiuuxxvSKuj-NyEuf4qpJMqooATX5LuJYaaPzogkgg.xJ9__dAz6jX0bURfELp-OFeyAsMfd3__XpgpZPcF0egg.JPEG.aveeya%2FIMG_0830.jpg&type=a340",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"d","birth":"","position":"3","kakao_id":"2542925662"}],
                
                "hashtag":["집사","귀여워", "츄르츄르"]},
            {"title":"2:2소개팅","entrance":"off","silence":"off","time":"20230210155418","host":"5","max":4,
            "code":"8vvak108","current":2,
            "member":[
                {"uid":6,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzAxMjZfNDcg%2FMDAxNjc0NzAwOTA4MDk5.PGM4aDYSmIG0mHqKdEw3IBZXh2X57ulDzlJ8gN1KOAEg.5BoD8nJxnEoWtVH8VkoYRWDffIe0MPVVTei0WnXfchgg.JPEG.withinarts%2FUntitled-4_copy.jpg&type=a340",
                "gender":"male","interest":"개껌","reportcnt":"0","name":"",
                "nickname":"e","birth":"","position":"1","kakao_id":"2638215374"},

                {"uid":5,"image":"https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMjA3MTNfMTI1%2FMDAxNjU3Njg5ODQ2MjIx.55D9DLEri46TT5dqocmEMxkWo6aHZmmA6vw42fjTc9Ug.H2SgvGfavaIXiKgotDvuUnT7KPTmYJ5heJpKg8sAG-Mg.JPEG.lyj0900%2FScreenshot%25A3%25DF20220713%25A3%25AD141515%25A3%25DFInstagram.jpg&type=sc960_832",
                "gender":"male","interest":"","reportcnt":"0","name":"",
                "nickname":"f","birth":"","position":"2","kakao_id":"2542925662"}],
                
                "hashtag":["26살","구미살아요", "자만추"]}
            ]

        //비공개 방 : 2, 공개방 : 1, 사람없음 : 0
        this.table1_axios.forEach((obj, i)=> {
            const room_num = Number(obj.code.substring(6, 8)) - 1
            if (obj.password){
                roomTF[room_num] = 2
            }
            else{
                roomTF[room_num] = 1
            }
            roomIndex[room_num] = i
            obj.member.forEach(user => {
                this.load.image(user.nickname, user.image)
            })
        })
    }

    
    // 생성하기
    create ()
    {
        //// 맵 생성
        const map = this.make.tilemap({ key: "map", tileWidth: 16, tileHeight: 16});

        // 타일 생성
        //(타일셋이름 ,이미지이름)
        const bar_floors = map.addTilesetImage("bar_floors",'bar_floors');
        const bar_floor_table_deco = map.addTilesetImage("bar_floor_table_deco",'bar_floor_table_deco');
        const bar_furniture_deco = map.addTilesetImage("bar_furniture_deco",'bar_furniture_deco');
        const bar_furniture = map.addTilesetImage("bar_furniture",'bar_furniture');
        const bar_wineInterior = map.addTilesetImage("bar_wineInterior",'bar_wineInterior');
        const Classroom_and_library = map.addTilesetImage("Classroom_and_library",'Classroom_and_library');
        const Generic = map.addTilesetImage("Generic",'Generic');
        const Room_Builder_Office = map.addTilesetImage("Room_Builder_Office", 'Room_Builder_Office');
        const Room_Builder_Walls = map.addTilesetImage("Room_Builder_Walls", 'Room_Builder_Walls');
        const chairTileset = map.addTilesetImage("bar_furniture_deco_32",'tilesChair');

        
        // 레이어 생성
        // 2배 확대 : setScale(2) -> setZoom 으로 대체
        // const layer1 = map.createLayer('floorLayer', floorTileset, 0, 0).setScale(2);
        const layer1 = map.createLayer('floorLayer', bar_floors, 0, 0)
        const layer2 = map.createLayer('wallLayer', [Room_Builder_Office, Room_Builder_Walls], 0, 0)
        const layer3 = map.createLayer('decoLayer', [bar_furniture_deco, bar_furniture, bar_wineInterior], 0, 0)
        const layer4 = map.createLayer('stairLayer', Generic, 0, 0)
        const layer5 = map.createLayer('funitureLayer', [bar_furniture_deco, bar_furniture, Classroom_and_library], 0, 0)
        const layer6 = map.createLayer('tableLayer', [Generic, bar_floor_table_deco, bar_furniture_deco, bar_furniture], 0, 0)
        const layer7 = map.createLayer('subwallLayer', [Room_Builder_Office, Room_Builder_Walls], 0, 0)

        //// 타일에 충돌(Collision) 적용
        // Tiled에서 생성한 collides 적용
        layer2.setCollisionByProperty({ collides: true });
        layer5.setCollisionByProperty({ collides: true });
        layer6.setCollisionByProperty({ collides: true });

        //// 플레이어
        // JSON으로 불러온 캐릭터 적용
        // 캐릭터 선택
        this.characterKey = 'lucy'
        this.imageName = 'Lucy'
        // this.characterKey = 'ash'
        // this.imageName = 'Ash'

        // 캐릭터 & 시작 위치 설정
        this.player = this.physics.add.sprite(78, 630, this.characterKey).setScale(0.8).setDepth(32)

        
        //// chairObject 레이어 생성
        const chairLayer = map.getObjectLayer('chairObject');
        const chairs = this.physics.add.staticGroup();

        // console.log(chairTileset)

        chairLayer.objects.forEach((chairObj, i) => {
            // console.log(chairObj.gid, bar_furniture_deco_32.firstgid)
            const item = chairs.get(chairObj.x + chairObj.width * 0.5, chairObj.y - chairObj.height * 0.5, 'chairs', chairObj.gid - chairTileset.firstgid).setDepth(10)
            const id = Number(`${i}`)            
            item.id = id
            this.physics.add.overlap(this.player, item, ()=>this.seat(item), null, this)
        })

        //// infoObject 레이어 생성
        const infoLayer = map.getObjectLayer('infoObject');
        infoLayer.objects.forEach((infoObj, i) => {
          if(roomTF[i]){
              let data = {};
              data.popup = this.add.image(infoObj.x + infoObj.width / 2 + 20, infoObj.y + infoObj.height / 2, 'popup')
              data.popup.setDisplaySize(200, 100)
              data.popup.setDepth(40)
              data.popup.alpha = 0.7
              data.popup.visible = false

            //   console.log(this.table1_axios[roomIndex[i]], i)
              const title_style = { font: "15px Arial", fill: '#ffffff'};
              data.title = this.add.text(infoObj.x + infoObj.width / 2 - this.table1_axios[roomIndex[i]].title.length * 4 + roomTemp[i][0], infoObj.y + infoObj.height / 2 - 30 , this.table1_axios[roomIndex[i]].title, title_style);
              data.title.setDepth(45)
              data.title.visible = false

              const detail_style = { font: "10px Arial", fill: '#ffffff',  align: "center"};
              let tf = ''
              if(roomTF[i] == 2){
                  tf = '비공개방'
                  // const public = '비공개방'
              }else{
                  tf = '공개방'
                  // const public = '공개방'
              }
              const current = '현재 인원 : '+ this.table1_axios[roomIndex[i]].current.toString()+' / 4'
              let hashtag = ''
              this.table1_axios[roomIndex[i]].hashtag.forEach((tag, i) => {
                  if(i % 3 === 0 && i !== 0){
                      hashtag += '\n#' + tag +'  '
                  }
                  else{    
                  hashtag += '#' + tag + '  '
                  }
              })
              const input = `${tf}\n${current}\n${hashtag}`
              data.detail = this.add.text(infoObj.x + infoObj.width / 2 - 40 + roomTemp[i][1], infoObj.y + infoObj.height / 2 - 10, input, detail_style);
              data.detail.setDepth(45)
              data.detail.visible = false

              table_array[i] = data
          } 
        })

        roomTF.forEach((is_room, i) => {
            if (is_room > 0){
                this.table1_axios[roomIndex[i]].member.forEach( obj => {
                    const chair_num = i * 4 + Number(obj.position) - 1
                    const chairObj = chairLayer.objects[chair_num]
                    const profile = this.add.sprite(chairObj.x + chairObj.width * 0.5, chairObj.y - chairObj.height * 0.5, obj.nickname)
                    // console.log(obj.image)
                    profile.setDisplaySize(40, 40)
                    const shape = this.add.graphics().setPosition(chairObj.x + chairObj.width * 0.5, chairObj.y - chairObj.height * 0.5).fillCircle(0, 0, 17)
                    profile.setMask(shape.createGeometryMask())
                    profile.setDepth(30)
                })
            }
        })


        //// 타일에 충돌(Collision) 적용
        this.physics.add.collider(this.player, [layer2, layer5, layer6]);


        //// 키보드 입력기
        this.cursors = this.input.keyboard.createCursorKeys();
        // 키보드 입력키 추가
        this.Ctrl = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.CTRL)
        this.Alt = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.ALT)
        this.spaceBar = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.SPACE)
   

        //// 카메라 설정 ( 순서 중요!!!!!!!! )
        // 1. 경계 밖으로 카메라가 나가지 않도록 설정
        this.cameras.main.setBounds(0, 0, map.widthInPixels, map.heightInPixels);
        // 2. 플레이어를 중앙으로 카메라 이동
        this.cameras.main.startFollow(this.player);

        // 카메라로 맵 2배 확대 (setScale(2) 대신 가능)
        this.cameras.main.setZoom(2);

        // 애니메이션 함수 적용 (애니메이션 움직임을 createAnims함수로 만듬)
        this.createAnims(this.characterKey, this.imageName)  
    }
    
    // 실시간 반영
    update() {
        // 디버그용 (1초 간격으로 플레이어 좌표를 콘솔에 출력)
        // console.log(current_table, current_chair); 
        // console.log(this.player.body.y); 
                
        if (this.player.body.y > 632) {
            store.dispatch(getScene("street"))
            // 리덕스로 'street' 보냄
        }

        let speed = 200;
        // Shift 키를 누르면서 이동하면 빠르게 이동
        if (this.Ctrl.isDown) {speed = 300;}
    

        //// 이전 속도 (애니메이션 적용에 순서 중요!!!!)
        // 1.이전 속도(x,y) 저장
        const prevVelocity = this.player.body.velocity.clone();
        // 2.이전 프레임의 속도를 0으로 설정
        this.player.setVelocity(0);         // (setVelocityY(0);, setVelocityX(0);)
        

        //// 플레이어 이동 & 애니메이션        
        // 이동 & 애니메이션 적용 (좌우 이동 우선시)
        if (this.cursors.left.isDown) {
            // 플레이어 이동
            this.player.setVelocityX(-speed);
            // 애니메이션
            this.player.anims.play(`${this.characterKey}_run_left`, true);
            if (current_table >= 0){
                if (roomTF[current_table]){
                    table_array[current_table].popup.visible = false
                    table_array[current_table].title.visible = false
                    table_array[current_table].detail.visible = false
                }
                current_table = -1
                current_chair = -1
            }

        } else if (this.cursors.right.isDown) {
            this.player.setVelocityX(speed);
            this.player.anims.play(`${this.characterKey}_run_right`, true);
            if (current_table >= 0){
                if (roomTF[current_table]){
                    table_array[current_table].popup.visible = false
                    table_array[current_table].title.visible = false
                    table_array[current_table].detail.visible = false
                }
                current_table = -1
                current_chair = -1
            }

        } else if (this.cursors.up.isDown) {
            this.player.setVelocityY(-speed);
            this.player.anims.play(`${this.characterKey}_run_up`, true);
            if (current_table >= 0){
                if (roomTF[current_table]){
                    table_array[current_table].popup.visible = false
                    table_array[current_table].title.visible = false
                    table_array[current_table].detail.visible = false
                }
                current_table = -1
                current_chair = -1
            }

        } else if (this.cursors.down.isDown) {
            this.player.setVelocityY(speed);
            this.player.anims.play(`${this.characterKey}_run_down`, true);
            if (current_table >= 0){
                if (roomTF[current_table]){
                    table_array[current_table].popup.visible = false
                    table_array[current_table].title.visible = false
                    table_array[current_table].detail.visible = false
                }
                current_table = -1
                current_chair = -1
            }

        } else {
            // this.player.anims.stop();
            // console.log(prevVelocity)

            // 이동하다 멈추면, 사용할 프레임 선택 & idle상태로 전환
            if (prevVelocity.x < 0) {this.player.anims.play(`${this.characterKey}_idle_left`, true)}
            else if (prevVelocity.x > 0) {this.player.anims.play(`${this.characterKey}_idle_right`, true)}
            else if (prevVelocity.y < 0) {this.player.anims.play(`${this.characterKey}_idle_up`, true)}
            else if (prevVelocity.y > 0) {this.player.anims.play(`${this.characterKey}_idle_down`, true)}
        }

        // 앉는 애니메이션 적용
        // 'E' 키 눌렀을 때 앉는 모션 추가
        if (this.Alt.isDown && current_table >= 0) {
            // console.log(prevVelocity)
            // console.log(this.sit)
            // this.player.anims.play(`${this.characterKey}_sit_left`, true);
            // console.log(this.overlapChair)

            // 나중에 의자 모양에 따라 모션이 바뀌는 걸로 조정 !!!!!!!!!!!!!!!!!!!!!!
            switch(current_chair){
                case 0:
                case 1: //앞면
                    this.player.anims.play(`${this.characterKey}_sit_down`, true)
                    this.player.setPosition(chair_x, chair_y - 4)
                    break

                case 2:
                case 3: //뒷모습
                    this.player.anims.play(`${this.characterKey}_sit_up`, true)
                    this.player.setPosition(chair_x, chair_y - 4)
                    this.player.setDepth(5)
                    break
                
            }
        }
    }

    //////////////////////// FUNCTIONS ////////////////////////

    // 현재 접근한 의자
    seat(item){
        if(current_table === -1){
            current_chair = item.id % 4
            current_table = parseInt(item.id / 4)
            chair_x = item.x
            chair_y = item.y
            // console.log(parseInt(current_chair / 4), current_chair % 4)
        }
        if (roomTF[current_table]){
            // console.log(table_array[current_table])
            table_array[current_table].popup.visible = true
            table_array[current_table].title.visible = true
            table_array[current_table].detail.visible = true
        }
    }



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
            frameRate: 7
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
            frameRate: 7
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
            frameRate: 7
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
            frameRate: 7
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
            frameRate: 13,
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
            frameRate: 13,
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
            frameRate: 13,
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
            frameRate: 13,
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

export default barScene;