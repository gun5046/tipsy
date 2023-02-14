import React from 'react';
import { useContext, useRef, useState, useEffect, useCallback } from "react"
import { useNavigate } from "react-router-dom"
import styled from "styled-components"


const StyledBox = styled.div`
  background: white;
`;

const RoomSetting = () => {

  const titleRef = useRef(null)
  const maxPeopleRef = useRef(null)
  const [title, setTitle] = useState("");
  const [maxPeople, setmaxPeople] = useState("");


  return (
    <StyledBox>
        <div className="roomSetting">
            <h1>방 설정</h1>
            <section>
                <h4>방 제목</h4>
                <input
                    placeholder={"제목을 입력해주세요."}
                    ref={titleRef}
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
            </section>
            <br/>
            <section>
                <h4>최대 인원 설정</h4>
                <input
                    placeholder={"최대 인원을 설정하세요."}
                    type = "number"
                    min={1} max={6} step={1}
                    ref={maxPeopleRef}
                    value={maxPeople}
                    onChange={(e) => setmaxPeople(e.target.value)}
                />
            </section>
        </div>
    </StyledBox>


  );
}

export default RoomSetting;