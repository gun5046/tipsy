//import { useState } from 'react'
import './Profile.css'

function Profile (props) {
	const interest = '취향1,취향2,취향3'
	const arr = interest.split(",")
/*	const [temp, setTemp] = useState('')
 	if (document.getElementById('nickname').innerText !== '') {
		setTemp(props.name)
	} */
	return (
    <div id="profileBox" style={{display: 'none', position: "fixed", backgroundColor:'white' }}>
			<button onClick={()=> {
				document.getElementById('profileBox').style.display = 'none'
			}}
			>
				X
			</button>
			<br/>
			<img alt='' src='https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F2513B53E55DB206927'/> 
			<p id='nickname'></p>
			{
				arr.map((element)=>{
					return ( <p key={element}>{element}</p>)
				})
			}
			<p>활동뱃지</p>
    </div>
  )
}

export default Profile;