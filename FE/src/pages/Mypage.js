import React from "react";
import store from "../store";


function Mypage() {
    const nickname = store.getState().nickname
    const interest = store.getState().interest

        
    return (
        <>
            <p>Mypage</p>
            <p>{nickname}</p>
            {
                interest.map((el) => {return (<p key={el}>{el}</p>)
                })
            }
        </>
    )
}

export default Mypage;