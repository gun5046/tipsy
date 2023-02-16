import React from 'react';
import { useContext, useRef, useState, useEffect, useCallback } from "react"
import axios from 'axios';
// import { useNavigate } from "react-router-dom"
// import styled from "styled-components"

import Grid from "@mui/material/Grid";
import Link from "@mui/material/Link";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import { Typography } from "@mui/material";
import { useDispatch, useSelector } from 'react-redux'
import { infoActions } from '../redux/infoSlice';
import styled from "styled-components"
import { useNavigate } from 'react-router-dom';


const GameSettingContainer = styled.section`
  // z-index: 1000;
  // position: absolute;
  // width: 50vh;
  // top: 10vh;
  // left: 80vh;
  padding: 20px;
  background: white;
  
  `;

const RoomSetting = () => {

  const titleInput = useRef();
  const hashtagInput = useRef();
  const passwordInput = useRef();
  const [RoomNum, setRoomNum] = useState('')
  const [roomState, setRoomState] = useState({
    code: '',
    title: '',
    max: 6,
    password: '',
    entrance: 0,
    silence: 0,
    hashtag: []
  });

  const dispatch = useDispatch()
  const navigate = useNavigate()
  const currentScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentUid = useSelector((state) => state.auth.uid)
  const url = 'http://i8d207.p.ssafy.io:8083/room'

 
  useEffect(() => {
    // 방이름 추가 && 구미이면 1번 서울이면 2번.... 이런식으로 방코드 저장
    if (currentTable !== -1){
      if (String(currentTable).length === 1) {
        setRoomState({
          ...roomState,
          code:`10${currentTable}`
        });
        
      } else {
        setRoomState({
          ...roomState,
          code:`1${currentTable}`
        });
      }
    }
  }, [currentChair, currentTable])
  // console.log(currentChair, currentTable)
  console.log(roomState.code)


  ////////////// axios
  // 앉으면 방만들기
  const createRoom = () => {
    
    console.log('createRoom 실행');
    // console.log(roomState);
    axios
      .post(`${url}`,
        { 
          code: roomState.code,
          title: roomState.title,
          max: 6,
          password: roomState.password,
          entrance: 0,
          silence: 0,
          hashtag: [roomState.hashtag]
        }
      )
      .then((res) => {
        // 방번호
        console.log("방 생성 성공");
        console.log(res.data);
        if (res.data) {
          setRoomNum(res.data)
          console.log("방 생성 성공 //////////////////////////");
          dispatch(infoActions.getRoomNum(res.data))
          dispatch(infoActions.isCreateRoom(false))
        }
      })
      .catch((e) => {
        console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
          navigate('/')
        } else {
          alert('다시 입력해주세요.')
        }
      });
  }
  
     
    // code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]
    const enterRoom = () => {
      console.log("방 입장 실행");
      axios
        .post(`${url}/entry`, { 
          code: RoomNum,
          id: currentUid,
          password: roomState.password,
          position: currentChair,
         })
        .then((res) => {
          console.log('입장성공');
          console.log(res.data);
          if (res.data == "success" && RoomNum) {
            console.log(RoomNum);

            navigate(`/meeting/${RoomNum}`)
         

            // 설정이 작성되면 작성 폼의 데이터를 초기화
            setRoomNum('')
            setRoomState({
              code: '',
              title: '',
              max: 6,
              password: '',
              entrance: 0,
              silence: 0,
              hashtag: []
            });
          } 
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
  /////////////
  

  //// 입력함수
  const handleChangeState = (e) => {
    setRoomState({
      // state의 객체가 길면 spread 연산자를 사용한다.
      // 변경하고자하는 값을 spread 연산자 밑에 쓴다.
      ...roomState,
      [e.target.name]: e.target.value
    });
  };

  
  //// 입장하기
  const handleSubmit = () => {
    if (roomState.title.length < 1) {
      // alert("작성자는 최소 1글자 이상 입력해주세요.")
      // focus
      titleInput.current.focus();
      return;
    }

    // if (roomState.password.length < 4) {
    //   passwordInput.current.focus();
    //   return;
    // }

    // console.log(roomState);
     // 앉으면 방만들기
    createRoom(roomState)
  }
  
  useEffect(() => {
    enterRoom(RoomNum, roomState, currentChair)
  }, [RoomNum])

  return (
    <GameSettingContainer>
      <Typography variant="h3">Settings</Typography>
      <Typography variant="h7">테이블에 처음으로 앉으셨습니다.<br />테이블 설정을 진행해주세요.</Typography>
      <form>
        <TextField
          margin="normal"
          required
          fullWidth
          id="title"
          label="제목을 입력주세요"
          autoComplete="text"
          autoFocus

          ref={titleInput}
          name="title"
          value={roomState.title}
          onChange={handleChangeState}
        />
        <br/>
        <TextField
          disabled
          id="outlined-disabled"
          label="최대 인원"
          defaultValue="6"
          fullWidth
        />
        <TextField
          margin="normal"
          fullWidth
          id="hashTag"
          label="관심 있는 해시태그를 입력해주세요"
          autoComplete="text"
          autoFocus

          ref={hashtagInput}
          name="hashtag"
          value={roomState.hashtag}
          onChange={handleChangeState}
        />
        <Button variant="contained">추가</Button>
        <br/>
        <FormControl>
        <RadioGroup
          row
          aria-labelledby="demo-row-radio-buttons-group-label"
          name="row-radio-buttons-group"
        >
          <FormControlLabel value="Public" control={<Radio />} label="공개" />
          <FormControlLabel value="Private" control={<Radio />} label="비공개" />
        </RadioGroup>
        </FormControl>
        <br/>
        <TextField
          margin="normal"
          fullWidth
          label="Password"
          type="password"
          id="password"
          autoComplete="current-password"

          ref={passwordInput}
          name="password"
          value={roomState.password}
          onChange={handleChangeState}
        />
        <Button
          // type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
          onClick={handleSubmit}
        >
          입장하기
        </Button>
        <Grid item>
          <Link href="#" variant="body2">
            더 둘러보기
          </Link>
        </Grid>
      </form>
    </GameSettingContainer>
  );
}

export default RoomSetting;