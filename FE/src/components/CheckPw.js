import React, { useEffect, useRef, useState } from 'react';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useDispatch } from 'react-redux';
import axios from 'axios';
import { infoActions } from '../redux/infoSlice';

const CheckPw = () => {
  const [open, setOpen] = useState(true);
  const [checkPassword, setCheckPassword] = useState('');
  const passwordInput = useRef()
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const roomNumber = useSelector((state) => state.info.roomNumber)
  // const roomPassword = useSelector((state) => state.game.roomPassword)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentUid = useSelector((state) => state.auth.uid)
  const url = 'http://i8d207.p.ssafy.io:8083/room'

  // author, content 이벤트핸들러 합치기
  const handleChangeState = (e) => {
    setCheckPassword(e.target.value);
  };

  const handleClose = () => {
    setOpen(false);
  };

  //////////////// axios
  // code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]
  const enterRoom = () => {
    console.log("비밀방 입장 실행");
    axios
      .post(`${url}/entry`, { 
        code: roomNumber,
        id: currentUid,
        password: String(checkPassword),
        position: currentChair,
       })
      .then((res) => {
        console.log('비밀방입장성공');
        console.log(res.data);
        if (res.data == "success" && roomNumber) {
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
        } else if (e == 'incorrect password') {
          passwordInput.current.focus();
        } else if (e == 'banned user') {
          alert('들어갈 수 없는 방 입니다.')
        }
      });
  };

  //// 입장하기
  const handleSubmit = () => {
    if (checkPassword.length < 1) {
      passwordInput.current.focus();
      return;
    }
    enterRoom(roomNumber,currentUid,checkPassword,currentChair)
  }


  return (
    <div>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>비밀번호</DialogTitle>
        <DialogContent>
          <DialogContentText>
            비공개 방 입니다. 비밀번호를 입력하세요.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            // label="password"
            fullWidth
            variant="standard"

            id='password'
            ref={passwordInput}
            type='password'
            name="password"
            value={checkPassword}
            onChange={handleChangeState}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>취소하기</Button>
          <Button onClick={handleSubmit}>입장하기</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default CheckPw