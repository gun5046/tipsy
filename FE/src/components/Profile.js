function Profile ({nickname}) {
  //대충 nickname으로 정보 받아오는중..
	
	console.log({nickname})
	const interest = '취향1 취향2 취향3'
	const arr = interest.split(" ")
	return (
      <div id="profileBox" style={{display: 'none', position: "fixed", backgroundColor:'white' }}>
				{/* <img/> */}
				<button onClick={()=> {
					document.getElementById('profileBox').style.display = 'none'
				}}
				>
					X
				</button>
				<p>{nickname}</p>
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