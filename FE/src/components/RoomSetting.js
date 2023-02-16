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
import Switch from '@mui/material/Switch';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

import { useDispatch, useSelector } from 'react-redux'
import { infoActions } from '../redux/infoSlice';
import { useNavigate } from 'react-router-dom';
import { set } from 'lodash';


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

  const [open, setOpen] = useState(true);

  const dispatch = useDispatch()
  const navigate = useNavigate()
  const currentScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentUid = useSelector((state) => state.auth.uid)
  const isCreate = useSelector((state) => state.info.createRoom)

  if (isCreate) {
    setOpen(true)
  }

  const [chips, setChips] = useState([])

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
          // dispatch(infoActions.isCreateRoom(false))
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

  const handleClose = () => {
    setOpen(false);
  };
  
  //// 입장하기
  const handleSubmit = () => {
    if (roomState.title.length < 1) {
      // alert("작성자는 최소 1글자 이상 입력해주세요.")
      titleInput.current.focus();
      return;
    }

    // console.log(roomState);
    // 앉으면 방만들기
    createRoom(roomState)
  }
  
  useEffect(() => {
    enterRoom(RoomNum, roomState, currentChair)
  }, [RoomNum])


  //////////////////////////////////////////
  const addhashtag = (newChips) => {
    console.log(roomState.hashtag);
    setChips(newChips)
  }
  
  ///공개방인지 비공개방인지 판단
  function EnableDisablePassword() {
    console.log(document.getElementById("Public"));
    if (document.getElementById("Public").checked) {
      document.getElementById("password").disabled = false;
    } else {
      console.log("not pwd");
      document.getElementById("password").disabled = true;
    }
  }



  return (
    <div>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>테이블 설정</DialogTitle>
        <DialogContent>
          <DialogContentText>
            테이블에 처음으로 앉으셨습니다. 테이블 설정을 진행해주세요.
          </DialogContentText>
          <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography variant="h6" sx={{ mt: 3.5 }}>방 제목</Typography>
          </Grid>
          <Grid item xs={9}>
            <TextField
              borderRadius="70%"
              margin="normal"
              required
              id="title"
              autoComplete="text"
              autoFocus
              fullWidth
              ref={titleInput}
              name="title"
              value={roomState.title}
              onChange={handleChangeState}
              style={{ textAlign: "center" }}
            />
          </Grid>
        </Grid>
        <br />
        <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography variant="h6" sx={{ mt: 1.5 }}>최대 인원</Typography>
          </Grid>
          <Grid item xs={9}>
            <TextField
              disabled
              id="outlined-disabled"
              defaultValue="6"
              fullWidth
              type="number"
              inputProps={{style: {fontSize: 18}}}
            />
          </Grid>
        </Grid>
        <br />        
        <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography variant="h6" sx={{ mt: 3.5 }}>해시태그</Typography>
          </Grid>
          <Grid item xs={9}>
            <TextField
              margin="normal"
              fullWidth
              id="hashTag"
              placeholder="관심 있는 해시태그를 입력해주세요"
              autoComplete="text"
              autoFocus
              ref={hashtagInput}
              name="hashtag"
              value={roomState.hashtag}
              onChange={handleChangeState}
            />
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography sx={{ ml:4, mt:4 }}>공개방</Typography>
          </Grid>
          <Grid item xs={1} sx={{ ml:-2, mt: 3, mr:3 }}>
            <Switch id="Public" defaultChecked sx={{ color: "white" }} onChange={EnableDisablePassword} />
          </Grid>
          {/* <Grid item xs={1.5}>
            <Typography sx={{ mt: 4 }}>비공개방</Typography>
          </Grid> */}
          <Grid item xs={7} sx={{ ml:3 }}>
            <TextField
              margin="normal"
              fullWidth
              type="password"
              id="password"
              autoComplete="current-password"
              ref={passwordInput}
              name="password"
              placeholder='password'
              value={roomState.password}
              onChange={handleChangeState}
            />
          </Grid>
        </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleSubmit}>입장하기</Button>
          <Button onClick={handleClose}>더 둘러보기</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default RoomSetting;