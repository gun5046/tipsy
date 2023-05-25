import React from 'react';
import { useState } from 'react';
import { useSelector } from "react-redux";
import axios from "axios";

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import Chip from '@mui/material/Chip';

import TextField from '@mui/material/TextField';
import { authSubmit } from "../redux/authSlice";
import { infoActions } from "../redux/infoSlice";
import { useDispatch } from 'react-redux';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
  textAlign: 'center',
};

let overlap = false
const MypageModal =() => {

    const dispatch = useDispatch()

    //수정여부 확인
    const [isUpdate, setIsUpdate] = useState(true)
    //특수 문자 정규표현식
    const regExp = /^[ㄱ-힣a-zA-Z0-9]+$/; 

    const [newInterest, setNewInterest] = useState('')
    //입력받은 유저 정보
    const currentUser = useSelector((state) => state.auth)
    const [state, setState] = useState(currentUser)
    //관심사 배열
    const [interest, setInterest] = useState(state.interest.split(","))
    //관심사 입력시 정보 변경

    const plusInteresting = () => {
    if (!interest.includes(newInterest)){
        if(!regExp.test(newInterest)){
        alert("특수문자, 공백은 입력하실수 없습니다.");
        return
        }
        
        if (newInterest.length <= 0 || newInterest.length >= 9) {
        alert('8자리이하로 입력해주세요.')
        return
        }
        const newList = interest
        newList.push(newInterest)
        setInterest(newList)
        setNewInterest('')
        setState({...state, interest: interest.join(',')})
    }

    }

    //관심사 제거
    const removeInterst = (e) => {
    const newList = interest.filter((element) => element !== e.target.id)
    setInterest(newList)
    setState({...state, interest: interest.join(',')})
    }

    //닉네임 입력시 state의 값을 바꿈
    const NicknameInput = (e) => {
    const newNickname = e.target.value
    setState({
        ...state,
        'nickname': newNickname
    })
    overlap = false
    }

    //관심사 입력 인풋값 바꿈
    const changeInput = (e) => {
        setNewInterest(e.target.value)
    }

    //서버와 통신 
    const check = ()=> {

    if(!regExp.test(state.nickname)){
        alert("특수문자, 공백은 입력하실수 없습니다.");
        return
    }

    if (state.nickname.length <= 0 || state.nickname.length >= 9) {
        alert('8자리이하로 입력해주세요.')
        return
    }

    axios.get( 'http://i8d207.p.ssafy.io:8081/user/nickname',  {
        params: {
        nickname: state.nickname
        }
    })
    .then((res) => {
        overlap = res.data
        console.log(overlap)
        if(overlap) {
        alert('사용 가능한 닉네임입니다.')
        } else {
        alert('중복된 닉네임 입니다')
        }
    })
    }
    
    
    //제출 
    const submit = () => {
        console.log(state)
        if (overlap) {
            // axios.post('http://127.0.0.1:8081/user/account', state)
            axios.put( 'http://i8d207.p.ssafy.io:8081//user/mypage/modify', state)
            .then((res) => {
                console.log(res)
                dispatch(authSubmit(state))
                setIsUpdate(!isUpdate)
                alert('제출 완료')
                dispatch(infoActions.isMyPage(false))
            })
            .catch((err) => {
                console.log(err)
            })
        } else {
            alert('중복체크')
            }
        }

    const updateData = () => {
        setIsUpdate(!isUpdate)
       
    }

    const updateSubmit = () => {
        submit()
    }
    
    const [open, setOpen] = useState(true);

    const handleClose = () => {
        setOpen(false);
        dispatch(infoActions.isMyPage(false))
    }

    return (
        isUpdate?(
            <div>
                <Modal
                    open={open}
                    onClose={handleClose}
                    aria-labelledby="modal-modal-title"
                    aria-describedby="modal-modal-description"
                >
                    <Box sx={style}>
                        <Typography id="modal-modal-title" variant="h3" component="h2" align="center">
                            Mypage
                        </Typography>
                        <Avatar 
                            src = {currentUser.image} 
                            sx = {{ width: 100, height: 100, top: '10px', left: '38%',}} 
                        />
                        <Typography id="modal-modal-description" sx={{ mt: 2 }} align="center" variant="h6">
                            {state.nickname}
                        </Typography>
                        <Typography gutterBottom variant="body1" align="center" sx={{ mt: 2 }}>
                            # 관심사
                        </Typography>
                        <Stack direction="row" spacing={1} justifyContent="center">
                            {
                                interest.map((element)=>{
                                    return ( <Chip label={element} key={element} />)
                                })
                            }
                        </Stack>
                        <Button onClick={updateData}>수정하기</Button>
                        <Button onClick={handleClose}>나가기</Button>
                    </Box>
                </Modal>
            </div>
        ):(
            <div>
                <Modal
                    open={open}
                    onClose={handleClose}
                    aria-labelledby="modal-modal-title"
                    aria-describedby="modal-modal-description"
                >
                <Box sx={style}>
                    <Typography id="modal-modal-title" variant="h3" component="h2" align="center">
                        Update
                    </Typography>
                    <div>
                        <TextField
                            sx={{width: 250}}
                            autoFocus
                            margin="dense"
                            label="닉네임"
                            value={state.nickname}
                            onChange={NicknameInput}
                            type = "text"
                            variant="standard"
                        />
                        <Button onClick={check} sx={{top:"20px"}}>중복확인</Button>
                    </div>
                    <div>
                    <TextField
                        sx={{width: 200}}
                        autoFocus
                        margin="dense"
                        label="관심사"
                        value={newInterest} 
                        onChange={changeInput}
                        type = "text"
                        variant="standard"
                    />
                    <Button onClick={plusInteresting} sx={{top:"20px"}} >Add</Button>
                    <div>
                    <br/>
                    {
                        interest.map((e)=>{return <Button className='interest' id={e} key={e} onClick={removeInterst}>#{e}</Button>})
                    }
                    </div>
                </div>
                <div sx={{ mt: 2 }}>
                    <Button onClick={updateSubmit}>수정하기</Button>
                    <Button onClick={handleClose}>취소하기</Button>
                </div>

                </Box>
            </Modal>
        </div>
        )
        
    )
}

export default MypageModal