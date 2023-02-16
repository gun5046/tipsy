import phaser from 'phaser';
import React, { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ssafyConfig from '../phaser/ssafyConfig';
import axios from "axios";
import styled from "styled-components"
import RoomSetting2 from '../components/RoomSetting2';
import CheckPw from '../components/CheckPw';
// import Setting from '../components/Setting';

// 리덕스
import { useSelector } from 'react-redux'
import { infoActions } from '../redux/infoSlice';
// useSelector 데이터 읽기
// useDispatch 데이터 전달

const GameViewContainer = styled.section`
  z-index: 1000;
  position: absolute;
  width: 50vh;
  top: 10vh;
  left: 80vh;
  // padding: 20px;
  
  `;
  // background: white;


// 게임 화면 뷰 영역 컴포넌트
const SsafyView = () => {
  // 게임 화면 초기화
  const phaserEl = useRef(null);
  const [startGame, setStartGame] = useState()
  const navigate = useNavigate();
  const changeScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentRoom = useSelector((state) => state.info.roomNumber)
  const currentPassword = useSelector((state) => state.info.roomPassword)
  const currentUid = useSelector((state) => state.auth.uid)
  const isRoom = useSelector((state) => state.info.createRoom)
  const isPublic = useSelector((state) => state.info.publicRoom)
  // const [isRoom, setisRoom] = useState(false)
  console.log(`방만들기 : ${isRoom}`)
  console.log(`공개방 : ${isPublic}`)
  // const [RoomNum, setRoomNum] = useState()
  

  // 건물번호 1,2,3
  const url = 'http://i8d207.p.ssafy.io:8083/room'

  ///// axios ///////////////////////////////////////////////
  // 테이블 정보 가져오기 (1번 구미) - 미리가져옴
  const getTable1 = () => {
    axios
      .get(`${url}/1`)
      .then((res) => {
        console.log("1번 건물 테이블 정보");
        console.log(res.data);
        dispatch(infoActions.getTable1(res.data))
      })
      .catch((e) => {
        console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
          navigate('/')
        }
      });
  };

  // code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]
  const enterRoom = () => {
    console.log("방 입장 실행");
    axios
      .post(`${url}/entry`, { 
        code: currentRoom,
        id: currentUid,
        password: currentPassword,
        position: currentChair,
       })
      .then((res) => {
        console.log('입장성공 ssafyView');
        console.log(res.data);
        if (res.data == "success" && currentRoom) {
          console.log(currentRoom);
          navigate(`/meeting/${currentRoom}`)
        } 
      })
      .catch((e) => {
        console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
          navigate('/')
        } else if (e == 'banned user') {
          alert('들어갈 수 없는 방 입니다.')
        }
      });
  };
  /////////////////////////////////////////////


  // 테이블 정보 가져오기 엑시오스 실행
  useEffect(() => {
    getTable1()
  }, [])

  // 메인으로 나가기
  useEffect(() => {
    console.log(changeScene)
    if (changeScene ==='street') {
      navigate('/mainstreet')
    }
  }, [changeScene])

  
  
  //////////////////////////////////////
  // 공개방이고 의자랑 테이블이 넘어오면 미팅 페이지 이동 (103 : 1번건물에 3번 방)
  useEffect(() => {
    if (currentTable !== -1 && isPublic && isRoom){
      enterRoom(currentRoom,currentUid, currentPassword, currentChair)
    }
  }, [currentChair, currentTable])
  

  useEffect(() => {
    // console.log('페이져 불러옴');
    if (!startGame && phaserEl.current) {
      const newGame = new phaser.Game(ssafyConfig)
      // phaserEl레퍼런스에 phaserConfig 로 scene을 생성
      phaserEl.current = newGame
      setStartGame(newGame)
    }
    // 언마운트 시 destroy
    return () => {
      startGame?.destroy(true)
    }
  }, [ssafyConfig, phaserEl, startGame])

  
  return (
    <div>
      <GameViewContainer>
        {isRoom && <RoomSetting2/>}
        {!isPublic && <CheckPw/>}
        {/* <RoomSetting/> */}
      </GameViewContainer>
      
      <div ref={phaserEl} className="game-container"></div>
    </div>
  );
};

export default SsafyView;