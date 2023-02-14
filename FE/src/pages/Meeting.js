import * as THREE from 'three'
import { MeshBasicMaterial } from 'three';
//import {RGBELoader} from 'three/examples/jsm/loaders/RGBELoader'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { Stomp } from "@stomp/stompjs"; 
import SockJS from 'sockjs-client';
import { useState } from 'react';
//import { GUI } from 'dat.gui'
import './Meeting.css'
import { SelfieSegmentation } from "@mediapipe/selfie_segmentation";
import { useParams } from 'react-router-dom';
import QrModal from '../components/QrModal';

const pointer = new THREE.Vector2()
const textboxPointer = new THREE.Vector2(0,0)
const textboxPointer2 = new THREE.Vector2(0,0)
let gameresult = -1
let INTERSECTED
let lon = 0,
  onPointerDownLon = 0,
  lat = 0,
  onPointerDownLat = 0,
  phi = 0,
  theta = 0;

  
function Meeting(id) {
  console.log(id)
  const webcam = document.createElement("video");
  const webcamCanvas = document.createElement("canvas");
  const webcamCtx = webcamCanvas.getContext("2d");
  const selfieSegmentation = new SelfieSegmentation({
    locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
  });
  selfieSegmentation.setOptions({
    modelSelection: 1,
    selfieMode: true,
  });

  const sendToMediaPipe = async () => {
    if (!webcam.videoWidth) {
      requestAnimationFrame(sendToMediaPipe);
    } else {
      await selfieSegmentation.send({ image: webcam });
      requestAnimationFrame(sendToMediaPipe);
    }
  };

  var constraints = { audio: false, video: { width: 1280, height: 1024 } };
  navigator.mediaDevices
    .getUserMedia(constraints)
    .then(function (mediaStream) {
      /* const cat = document.createElement('source')
            cat.setAttribute('src','../video/Cat.mp4')
            cat.setAttribute('type', 'video/mp4')
            webcam.appendChild(cat)
            webcam.setAttribute('autoplay', 'true')
            webcam.setAttribute('playsinline', 'true')
            webcam.setAttribute('loop', 'true')
            webcam.play() */
      webcam.srcObject = mediaStream;
      sendToMediaPipe();
      webcam.onloadedmetadata = function (e) {
        webcam.setAttribute("autoplay", "true");
        webcam.setAttribute("playsinline", "true");
        webcam.play();
      };
    })
    .catch(function (err) {
      alert(err.name + ": " + err.message);
    });

  const onResults = (results) => {
    webcamCtx.save();
    webcamCtx.clearRect(0, 0, webcamCanvas.width, webcamCanvas.height);
    webcamCtx.drawImage(results.segmentationMask, 0, 0, webcamCanvas.width, webcamCanvas.height);
    // Only overwrite existing pixels.
    webcamCtx.globalCompositeOperation = "source-out";
    webcamCtx.fillStyle = "#00FF00";
    webcamCtx.fillRect(0, 0, webcamCanvas.width, webcamCanvas.height);

    // Only overwrite missing pixels.
    webcamCtx.globalCompositeOperation = "destination-atop";
    webcamCtx.drawImage(results.image, 0, 0, webcamCanvas.width, webcamCanvas.height);

    webcamCtx.restore();
  };

  const interest = "취향1,취향2,취향3,취향4,집에가고싶어요"
	const arr = interest.split(",")
  const [props, setProps] = useState({
    name: "",
  })
  const mySit = 4
  
  const sit = [[-7, 8, 0],[2, 8, 0],[13, 8, 2],[13, 8, 10],[2, 8, 12],[-7, 8 ,12]]
  let test = 0
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(0x000000)
  
  //카메라ㅏㅏ
  const camera = new THREE.PerspectiveCamera(35, window.innerWidth / window.innerHeight, 0.1, 1000)
  camera.position.set(3, 9, 13)
	

  const renderer = new THREE.WebGLRenderer()
  renderer.setSize(window.innerWidth, window.innerHeight)
  document.body.appendChild(renderer.domElement)

  const gridHelper = new THREE.GridHelper(20, 20); //바닥 격자 크기, 갯수
  gridHelper.position.y = 0;
  scene.add(gridHelper);

  const canvasCtx = webcamCanvas.getContext("2d")
  canvasCtx.fillStyle = "#00FF00"
  canvasCtx.fillRect(0, 0, webcamCanvas.width, webcamCanvas.height)
  const webcamTexture = new THREE.Texture(webcamCanvas)
  webcamTexture.minFilter = THREE.LinearFilter
  webcamTexture.magFilter = THREE.LinearFilter

  const geometry = new THREE.PlaneGeometry(2,2)

    function vertexShader() {
        return `
            varying vec2 vUv;
            void main( void ) {     
                vUv = uv;
                gl_Position = projectionMatrix * modelViewMatrix * vec4(position,1.0);
            }
        `
    }
    function fragmentShader() {
        return `
            uniform vec3 keyColor;
            uniform float similarity;
            uniform float smoothness;
            varying vec2 vUv;
            uniform sampler2D map;
            void main() {

                vec4 videoColor = texture2D(map, vUv);
            
                float Y1 = 0.299 * keyColor.r + 0.587 * keyColor.g + 0.114 * keyColor.b;
                float Cr1 = keyColor.r - Y1;
                float Cb1 = keyColor.b - Y1;

                float Y2 = 0.299 * videoColor.r + 0.587 * videoColor.g + 0.114 * videoColor.b;
                float Cr2 = videoColor.r - Y2; 
                float Cb2 = videoColor.b - Y2; 

                float blend = smoothstep(similarity, similarity + smoothness, distance(vec2(Cr2, Cb2), vec2(Cr1, Cb1)));
                gl_FragColor = vec4(videoColor.rgb, videoColor.a * blend); 
            }
        `
    }

  const material = new THREE.ShaderMaterial({
      transparent: true,
      uniforms: {
          map: { value: webcamTexture },
          keyColor: { value: [0.0, 1, 0] },
          similarity: { value: 0.7 },
          smoothness: { value: 0.0 },
      },
      vertexShader: vertexShader(),
      fragmentShader: fragmentShader(),
  })

  const cam = new THREE.Mesh(geometry, material)
  //cube.add(new THREE.BoxHelper(cube, 0xff0000))

  cam.scale.x = 3
  cam.scale.y = 3
  cam.position.set(sit[0][0], sit[0][1], sit[0][2])
  cam.name= 'CAT'
  scene.add(cam)

  const cam2 = cam.clone()
  cam2.name = 'CAT2'
  cam2.position.set(sit[1][0], sit[1][1], sit[1][2])
  scene.add(cam2)

  const cam3 = cam.clone()
  cam3.name = 'CAT3'
  cam3.position.set(sit[2][0], sit[2][1], sit[2][2])
  //cam3.rotateY(1.3)
  scene.add(cam3)

  const cam4 = cam.clone()
  cam4.name = 'CAT4'
  cam4.position.set(sit[3][0], sit[3][1], sit[3][2])
  cam4.rotateY(-1.3)
  scene.add(cam4)

  const cam6 = cam.clone()
  cam6.name = 'CAT6'
  cam6.position.set(sit[5][0], sit[5][1], sit[5][2])
  cam6.rotateY(1.3)
  scene.add(cam6)
	// 광원
  const basicLight = new THREE.HemisphereLight(0xffffff, 0x000000, 2)
  scene.add(basicLight)

  //머리위 광원
  const light = new THREE.SpotLight( 0xffffff, 10 )
  light.angle = Math.PI / 6
  light.penumbra = .8
  light.decay = 0.1
  light.distance = 20
  light.castShadow = true
  light.position.set(2,13,6);
  const lightList = [];
  for (let i =0; i < 6 ; i ++){
    const temp = light.clone()
    temp.position.set(sit[i][0], sit[i][1]+5, sit[i][2])
    temp.target.position.set(sit[i][0], sit[i][1], sit[i][2])
    lightList.push(temp)
    const temphelper = new THREE.SpotLightHelper(temp) 
    scene.add(temphelper)
    scene.remove(temphelper)

  }

    
    /* 배경 hdr 광원 */
    //const textureLoader = new THREE.TextureLoader();
    /*   new RGBELoader()
    .load('../3d/cafe_4k.hdr', (texture) => {
      texture.mapping = THREE.EquirectangularReflectionMapping;
      //scene.background = texture; // 3차원 배경으로 사용
      //scene.environment = texture; // 광원으로 사용
      //texture.dispose();
    }
    ) */
    
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.toneMappingExposure = 1

  document.addEventListener('click', onPointerMove2)
  document.addEventListener( 'pointermove', onPointerMove )
  document.addEventListener( 'wheel', onDocumentMouseWheel )
  window.addEventListener('resize', onWindowResize, false)
  document.addEventListener( 'pointerdown', onPointerDown )
  /* 배경 skybox */
	function skybox(place) {
		const skyTexture = new THREE.TextureLoader().load(`/3d/D207_1.jpg`)
		//const skyGeometry = new THREE.SphereGeometry(400, 60, 40)
    const skyGeometry = new THREE.CylinderGeometry(150, 150, 400, 32, 2, true)
    skyGeometry.scale(-1,1,1)
    const skyMaterial = new MeshBasicMaterial({ map: skyTexture })
		const sky = new THREE.Mesh(skyGeometry, skyMaterial)
		sky.position.set(0, 0, 0)
		scene.add(sky)
	}

    /* 테이블 */
 	/*function table(tableFolder){
  	const tableLoader = new GLTFLoader()
  	const diff = textureLoader.load(`3d/${tableFolder}_4k/textures/${tableFolder}_diff_4k.jpg`)
  	const nor = textureLoader.load(`3d/${tableFolder}_4k/textures/${tableFolder}_nor_gl_4k.jpg`)
  	const metal = textureLoader.load(`3d/${tableFolder}_4k/textures/${tableFolder}_metal_4k.jpg`)
  	const rough = textureLoader.load(`3d/${tableFolder}_4k/textures/${tableFolder}_rough_4k.jpg`)
  	tableLoader.load( `3d/${tableFolder}_4k/${tableFolder}_4k.gltf`,
  	  gltf2 => {
  	    //gltf.scene.scale(3,3,3)
  	    var table = gltf2.scene;
  	    table.traverse ( ( o ) => {
  	      if ( o.isMesh ) {
  	          //note: for a multi-material mesh, `o.material` may be an array,
  	          // in which case you'd need to set `.map` on each value.
  	          // 텍스쳐 요소들 넣기
  	          o.material.map = diff;
  	          o.material.normalMap = nor
  	          o.material.metalnessMap = metal
  	          o.material.roughnessMap = rough
  	          o.material.roughness = 0.5
  	      }
  	    });
  	  table.scale.set(8,8,8)
  	  table.position.set(2,-2,5)
  	  scene.add(table)
  	})
	}*/
  
    /* 의자 생성  */
	function chairMake(furniture) {
    const textureLoader = new THREE.TextureLoader()
		const loader = new GLTFLoader();
    const diff2 = textureLoader.load(`/3d/${furniture}_4k/textures/${furniture}_diff_4k.jpg`)
    const nor2 = textureLoader.load(`/3d/${furniture}_4k/textures/${furniture}_nor_gl_4k.jpg`)
    const metal2 = textureLoader.load(`/3d/${furniture}_4k/texdiffes/${furniture}_metal_4k.jpg`)
    const rough2 = textureLoader.load(`/3d/${furniture}_4k/textures/${furniture}_rough_4k.jpg`)
    loader.load( `/3d/${furniture}_4k/${furniture}_4k.gltf`,
		gltf2 => {
			var chair = gltf2.scene;
			
			chair.traverse ( ( o ) => {
				if ( o.isMesh ) {
					// 텍스쳐 요소들 넣기
					o.material.map = diff2;
					o.material.normalMap = nor2
					o.material.metalnessMap = metal2
					o.material.roughnessMap = rough2
					o.material.roughness = 0.5
				}
			});
			chair.scale.set(8,8,8)
			
			
			const chair2 = chair.clone()
			const chair3 = chair.clone()
			const chair4 = chair.clone()
			const chair5 = chair.clone()
			
			chair.position.set(2,0,0)
			chair2.position.set(-7, 0, 0)
			chair3.position.set(12,0,4)
			chair4.position.set(2,0,12)
			chair5.position.set(-7,0,12)
			
      chair2.rotateY(0)
			chair3.rotateY(-Math.PI/2)
			chair4.rotateY(Math.PI)
			chair5.rotateY(Math.PI)
			
			scene.add( chair );
			scene.add( chair2 )
			scene.add( chair3 )
			scene.add( chair4 )
			scene.add( chair5 )
			
			// 시간에 따라 회전함
			/*
	 		function animate2() {
				gltf2.scene.rotation.y += 0.005
				requestAnimationFrame(animate2)
			} */
			//animate2()
    });
	}
  function sojumaker(x, y, z){
  	const bottleLoader = new GLTFLoader()
  	bottleLoader.load( '/3d/jiro_soju_4k/jiro_bottle.gltf',
  	  gltf2 => {
  	    var bottle = gltf2.scene;
  	    bottle.traverse ( ( o ) => {
  	      if ( o.isMesh ) {
              o.material.transparent = true
              o.material.opacity = 0.28
  	      }
  	    });
  	  bottle.scale.set(40, 40, 40)
  	  bottle.position.set(x, y, z)
  	  scene.add(bottle)
      bottle.rotation.x = -Math.PI/2
  	})
    const labelLoader = new GLTFLoader()
    labelLoader.load( '/3d/jiro_soju_4k/jiro_label.gltf',
    gltf2 => {
      var label = gltf2.scene;
      label.traverse ( ( o ) => {
        if ( o.isMesh ) {
            //note: for a multi-material mesh, `o.material` may be an array,
            // in which case you'd need to set `.map` on each value.
            // 텍스쳐 요소들 넣기
            //o.material.map = diff;
        }
      });
    label.scale.set(40, 40, 40)
    label.position.set(x, y, z)
    scene.add(label)
    label.rotation.x = -Math.PI/2
  })
	}
  function anju(){
    const foodLoader = new GLTFLoader()
    foodLoader.load( '/3d/pizza/pizza.gltf',
    gltf2 => {
      var pizza = gltf2.scene;
      //bottle.scale.set(30,30,30)
      pizza.position.set(0,6.4,5)
      scene.add(pizza)
    })
    foodLoader.load( '/3d/pizza/frenchfries.gltf',
     gltf2 => {
      var pizza = gltf2.scene;
      //bottle.scale.set(30,30,30)
      pizza.position.set(3,6.4,5)
      scene.add(pizza)
    })
    foodLoader.load( '/3d/krispy_fried_chicken_resize.glb',
    gltf2 => {
     var leg = gltf2.scene;
     leg.scale.set(1.5, 1.5, 1.5)
     leg.position.set(0,6.4,9)
     scene.add(leg)
   })
   foodLoader.load( '/3d/krispy_fried_chicken_resize.glb',
   gltf2 => {
    var leg = gltf2.scene;
    leg.scale.set(1.5, 1.5, 1.5)
    leg.position.set(0.5,6.4,9)
    leg.rotateX(0.3)
    scene.add(leg)
  })
  foodLoader.load( '/3d/krispy_fried_chicken_resize.glb',
  gltf2 => {
   var leg = gltf2.scene;
   leg.scale.set(1.5, 1.5, 1.5)
   leg.position.set(1,6.4,9)
   leg.rotateX(-0.5)
   leg.rotateY(-0.5)
   scene.add(leg)
 })
    foodLoader.load( '/3d/florence_steak_-_fiorentina_bistecca/scene.gltf',
     gltf2 => {
      var steak = gltf2.scene;
      steak.scale.set(10,10,10)
      steak.position.set(5, 6.4 ,7)
      scene.add(steak)
    })
    foodLoader.load( '/3d/ssafydesk.gltf',
    gltf2 => {
      var desk = gltf2.scene;
      desk.scale.set(10,10,10)
      desk.position.set(3,6,5)
      desk.rotateX(-Math.PI/2)
      
      scene.add(desk)
    })
  }
  let raycaster = new THREE.Raycaster()
  function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight
    camera.updateProjectionMatrix()
    renderer.setSize(window.innerWidth, window.innerHeight)
    render()
  }
  function onDocumentMouseWheel( event ) {

    const fov = camera.fov + event.deltaY * 0.05;

    camera.fov = THREE.MathUtils.clamp( fov, 10, 75 );

    camera.updateProjectionMatrix();

  }
  function onPointerDown( event ) {
    if ( event.isPrimary === false ) return;

    textboxPointer.x = event.clientX;
    textboxPointer.y = event.clientY;

    onPointerDownLon = lon;
    onPointerDownLat = lat;
    document.removeEventListener('click', onPointerMove2)
    document.addEventListener( 'pointermove', onPointerMove );
    document.addEventListener( 'pointerup', onPointerUp );
  }
  function onPointerMove( event ) {
    if ( event.isPrimary === false ) return;
    lon = ( textboxPointer.x - event.clientX ) * 0.1 + onPointerDownLon;
    lat = ( event.clientY - textboxPointer.y ) * 0.1 + onPointerDownLat;
  }
  function onPointerMove2 ( event ) {
    pointer.x = ( event.clientX / window.innerWidth ) * 2 - 1;
    pointer.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
    textboxPointer2.x = event.clientX
    textboxPointer2.y = event.clientY
  }
  function onPointerUp(event) {

    if ( event.isPrimary === false ) return;
    document.removeEventListener( 'pointermove', onPointerMove );
    document.removeEventListener( 'pointerup', onPointerUp );
    document.addEventListener('click', onPointerMove2)

  }
  function connect(rid) {
    const socket = new SockJS('http://i8d207.p.ssafy.io:8082/ws/chat')
    const client = Stomp.over(socket);
    client.connect({},(frame)=>{
      client.subscribe(`/topic/play/liar-game/${rid}`, message =>{
        const result = message.body.split(',')
        if (result[0] === "Win") {      
          } else {
		  		}
      });
      client.subscribe(`/topic/play/croco-game/${rid}`, message =>{
		  	console.log(message.body)
        gameresult =  500
        scene.remove(basicLight)
        renderer.toneMappingExposure = 0.2
        scene.add(lightList[test])
        const pTag = document.createElement('p')
        pTag.innerText = 'Victory!'
        pTag.id = 'pTag'
        document.body.prepend(pTag)
      });
      client.subscribe(`/topic/play/drink-game/${rid}`, message =>{
        console.log(message.body)
        gameresult =  500
        scene.remove(basicLight)
        renderer.toneMappingExposure = 0.2
        scene.add(lightList[test])
      });
      client.subscribe(`/topic/play/drag-game/${rid}`, message =>{
        console.log(message.body)
        gameresult =  500
        scene.remove(basicLight)
        renderer.toneMappingExposure = 0.2
        scene.add(lightList[test])
      });
      client.subscribe(`/topic/play/roulette-game/${rid}`, message =>{
        console.log(message.body)
        gameresult =  500
        scene.remove(basicLight)
        renderer.toneMappingExposure = 0.2
        scene.add(lightList[test])
      });
      client.subscribe(`/topic/play/ordering-game/${rid}`, message =>{
        console.log(message.body)
        gameresult =  500
        scene.remove(basicLight)
        renderer.toneMappingExposure = 0.2
        scene.add(lightList[test])
      }); 
    })
  };

  const textBox = document.createElement('div')
  const brTag = document.createElement('br')
  const brTag2 = document.createElement('br')
  const btnTag1 = document.createElement('button')
  const btnTag2 = document.createElement('button')
  const btnTag3 = document.createElement('button')
  btnTag1.innerText = '프로필'
  btnTag2.innerText = '신고'
  btnTag3.innerText = '강퇴'

  textBox.prepend(btnTag3)
  textBox.prepend(brTag)
  textBox.prepend(btnTag2)
  textBox.prepend(brTag2)
  textBox.prepend(btnTag1)
  textBox.id = 'textBox'
  document.body.prepend(textBox)
  textBox.style.display = 'none'
  btnTag1.onclick = () => {
    //console.log(document.getElementById('profileBox'))
    document.getElementById('profileBox').style.display = 'block'
    document.removeEventListener('pointerdown', onPointerDown)
    document.removeEventListener('click', onPointerMove2)
  }

  function render() {
    raycaster.setFromCamera( pointer, camera );
    const intersects = raycaster.intersectObjects( scene.children, false );
    if ( intersects.length > 0 ) {
      if ( INTERSECTED !== intersects[ 0 ].object.name) {
      	INTERSECTED = intersects[ 0 ].object.name;
        const text = INTERSECTED
        if (document.getElementById('nickname2') && INTERSECTED !== "")  {
          document.getElementById('nickname2').innerText = INTERSECTED
        }
        if (INTERSECTED !== ""){
          const temp = props
          temp.name = INTERSECTED
          setProps(temp)
          console.log(props)
          
        }
				if (text) {
					textBox.style.display = "none"
				}
				textBox.setAttribute(
          "style",
          `
          position: fixed;
          left:${textboxPointer2.x}px;
          top:${textboxPointer2.y}px;
          width: 100px
          `
        )
        if (INTERSECTED === "") {
      	  textBox.style.display = "none"
        }
      }
    }
    renderer.render(scene, camera)
  }
  function animate() {
    requestAnimationFrame(animate)
    //if (webcam.readyState === webcam.HAVE_ENOUGH_DATA) {
    selfieSegmentation.onResults(onResults);
    canvasCtx.drawImage(webcamCanvas, 0, 0, webcamCanvas.width, webcamCanvas.height)
    if (webcamTexture) webcamTexture.needsUpdate = true
    //}

    lat = Math.max( - 85, Math.min( 85, lat ) );
    phi = THREE.MathUtils.degToRad( 90 - lat );
    theta = THREE.MathUtils.degToRad( lon );
    let x = 500 * Math.sin( phi ) * Math.cos( theta );
    let y = 500 * Math.cos( phi );
    let z = 500 * Math.sin( phi ) * Math.sin( theta );
    if (gameresult > 0) {
      gameresult -= 1
      if (test !== mySit) {
        x = sit[test][0]        //test용!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        y = sit[test][1]
        z = sit[test][2]
      }
    }else if (gameresult === 0) {
      gameresult -= 1
      scene.add(basicLight)
      renderer.toneMappingExposure = 1
      scene.remove(lightList[test])
      const targetp = document.getElementById('pTag')
      console.log(targetp)
      document.body.removeChild(targetp)
      test += 1                //test용!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    camera.lookAt( x, y, z )
    render()
  }
  connect(11)
  skybox("bar")
  //table('round_wooden_table_01')
  chairMake("dining_chair_02")
  sojumaker(2, 6, 2)
  sojumaker(3, 6, 2)
  sojumaker(-5, 6, 2)
  sojumaker(5, 6, 10)
  anju()
	animate()

  //// QRgame Url
  const params = useParams()
  console.log(params.id)

  return (
    <>
      <div id="profileBox">
			  <button 
          onClick={()=> {
			  	  document.getElementById('profileBox').style.display = 'none'
            document.addEventListener('pointerdown', onPointerDown)
            document.addEventListener('click', onPointerMove2)
			    }}
          id='closeBtn'
        >
			  	X
			  </button>
			  <br/>
			  <img alt='' src='https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F2513B53E55DB206927'/> 
        <br/>
			  <span id='nickname2'></span>
        <button id='friend'>친구추가</button>
        <p id='interest'>관심사</p>
			  {
			  	arr.map((element)=>{
			  		return ( <button key={element} variant="outlined" color="secondary"># {element}</button>)
			  	})
			  }

        <QrModal paramsNum={params.id}/>
      </div>
      {/* <Profile props={props}/> */}

    </>
  )
}

export default Meeting;