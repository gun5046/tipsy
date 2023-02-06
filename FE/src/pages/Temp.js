import axios from "axios";
import { useNavigate } from "react-router-dom";
import store from "../store";

const searchParams = new URLSearchParams(window.location.search);
let code;
for (const param of searchParams) {
  code = param[1];
}

function Temp(){
	const navigate = useNavigate()
  //axios.get(`http://i8d207.p.ssafy.io:8081/user/login?code=${code}`)
  axios.get(`http://127.0.0.1:8081/user/login?code=${code}`)
  .then(res =>{
    if(!res.data.userCheck){
			//처음 로그인시!
      console.log(res.data.userVo)
			navigate('/login', {state: res.data.userVo})
    }else{
			//console.log(res.data)
      // 메인 페이지 location.href
      // token같은거 처리해줘야함
			navigate('/map')
    }
    })
  .catch(err => {
		navigate('/')
  })

}

export default Temp