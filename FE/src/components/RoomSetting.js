import React from 'react';
import { useRef, useState, useEffect} from "react"
import axios from 'axios';

import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from "@mui/material/Grid";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import { Typography } from "@mui/material";

import { useDispatch, useSelector } from 'react-redux'
import { infoActions } from '../redux/infoSlice';
import { useNavigate } from 'react-router-dom';


const RoomSetting = () => {
  const [open, setOpen] = useState(true);
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
  const [newTag, setNewTag] = useState('')
  const [tag, setTag] = useState(roomState.hashtag)
  
  //특수 문자 정규표현식
  const regExp = /\W|\s/g; 

  const dispatch = useDispatch()
  const navigate = useNavigate()
  const currentScene = useSelector((state) => state.game.scene)
  const currentChair = useSelector((state) => state.game.chair)
  const currentTable = useSelector((state) => state.game.table)
  const currentStore = useSelector((state) => state.game.store)
  const currentUid = useSelector((state) => state.auth.uid)
  const url = 'http://i8d207.p.ssafy.io:8083/room'
  // const url = 'http://localhost:8083/room'
  
  const handleClose = () => {
    setOpen(false);
  };
  console.log(currentStore)
  useEffect(() => {
    // 방이름 추가 && 구미이면 1번 서울이면 2번.... 이런식으로 방코드 저장
    if (currentTable !== -1){
        if (String(currentTable).length === 1) {
          setRoomState({
            ...roomState,
            code:`${currentStore}0${currentTable}`
          });
          
        } else {
          setRoomState({
            ...roomState,
            code:`${currentStore}${currentTable}`
          });
        }
      } 
  }, [currentChair, currentTable, currentScene])
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
          hashtag: roomState.hashtag
        }
      )
      .then((res) => {
        // 방번호
        console.log(res.data);
        if (res.data !== "exist") {
          setRoomNum(res.data)
          console.log("방 생성 성공 //////////////////////////");
          dispatch(infoActions.getRoomNum(res.data))
        }else{
          alert("asd")
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
      }
    );
  }
  
     
    // code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]
    const enterRoom = () => {
      console.log("방 입장 실행");
      axios
        .post(`${url}/entry`, { 
          code: RoomNum,
          uid: currentUid,
          password: roomState.password,
          position: currentChair,
         })
        .then((res) => {
          console.log('입장성공');
          console.log('111111',currentUid)
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

  
  const addhashtag = (newChips) => {
    // console.log(roomState.hashtag);
    setChips(newChips)
  }

  ///공개방인지 비공개방인지 판단
  function EnableDisablePassword() {
    if (document.getElementById("Public").checked) {
      document.getElementById("Private").innerText = "비공개방";
      document.getElementById("password").disabled = false;
    } else {
      document.getElementById("Private").innerText = "공개방";
      document.getElementById("password").disabled = true;
      setRoomState({...roomState, password: ""})
    }
  }




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


  const plusHashtag = () => {
    if(!tag.includes(newTag)){

      // const regExp = /[!?@#$%^&*():;+-=~{}<>\_\[\]\|\\\"\'\,\.\/\`\₩]/g;
      // if(regExp.test(newTag)) {
      //   alert("특수문자는 입력하실수 없습니다.");
      //   return
      // }
      // if(newTag.search(/\s/) !== -1) {
      //   alert("공백은 입력하실수 없습니다.");
      //   return // 스페이스가 있는 경우
      // } 

      // if(newTag.length <= 0 || newTag.length >= 9) {
      //   alert('8자리이하로 입력해주세요.')
      //   return
      // }

      const newList = tag
      newList.push(newTag)
      setTag(newList)
      setNewTag('')
      setRoomState({...roomState, hashtag: tag})
    }
    // console.log(roomState.hashtag)
  }
  //입력 인풋값 바꿈
  const changeInput = (e) => {
    setNewTag(e.target.value)
  }
  const removeHashtag = (e) => {
    console.log(e.target.id);
    const newList = tag.filter((element) => element !== e.target.id)
    setTag(newList)
    setRoomState({...roomState, hashtag: tag})
  }

  return (
    <div>
      <Dialog open={open} onClose={handleClose} 
      fullWidth 
      PaperProps={{ style: { 
        borderRadius: 50,
        backgroundColor: "#fafafa",
        opacity: 0.8
      }}} 
      >
        <DialogTitle fullwidth variant="h5" textAlign='center' >테이블 설정</DialogTitle>
        <DialogContent>
          {/* <DialogContentText>
            테이블에 처음으로 앉으셨습니다. 테이블 설정을 진행해주세요.
          </DialogContentText> */}
          <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography variant="h6" sx={{ mt: 3.5 }}>방 제목</Typography>
          </Grid>
          <Grid item xs={9}>
            <TextField
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
              InputProps={{ style: { borderRadius: 50 }}}
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
              InputProps={{ style: { borderRadius: 50}}}
              inputProps={{ style: {textAlign: 'center'} }}
            />
          </Grid>
        </Grid>
        <br />        
        <Grid container spacing={2}>
          <Grid item xs={3}>
            <Typography variant="h6" sx={{ mt: 3.5 }}>해시태그</Typography>
          </Grid>
          <Grid item xs={8}>
            <TextField
              margin="normal"
              fullWidth
              id="hashTag"
              placeholder="관심 있는 해시태그를 입력해주세요"
              autoComplete="text"
              ref={hashtagInput}
              name="hashtag"
              value={newTag}
              onChange={changeInput}
              InputProps={{ style: { borderRadius: 50 }}}
            />
          </Grid>
          <Grid item xs={1} sx={{ ml:-3, mt:2.5 }} >
          <Button style={{
            borderRadius: 35,
            height: 50
          }}
            onClick={plusHashtag}>추가</Button>
          </Grid>
        </Grid>
        <Grid container sx={{ mb:3 }} style={{justifyContent: 'center'}}>
          {
            roomState.hashtag.map((e)=>{return <Button className='hashtag' id={e} key={e} onClick={removeHashtag}>#{e}</Button>})
          }
        </Grid>


        <Grid container spacing={2}>
          <Grid item xs={2.5} sx={{ mt:3.5 }}>
            <Typography variant="h6" id="Private">비공개방</Typography>
          </Grid>
          <Grid item xs={0.5} sx={{ ml:-5, mt:3, mr:5 }}>
            <Switch id="Public" defaultChecked sx={{ color: "white" }} onChange={EnableDisablePassword} />
          </Grid>
          {/* <Grid item xs={1.5}>
            <Typography sx={{ mt: 4 }}>비공개방</Typography>
          </Grid> */}
          <Grid item xs={9}>
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
              InputProps={{ style: { borderRadius: 50 }}}
            />
          </Grid>
        </Grid>
        </DialogContent>
        <DialogActions sx={{ mb:3 }} style={{justifyContent: 'center'}}>
          <Button variant="contained" style={{
            borderRadius: 35,
            backgroundcolor: "#bdbdbd"}}
            onClick={handleSubmit}>입장하기</Button>
          <Button onClick={handleClose}>더 둘러보기</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default RoomSetting;
