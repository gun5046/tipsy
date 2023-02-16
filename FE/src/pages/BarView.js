import phaser from 'phaser';
import React, { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import barConfig from '../phaser/barConfig';
import axios from "axios";
import styled from "styled-components"
import RoomSetting from '../components/RoomSetting';
import CheckPw from '../components/CheckPw';

// 리덕스
import { useDispatch, useSelector } from 'react-redux'
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

const BarView = () => {
  const phaserEl = useRef(null);
  const [startGame, setStartGame] = useState()
  const navigate = useNavigate();
  const dispatch = useDispatch()
  const changeScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentRoom = useSelector((state) => state.info.roomNumber)
  const currentPassword = useSelector((state) => state.info.roomPassword)
  const currentUid = useSelector((state) => state.auth.uid)
  const isCreate = useSelector((state) => state.info.createRoom)
  const isPublic = useSelector((state) => state.info.publicRoom)
  // const [isRoom, setisRoom] = useState(false)
  console.log(`방만들기 : ${isCreate}`)
  console.log(`공개방 : ${isPublic}`)
  // const [RoomNum, setRoomNum] = useState()

  // 건물번호 1,2,3
  const url = 'http://i8d207.p.ssafy.io:8083/room'

 
  ///// axios ///////////////////////////////////////////////
  // 테이블 정보 가져오기 (2번 bar) - 미리가져옴
  const getTable2 = () => {
    axios
      .get(`${url}/2`)
      .then((res) => {
        console.log("2번 건물 테이블 정보");
        console.log(res.data);
        dispatch(infoActions.getTable2(res.data))
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
    // console.log("공개방 입장 실행");
    axios
      .post(`${url}/entry`, { 
        code: currentRoom,
        uid: currentUid,
        password: currentPassword,
        position: currentChair,
       })
      .then((res) => {
        // console.log('입장성공 ssafyView');
        if (res.data !== "exist") {
          // console.log(roomNumber);
          navigate(`/meeting/${roomNumber}`)
          dispatch(infoActions.isPublic(true))
          setCheckPassword('')
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
    getTable2()
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
    if (currentTable !== -1 && isPublic && isCreate == false){

      console.log('11111111111111111111111111111111111111111')
      enterRoom(currentRoom, currentUid, currentPassword, currentChair)
    }
  }, [currentChair, currentTable])
  

  useEffect(() => {
    if (!startGame && phaserEl.current) {
      const newGame = new phaser.Game(barConfig)
      phaserEl.current = newGame
      setStartGame(newGame)
    }
    return () => {
      startGame?.destroy(true)
    }
  }, [barConfig, phaserEl, startGame])

  
  return (
    <div>
      <GameViewContainer>
        {isCreate && <RoomSetting/>}
        {!isPublic && <CheckPw/>}
        {/* <RoomSetting/> */}
      </GameViewContainer>
      
      <div ref={phaserEl} className="game-container"></div>
    </div>
  );
};

export default BarView;