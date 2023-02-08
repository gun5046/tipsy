import React from "react";
import store from "../store";
import './Mypage.css'


function Mypage() {
  //로컬 스토리지에서 받아오기
  const userdata = store.getState()
	console.log(userdata)
	const interest = userdata.interest.split(',')
  return (
    <div id="Mypage">
      <p>Mypage</p>
			<img alt='' src={userdata.image}/>
			<p>{userdata.nickname}</p>
			{
				interest?.map((e)=>{return <span id={e} key={e}>#{e}</span>})
			}

    </div>
  )
}

export default Mypage;