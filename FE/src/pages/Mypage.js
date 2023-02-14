import React, { useState } from "react";
import './Mypage.css'
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { useEffect } from "react";
import { selectCurrentAuth } from "../redux/authSlice";
import axios from "axios";


let overlap = false
function Mypage() {
  //수정여부 확인
  const [isUpdate, setIsUpdate] = useState(true)
  //특수 문자 정규표현식
  const regExp = /^[ㄱ-힣a-zA-Z0-9]+$/; 

  const [newInterest, setNewInterest] = useState('')
  //입력받은 유저 정보
  const props = useSelector((state) => state.auth)
  const [state, setState] = useState(props)
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
  const updateData = () => {
    setIsUpdate(!isUpdate)
  }
/*   const navi = useNavigate()
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
  } */

  return (
    isUpdate?(
      <div id="Mypage">
        <p id="h1">Mypage</p>
			  <img alt='' src={props.image}/>
			  {/* <p id="nickname">{userdata.nickname}</p> */}
        <p id="nickname">{ state.nickname }</p>
        {
          interest.map((element)=>{
            return ( <span key={element} variant="outlined" color="secondary"># {element}</span>)
			    })
			  }
        <br/>
        <br/>
        <button>회원탈퇴</button>
        <button onClick={updateData}>수정하기</button>
      </div>
    ):(
      <div id="Mypage">
        <p id="h1">Mypage</p>
        <div>
        <img 
          alt =''
          src={state.image}
          />
        <br/>
        <input
          type="file"
          accept="img/*"
          onChange={profileInput}
        />
      </div>
      <br/>
      <div>
        <span id="starting">닉네임</span>
        <input
          value={state.nickname}
          onChange={NicknameInput}
        />
        <button onClick={check}>중복확인</button>
      </div>
      <br/>
      <div>
        <span id="starting">관심사</span>
        <input 
          value={newInterest} 
          onChange={changeInput}
        />
        <button onClick={plusInteresting}>+</button>
        <div>
          <br/>
          {
            interest.map((e)=>{return <button className='interest' id={e} key={e} onClick={removeInterst}>#{e}</button>})
          }
        </div>
      </div>
      <br/>
        <button onClick={updateData}>수정하기</button>
      </div>
    )
  )
}

export default Mypage;