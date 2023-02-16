import axios from 'axios'
import { Navigate } from 'react-router-dom'
import { rsort } from 'semver'
import './Preview.css'
const Preview =() => {
  
  const getBuilding = () => {
    axios
      .get(`${url}/room`)
      .then((res) => {
        console.log("건물별 정보");
        // console.log(res);
        // setBuilding(res.data)
        dispatch(infoActions.getBuilding(res.data))

      })
      .catch((e) => {
        // console.log(e);
        // 403 에러가 발생한 경우
        if (e.response && e.response.status === 403) {
          console.log("로그인으로 이동");
          
        }
      });
  };

  const KAKAO_API = process.env.REACT_APP_KAKAO_API
  
  return (
  <div className="Preview">
    <div className="logo"><b>T<span>i</span>p<span>s</span>y</b>
    <br/>
    <br/>
    <br/>
    <br/>
    <img src="../login/kakao_login.png" onClick={() => {
      Kakao.Auth.authorize({
        redirectUri: 'http://i8d207.p.ssafy.io:3000/temp',
      })
    }}/>
    </div>
  </div>
  )
}


export default Preview;