import axios from "axios";

const searchParams = new URLSearchParams(window.location.search);
let code;
for (const param of searchParams) {
  code = param[1];
}

function Temp(){
    axios.get('http://127.0.0.1:8081/user/login', 0, {params:{
        code : `${code}` 
    }})
    .then(res =>{
        if(!res.data.userCheck){
            // 회원가입 페이지 location.href
            // 요거는 kakao 유저 정보를 들고 가셔야함 ex) birth, image
        }else{
            // 메인 페이지 location.href
            // token같은거 처리해줘야함
        }
    })
    return (
        <div></div>
    )
}

export default Temp