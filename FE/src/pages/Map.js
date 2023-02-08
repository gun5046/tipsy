//import axios from "axios"

import { Link } from "react-router-dom"

const Map = () => {
/*     
    axios.get("http://localhost:8081/user/token")
    .then(res=>{
        if(res.status === 403){
            navigator("/preview")
        }else{
            
        }
    }) 
*/
    
    return (

        <div>
            <p>이히히 맵</p>
            <Link to="/mypage">mypage</Link>
        </div>
    )
}

export default Map