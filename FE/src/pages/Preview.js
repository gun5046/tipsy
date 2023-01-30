import { Link } from "react-router-dom";
import axios from "axios";


const Preview =() => {
  const KAKAO_API = process.env.REACT_APP_KAKAO_API

  return (
  <div className="Preview">
    <p>preview</p>
    <button 
      onClick={() => {
        Kakao.Auth.authorize({
          redirectUri: 'http://127.0.0.1:3000/temp',
        })
      }
      }
    >
    kakao
    </button>
    <br/>
    <Link to={"/login"}>Login</Link>
  </div>
  )
}


export default Preview;