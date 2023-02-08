import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import store from "../store";
import axios from "axios";


let overlap = false
const Login = ()=> {
  
  //특수 문자 정규표현식
  const regExp = /^[ㄱ-힣a-zA-Z0-9]+$/; 
  //쿠키

  //중복 확인
  
  //관심사 입력
  const [newInterest, setNewInterest] = useState('')
  //입력받은 유저 정보
  const props = useLocation().state
  const [state, setState] = useState({
    birth: props.birth ? props.birth : '',
    email: props.email ? props.email : '',
    gender: props.gender,
    image: props.image ? props.image : '',
    interest: props.interest ? props.interest : '',
    kakao_id: props.kakao_id ? props.kakao_id : '',
    name: props.name ? props.name : '',
    nickname: props.nickname ? props.nickname : '',
    reportcnt: props.reportcnt ? props.reportcnt : '',
    uid: props.uid ? props.uid : 0,
  })
  //관심사 배열
  const [interest, setInterest] = useState([])
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
  //profile 사진 입력
  const profileInput = (e) => {
    const profileImg = e.target.files[0]
    const reader = new FileReader()
    reader.readAsDataURL(profileImg)
    reader.onloadend = () =>{
      setState({
        ...state,
        'profile': reader.result
      })
    }
  }
  //서버와 통신 필요-------------------------------------------수정필요
  const check = ()=> {

    if(!regExp.test(state.nickname)){
      alert("특수문자, 공백은 입력하실수 없습니다.");
      return
    }

    if (state.nickname.length <= 0 || state.nickname.length >= 9) {
      alert('8자리이하로 입력해주세요.')
      return
    }

    overlap = true
    if(overlap) {
      alert('사용 가능한 닉네임입니다.')
    } else {
      alert('중복된 닉네임 입니다')
    }
  }
  const navi = useNavigate()
  //제출 
  const submit = () => {
    console.log(state)
    if (overlap) {
      axios.post('http://127.0.0.1:8081/user/account', state)
      //axios.post( 'http://i8d207.p.ssafy.io:8081/user/account', state )
      .then((res) => {
        console.log(res)
        store.dispatch({type:'submit', state:state })
        alert('제출 완료')
      })
      .then(res =>{
        axios.post(`http://127.0.0.1:8081/user/check`, res.data.userVo)
        .then((res) => {
          navi('/map')
        })
      })
      .catch((err) => {
        console.log(err)
        navi('/mypage')
      })
    } else {
      alert('중복체크')
    }
  }

  return (
    <div className="Login">
      <h2>Welcome to Tipsy!</h2>
      <div>
        <img 
          alt =''
          src={state.image}
          />
        <br/>
        <input
          type="file"
          onChange={profileInput}
        />
      </div>
      <div>
        <span>닉네임</span>
        <input
          value={state.nickname} 
          accept="img/*"
          onChange={NicknameInput}
        />
        <button onClick={check}>중복확인</button>
      </div>
      <div>
        <span>관심사</span>
        <input 
          value={newInterest} 
          onChange={changeInput}
        />
        <button onClick={plusInteresting}>+</button>
        <div>
          {
            interest?.map((e)=>{return <span id={e} key={e} onClick={removeInterst}>#{e}</span>})
          }
        </div>
      </div>
      <button onClick={submit} >가입 완료하기</button>
    </div>
  )
}

export default Login;