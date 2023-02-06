import { Link } from "react-router-dom";
import axios from "axios";


const Preview =() => {
  const KAKAO_API = process.env.REACT_APP_KAKAO_API
  return (
  <div className="Preview">
    <p>preview</p>
    <img src="../login/kakao_login.png" onClick={() => {
      Kakao.Auth.authorize({
        redirectUri: 'http://127.0.0.1:3000/temp',
      })
    }}/>
    <br/>
    <Link to={"/login"}>Login</Link>
  </div>
  )
}


export default Preview;