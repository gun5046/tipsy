import Phaser from 'phaser';

import ssafy_floor_wall from '../assets/ssafyMap/FloorAndGround.png';
import ssafy_office from '../assets/ssafyMap/Modern_Office.png';
import ssafy_deco from '../assets/ssafyMap/library.png';
import ssafy_table1 from '../assets/ssafyMap/table1.png';
import ssafy_table2 from '../assets/ssafyMap/table2.png';
import ssafy_table3 from '../assets/ssafyMap/table3.png';
import ssafy_table4 from '../assets/ssafyMap/table4.png';
import ssafy_chair from '../assets/ssafyMap/chair.png';
import ssafy_generic from '../assets/ssafyMap/Generic.png';
import ssafy_logo1 from '../assets/ssafyMap/ssafy_logo1.png';
import ssafy_logo2 from '../assets/ssafyMap/ssafy_logo2.png';

import jsonash from '../assets/character/ash.json'
import imageash from '../assets/character/ash.png'
import jsonlucy from '../assets/character/lucy.json'
import imagelucy from '../assets/character/lucy.png'

import ssafy_map from '../assets/ssafyMap/ssafy_map.json';

let sit = -1; // 전역변수 : 선택한 의자의 방향
let current_chair = -1
let current_table = -1
let chair_x = -1
let chair_y = -1

class SsafyScene extends Phaser.Scene {
    constructor () {
        super();
    }

    preload ()
    {
        // 플레이어 캐릭터 불러오기
        // image
        
        // Json (키: ash or lucy)
        this.load.atlas('ash', imageash, jsonash)
        this.load.atlas('lucy', imagelucy, jsonlucy)
        // console.log(this.load.atlas('lucy', imagelucy, jsonlucy))
        // 타일맵 이미지 불러오기
        this.load.image('tilesFloorWall', ssafy_floor_wall);
        this.load.image('tilesOffice', ssafy_office);
        this.load.image('tilesDeco', ssafy_deco);
        this.load.image('tilesGene', ssafy_generic);
        this.load.image('tileslogo1', ssafy_logo1);
        this.load.image('tileslogo2', ssafy_logo2);

        // tableObject의 책상 이미지 불러오기
        this.load.image('tilesTable1', ssafy_table1);
        this.load.spritesheet('tables1', ssafy_table1, {
            frameWidth: 80,
            frameHeight: 48,
        })
        this.load.image('tilesTable2', ssafy_table2);
        this.load.spritesheet('tables2', ssafy_table2, {
            frameWidth: 36,
            frameHeight: 68,
        })
        this.load.image('tilesTable3', ssafy_table3);
        this.load.spritesheet('tables3', ssafy_table3, {
            frameWidth: 38,
            frameHeight: 80,
        })
        this.load.image('tilesTable4', ssafy_table4);
        this.load.spritesheet('tables4', ssafy_table4, {
            frameWidth: 78,
            frameHeight: 36,
        })
        
        this.load.image('tilesChair', ssafy_chair);
        this.load.spritesheet('chairs', ssafy_chair, {
            frameWidth: 32,
            frameHeight: 64,
        })
        
        // 타일맵 Json 불러오기
        this.load.tilemapTiledJSON('map', ssafy_map)
        // this.load.tilemapTiledJSON('map', map2)
    }
    
