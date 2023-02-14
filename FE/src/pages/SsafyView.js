import phaser from 'phaser';
import React, { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ssafyConfig from '../phaser/ssafyConfig';
import axios from "axios";
import styled from "styled-components"
// import RoomSetting from '../components/RoomSetting';

// 리덕스
import { useSelector } from 'react-redux'
// useSelector 데이터 읽기
// useDispatch 데이터 전달

const GameViewContainer = styled.section`
  z-index: 6;
  position: absolute;
  color: white;
  top: 0px;
  left: 0px;

`;


// 게임 화면 뷰 영역 컴포넌트
const SsafyView = () => {
  // 게임 화면 초기화
  const phaserEl = useRef(null);
  const [startGame, setStartGame] = useState()
  const navigate = useNavigate();
  const changeScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const RoomNum = ''

  // const [RoomNum, setRoomNum] = useState()
  const storeNum = 1
  

  // 건물번호 1,2,3
  const url = 'http://i8d207.p.ssafy.io:8083'


  const getTable = () => {
    axios
      .get(`${url}/room/${storeNum}`)
      .then((res) => {
        // console.log({ storeNum } + "번 건물 테이블 정보");
        // console.log(res);
      })
      .catch((e) => {
        console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
        }
      });
  };

  // 방생성
  const createRoom = () => {
    console.log(room);
    axios
      .post(url, { 
        code: room.code,
        title: room.code,
        max: room.max,
        password: room.password,
        antrance: room.antrance,
        silence: room.silence,
        hashtag: [room.hashtag]
      })
      .then((data) => {
        console.log(data);
      })
      .catch((e) => {
        console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
        }
      });
  };

  // 엑시오스 실행
  useEffect(() => {
    getTable()
  }, [])

  // 메인으로 나가기
  useEffect(() => {
    console.log(changeScene)
    if (changeScene ==='street') {
      navigate('/mainstreet')
    }
  }, [changeScene])

  // 미팅 페이지 이동 (103 : 1번건물에 3번 방)
  useEffect(() => {
    if (currentTable !== -1){
      if (String(currentTable).length === 1) {
        navigate(`/meeting/10${currentTable}`)
      
      } else {
        navigate(`/meeting/1${currentTable}`)
      }
        
      console.log(currentChair, currentTable)
      console.log(RoomNum)
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
      {/* <GameViewContainer>
        <RoomSetting/>
      </GameViewContainer> */}
      <div ref={phaserEl} className="game-container"></div>
    </div>
  );
};

export default SsafyView;