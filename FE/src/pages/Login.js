import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import store from "../store";

let overlap = false

const Login = ()=> {
  //특수 문자 정규표현식
  const regExp = /^[ㄱ-힣a-zA-Z0-9]+$/; 

  //중복 확인
  
  //관심사 입력
  const [newInterest, setNewInterest] = useState('')

  //입력받은 유저 정보
  const [state, setState] = useState({
    nickname: '',
    profile: '',
    interest: []
  })

  //관심사 입력시 정보 변경
  const plusInteresting = () => {
    if (!state.interest.includes(newInterest)){
      if(!regExp.test(newInterest)){
        alert("특수문자, 공백은 입력하실수 없습니다.");
        return
      }
      
      if (newInterest.length <= 0 || newInterest.length >= 9) {
        alert('8자리이하로 입력해주세요.')
        return
      }
      const list = state.interest
      list.push(newInterest)
      setState({...state, ['interest']: list})
      setNewInterest('')
    }

  }

  //관심사 제거
  const removeInterst = (e) => {
    const list = state.interest.filter((element) => element !== e.target.id)
    setState({...state, ['interest']: list})
  }


  //닉네임 입력시 state의 값을 바꿈
  const NicknameInput = (e) => {

    const newNickname = e.target.value

    setState({
      ...state,
      ['nickname']: newNickname
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
        ['profile']: reader.result
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
    if (overlap) {
      alert('제출 완료')
      //redux 연습
      store.dispatch({type:'submit', state:state });
      navi('/mypage')
    } else {
      alert('중복체크')
    }
  }

  return (
    <div className="Login">
      <div>
        <h2>WELCOME</h2>
        <span>닉네임</span>
        <input
          value={state.nickname} 
          accept="img/*"
          onChange={NicknameInput}
        />
        <button onClick={check}>중복확인</button>
        <hr/>
      </div>

      <div>
        <img 
          src={state.profile}
          alt="profile image" 
          />
        <br/>
        <input
          type="file"
          onChange={profileInput}
        />
        <hr/>
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
            state.interest.map((e)=>{return <button id={e} key={e} onClick={removeInterst}>#{e}</button>})
          }
        </div>
        <hr/>
      </div>
      <button onClick={submit} >가입 완료하기</button>
    </div>
  )
}

export default Login;