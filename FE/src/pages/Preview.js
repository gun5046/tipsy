import { Link } from "react-router-dom";

const Preview =() => {
const KAKAO_API = process.env.REACT_APP_KAKAO_API
    
  return (
  <div className="Preview">
    <p>preview</p>
    <button 
        onClick={() => window.open(`https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_API}&redirect_uri=http://localhost:8081/user/login&response_type=code`, '_blank')}
        >
        kakao
    </button>
    <br/>
    <Link to={"/login"}>Login</Link>
  </div>
  )
}


export default Preview;