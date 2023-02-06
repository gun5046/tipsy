import React from "react";
import store from "../store";


function Mypage() {
    const nickname = store.getState().nickname
    const interest = store.getState().interest
    const image = store.getState().profile
    //로컬 스토리지에서 받아오기
    const temp = localStorage.getItem('userData')
    return (
        <>
            <p>Mypage</p>
            <p>from redux</p>
            <p>{nickname}</p>
            <img src={image}/>
            <p> interest</p>
            {
                interest.map((el) => {return (<span key={el}>{el}|</span>)
                })
            }
        </>
    )
}

export default Mypage;