    // 생성하기
    create ()
    {
        //// 맵 생성
        const map = this.make.tilemap({ key: "map", tileWidth: 32, tileHeight: 32});
        // 타일 생성
        const floorWallTileset = map.addTilesetImage("FloorAndGround",'tilesFloorWall');
        const officeTileset = map.addTilesetImage("Modern_Office",'tilesOffice');
        const decoTileset = map.addTilesetImage("library",'tilesDeco');

        const tableTileset1 = map.addTilesetImage("table1",'tilesTable1');
        const tableTileset2 = map.addTilesetImage("table2",'tilesTable2');
        const tableTileset3 = map.addTilesetImage("table3",'tilesTable3');
        const tableTileset4 = map.addTilesetImage("table4",'tilesTable4');

        const chairTileset = map.addTilesetImage("chair",'tilesChair');
        const GenericTileset = map.addTilesetImage("Generic",'tilesGene');
        const logoTileset1 = map.addTilesetImage("ssafylogo1",'tileslogo1');
        const logoTileset2 = map.addTilesetImage("ssafylogo2",'tileslogo2');
        // const decoTileset = map.addTilesetImage("tilefurnituredeco",'tilesFurniDeco');
        
        // 레이어 생성
        // 2배 확대 : setScale(2) -> setZoom 으로 대체
        // const layer1 = map.createLayer('floorLayer', floorTileset, 0, 0).setScale(2);
        const layer1 = map.createLayer('floorLayer', floorWallTileset, 0, 0)
        const layer3 = map.createLayer('shadowLayer', floorWallTileset, 0, 0)
        const layer2 = map.createLayer('wallLayer', floorWallTileset, 0, 0)
        const layer6 = map.createLayer('tableLayer', officeTileset, 0, 0)
        const layer4 = map.createLayer('decoLayer1', [officeTileset, decoTileset, GenericTileset, logoTileset1, logoTileset2], 0, 0)
        const layer5 = map.createLayer('decoLayer2', officeTileset, 0, 0)

        //// 타일에 충돌(Collision) 적용
        // Tiled에서 생성한 collides 적용
        layer2.setCollisionByProperty({ collides: true });
        layer6.setCollisionByProperty({ collides: true });
        
        // Tiled에서 찍은 타일 번호 값 적용
        // 벽
        // layer2.setCollisionBetween(803, 1797);
        // 책상
        // layer3.setCollisionBetween(4195, 4228);
        

        //// 플레이어
        // image로 캐릭터 선택
        // this.player = this.physics.add.sprite(100, 150, 'character')

        // JSON으로 불러온 캐릭터 적용
        // 캐릭터 선택
        this.characterKey = 'lucy'
        this.imageName = 'Lucy'
        // this.characterKey = 'ash'
        // this.imageName = 'Ash'

        // 캐릭터 & 시작 위치 설정
        this.player = this.physics.add.sprite(100, 150, this.characterKey).setDepth(32)


        //// tableObject 레이어 생성
        const tableLayer = map.getObjectLayer('tableObject');
        const tables = this.physics.add.staticGroup();
        tableLayer.objects.forEach((tableObj, i) => {
            // console.log(tableObj.gid)
            if (tableObj.gid === 5201) {
                const item = tables.get(tableObj.x + tableObj.width * 0.5, tableObj.y - tableObj.height * 0.5, 'tables2', tableObj.gid - tableTileset2.firstgid)
            } else if (tableObj.gid === 5233) {
                const item = tables.get(tableObj.x + tableObj.width * 0.5, tableObj.y - tableObj.height * 0.5, 'tables1', tableObj.gid - tableTileset1.firstgid)
            } else if (tableObj.gid === 5234) {
                const item = tables.get(tableObj.x + tableObj.width * 0.5, tableObj.y - tableObj.height * 0.5, 'tables3', tableObj.gid - tableTileset3.firstgid)
            } else {
                const item = tables.get(tableObj.x + tableObj.width * 0.5, tableObj.y - tableObj.height * 0.5, 'tables4', tableObj.gid - tableTileset4.firstgid)
            } 
        })

         //// chairObject 레이어 생성
         const chairLayer = map.getObjectLayer('chairObject');
         const chairs = this.physics.add.staticGroup();

         chairLayer.objects.forEach((chairObj, i) => {
             const item = chairs.get(chairObj.x + chairObj.width * 0.5, chairObj.y - chairObj.height * 0.5, 'chairs', chairObj.gid - chairTileset.firstgid).setDepth(10)
             const id = Number(`${i}`)            
             item.id = id
             item.sit = chairObj.gid- chairTileset.firstgid
            //  console.log(id, chairObj.gid, chairTileset.firstgid)
            //  console.log(people.includes(id))
            //  if (people.indexOf(id) >= 0){
            //      this.add.image(item.x, item.y, 'profile').setDepth(10);
            //  }
            //  else{
            this.physics.add.overlap(this.player, item, ()=>this.seat(item), null, this);
            //  }
         })
 


        //// 플레이어에 충돌 적용
        // 왜 안돼!!!!!!
        // 플레이어 월드 바깥 이동제한
        // this.player.setCollideWorldBounds(true);
        
        // 타일에 충돌 적용
        this.physics.add.collider(this.player, [layer2, layer6, tables]);
        // this.physics.add.collider(this.player, layer3);
        // this.physics.add.collider(this.player, layer4);

        //// 키보드 입력기
        this.cursors = this.input.keyboard.createCursorKeys();
        // 키보드 입력키 추가
        this.keyZ = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.Z)
        this.keyX = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.X)
        // this.wasdKeys = this.input.keyboard.addKeys({
        //     up: Phaser.Input.Keyboard.KeyCodes.W,
        //     down: Phaser.Input.Keyboard.KeyCodes.S,
        //     left: Phaser.Input.Keyboard.KeyCodes.A,
        //     right: Phaser.Input.Keyboard.KeyCodes.D,
        //   });
        

        //// 카메라 설정 ( 순서 중요!!!!!!!! )
        // 1. 경계 밖으로 카메라가 나가지 않도록 설정
        this.cameras.main.setBounds(0, 0, map.widthInPixels, map.heightInPixels);
        // 2. 플레이어를 중앙으로 카메라 이동
        this.cameras.main.startFollow(this.player);
    
        // 카메라로 맵 2배 확대 (setScale(2) 대신 가능)
        this.cameras.main.setZoom(1.2);


        //// 애니메이션 적용
        // 사용할수있는 모든 프레임 이름 추출
        // const frameNames= this.textures.get(`${characterKey}`).getFrameNames();
        // console.log(frameNames)

        // 애니메이션 함수 적용 (애니메이션 움직임을 createAnims함수로 만듬)
        this.createAnims(this.characterKey, this.imageName)  
    }
    
    // 실시간 반영
    update() {
        // 디버그용 (1초 간격으로 플레이어 좌표를 콘솔에 출력)
        // console.log(this.player.body.x, this.player.body.y); 

        let speed = 200;
        // Shift 키를 누르면서 이동하면 빠르게 이동
        if (this.keyZ.isDown) {speed = 300;}
    

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
            current_table = -1
        } else if (this.cursors.right.isDown) {
            this.player.setVelocityX(speed);
            this.player.anims.play(`${this.characterKey}_run_right`, true);
            current_table = -1
        } else if (this.cursors.up.isDown) {
            this.player.setVelocityY(-speed);
            this.player.anims.play(`${this.characterKey}_run_up`, true);
            current_table = -1
        } else if (this.cursors.down.isDown) {
            this.player.setVelocityY(speed);
            this.player.anims.play(`${this.characterKey}_run_down`, true);
            current_table = -1
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
        if (this.keyX.isDown && current_table >= 0) {
            // console.log(prevVelocity)
            // this.player.anims.play(`${this.characterKey}_sit_left`, true);
            // console.log(this.overlapChair)

            // 나중에 의자 모양에 따라 모션이 바뀌는 걸로 조정 !!!!!!!!!!!!!!!!!!!!!!
            switch(sit){
                case 9: //옆면
                    this.player.anims.play(`${this.characterKey}_sit_right`, true);
                    this.player.setPosition(chair_x + 2, chair_y - 13)
                    break
                case 11: //뒷모습
                    this.player.anims.play(`${this.characterKey}_sit_up`, true)
                    this.player.setPosition(chair_x, chair_y - 4)
                    this.player.setDepth(5)
                    break
                case 6: //앞면
                    this.player.anims.play(`${this.characterKey}_sit_down`, true)
                    this.player.setPosition(chair_x, chair_y - 4)
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
            sit = item.sit
            chair_x = item.x
            chair_y = item.y
            // console.log(parseInt(current_chair / 4), current_chair % 4)
        }
        // // 한자리에 계속 머무를 때
        // if(current_chair === item.id){
        //     return
        // }
        // // 다른 자리로 바꿨을 때
        // else{
        //     current_chair = item.id
        //     current_table = parseInt(current_chair / 4)
        //     sit = item.sit
        //     // console.log(this.sit)
        //     console.log(parseInt(current_chair / 4), current_chair % 4)
        // }
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

export default SsafyScene;