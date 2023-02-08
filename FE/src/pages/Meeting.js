import * as THREE from 'three'
import { MeshBasicMaterial } from 'three';
import {RGBELoader} from 'three/examples/jsm/loaders/RGBELoader'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import Stats from 'three/examples/jsm/libs/stats.module'
//import Profile from '../components/Profile';
import { useState } from 'react';
//import { GUI } from 'dat.gui'

const pointer = new THREE.Vector2()
const textboxPointer = new THREE.Vector2(0,0)
const textboxPointer2 = new THREE.Vector2(0,0)
let INTERSECTED
let lon = 0, onPointerDownLon = 0,
  lat = 0, onPointerDownLat = 0,
  phi = 0, theta = 0;

  
function Meeting() {
  const interest = '취향1,취향2,취향3'
	const arr = interest.split(",")
  const [props, setProps] = useState({
    name: ''
  })
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(0xeeeeee)


  //카메라ㅏㅏ
  const camera = new THREE.PerspectiveCamera(35, window.innerWidth / window.innerHeight, 0.1, 1000)
  camera.position.set(3, 9, 13)
	

  const renderer = new THREE.WebGLRenderer()
  renderer.setSize(window.innerWidth, window.innerHeight)
  document.body.appendChild(renderer.domElement)

  const gridHelper = new THREE.GridHelper(20, 20)   //바닥 격자 크기, 갯수
  gridHelper.position.y = 0
  scene.add(gridHelper)

  const webcam = document.createElement('video')
  var constraints = { audio: false, video: { width: 1280, height: 1024} }


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
            webcam.srcObject = mediaStream
            webcam.onloadedmetadata = function (e) {
                webcam.setAttribute('autoplay', 'true')
                webcam.setAttribute('playsinline', 'true')
                webcam.play()
            }
        })
        .catch(function (err) {
            alert(err.name + ': ' + err.message)
        })

    const webcamCanvas = document.createElement('canvas')
    webcamCanvas.width = 1024
    webcamCanvas.height = 1024

    const canvasCtx = webcamCanvas.getContext('2d')
    canvasCtx.fillStyle = '#000000'
    canvasCtx.fillRect(0, 0, webcamCanvas.width, webcamCanvas.height)
    const webcamTexture = new THREE.Texture(webcamCanvas)
    webcamTexture.minFilter = THREE.LinearFilter
    webcamTexture.magFilter = THREE.LinearFilter

    //const geometry = new THREE.PlaneGeometry(1,1)
    const geometry = new THREE.PlaneGeometry(2,2)
    //const material: THREE.MeshBasicMaterial = new THREE.MeshBasicMaterial({ map: webcamTexture})


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
          keyColor: { value: [0.0, 1.0, 0.0] },
          similarity: { value: 0.0 },
          smoothness: { value: 0.0 },
      },
      vertexShader: vertexShader(),
      fragmentShader: fragmentShader(),
  })

  const cam = new THREE.Mesh(geometry, material)
  //cube.add(new THREE.BoxHelper(cube, 0xff0000))

  cam.scale.x = 3
  cam.scale.y = 3
  cam.position.set(0, 8, -1)
  cam.name= 'CAT'
  scene.add(cam)

  const cam2 = cam.clone()
  cam2.name = 'CAT2'
  cam2.position.set(7, 8, 2)
  cam2.rotateY(-1)
  scene.add(cam2)

  const cam3 = cam.clone()
  cam3.name = 'CAT3'
  cam3.position.set(-3, 8, 5)
  cam3.rotateY(1.3)
  scene.add(cam3)



  const stats= Stats()
  //document.body.appendChild(stats.dom)
  /* 
  var data = {
      keyColor: [0, 255, 0],
      similarity: 1,
      smoothness: 0.0,
  }
   */
  /* 
  gui 사용시 다른 파일 필요!
  const gui = new GUI()
  gui.addColor(data, 'keyColor').onChange(() => updateKeyColor(data.keyColor))
  gui.add(data, 'similarity', 0.0, 1.0).onChange(() => updateSimilarity(data.similarity))
  gui.add(data, 'smoothness', 0.0, 1.0).onChange(() => updateSmoothness(data.smoothness))
  */
  /* 
  function updateKeyColor(v) {
      material.uniforms.keyColor.value = [v[0] / 255, v[1] / 255, v[2] / 255]
  }
  function updateSimilarity(v) {
      material.uniforms.similarity.value = v
  }
  function updateSmoothness(v) {
      material.uniforms.smoothness.value = v
  }
  */
	//추가 광원 
	const light = new THREE.AmbientLight(0xffffff, 0.3)
	light.position.set(120, 120, 0)
	scene.add(light)
	const sphere = new THREE.Mesh(new THREE.BoxGeometry(2,2,2)) //광원 위치 꼭 지워주자! **************************************************************
	sphere.position.set(120, 120, 0)
	scene.add(sphere)

  /* 배경 hdr 광원 */
  const textureLoader = new THREE.TextureLoader();
  new RGBELoader()
    .load('../3d/cafe_4k.hdr', (texture) => {
      texture.mapping = THREE.EquirectangularReflectionMapping;
      //scene.background = texture; // 3차원 배경으로 사용
      scene.environment = texture; // 광원으로 사용
      //texture.dispose();
      }
  )
  document.addEventListener('click', onPointerMove2)
  document.addEventListener( 'pointermove', onPointerMove );
  document.addEventListener( 'wheel', onDocumentMouseWheel )
  window.addEventListener('resize', onWindowResize, false)
  document.addEventListener( 'pointerdown', onPointerDown )
  /* 배경 skybox */
	function skybox(place) {
		const skyTexture = textureLoader.load(`3d/D207_1.jpg`)
		//const skyGeometry = new THREE.SphereGeometry(400, 60, 40)
    const skyGeometry = new THREE.CylinderGeometry(150, 150, 400, 32, 2, true)
    skyGeometry.scale(-1,1,1)
    const skyMaterial = new MeshBasicMaterial({ map: skyTexture })
		const sky = new THREE.Mesh(skyGeometry, skyMaterial)
		sky.position.set(0, 0, 0)
		scene.add(sky)
	}
    /* 테이블 */
	function table(tableFolder){
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
	}
    /* 의자 생성  */
	function chairMake(furniture) {

		const loader = new GLTFLoader();
    const diff2 = textureLoader.load(`3d/${furniture}_4k/textures/${furniture}_diff_4k.jpg`)
    const nor2 = textureLoader.load(`3d/${furniture}_4k/textures/${furniture}_nor_gl_4k.jpg`)
    const metal2 = textureLoader.load(`3d/${furniture}_4k/texdiffes/${furniture}_metal_4k.jpg`)
    const rough2 = textureLoader.load(`3d/${furniture}_4k/textures/${furniture}_rough_4k.jpg`)
    loader.load( `3d/${furniture}_4k/${furniture}_4k.gltf`,
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
			
			chair.position.set(0,0,0)
			chair2.position.set(-6,0,4)
			chair3.position.set(7,0,4)
			chair4.position.set(2,0,12)
			
      chair2.rotateY(1)
			chair3.rotateY(-1)
			chair4.rotateY(Math.PI)
			
			scene.add( chair );
			scene.add( chair2 )
			scene.add( chair3 )
			scene.add( chair4 )
			
			// 시간에 따라 회전함
			/*
	 		function animate2() {
				gltf2.scene.rotation.y += 0.005
				requestAnimationFrame(animate2)
			} */
			//animate2()
    });
	}
  function sojumaker(){
  	const bottleLoader = new GLTFLoader()
  	bottleLoader.load( '3d/jiro_soju_4k/jiro_bottle.gltf',
  	  gltf2 => {
  	    var bottle = gltf2.scene;
  	    bottle.traverse ( ( o ) => {
  	      if ( o.isMesh ) {
              o.material.transparent = true
              o.material.opacity = 0.28
  	      }
  	    });
  	  bottle.scale.set(40, 40, 40)
  	  bottle.position.set(2, 6, 1)
  	  scene.add(bottle)
      bottle.rotation.x = -Math.PI/2
  	})
    const labelLoader = new GLTFLoader()
    labelLoader.load( '3d/jiro_soju_4k/jiro_label.gltf',
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
    label.position.set(2, 6, 1)
    scene.add(label)
    label.rotation.x = -Math.PI/2
  })
	}
  function anju(){
    const foodLoader = new GLTFLoader()
    foodLoader.load( '3d/pizza/pizza.gltf',
    gltf2 => {
      var pizza = gltf2.scene;
      //bottle.scale.set(30,30,30)
      pizza.position.set(0,6.4,5)
      scene.add(pizza)
    })
    foodLoader.load( '3d/pizza/frenchfries.gltf',
    gltf2 => {
      var pizza = gltf2.scene;
      //bottle.scale.set(30,30,30)
      pizza.position.set(3,6.4,5)
      scene.add(pizza)
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


  const textBox = document.createElement('div')
  const pTag = document.createElement('p')
  const btnTag1 = document.createElement('button')
  const brTag = document.createElement('br')
  const btnTag2 = document.createElement('button')
  btnTag1.innerText = '프로필'
  btnTag2.innerText = '신고'

  textBox.prepend(btnTag2)
  textBox.prepend(brTag)
  textBox.prepend(btnTag1)
  textBox.prepend(pTag)
  document.body.prepend(textBox)
  textBox.style.display = 'none'
  btnTag1.onclick = () => {
    //console.log(document.getElementById('profileBox'))
    document.getElementById('profileBox').style.display = 'block'
  }

  function render() {
    raycaster.setFromCamera( pointer, camera );
    const intersects = raycaster.intersectObjects( scene.children, false );
    if ( intersects.length > 0 ) {
      if ( INTERSECTED !== intersects[ 0 ].object.name) {
      	INTERSECTED = intersects[ 0 ].object.name;
        const text = INTERSECTED
				pTag.innerText = INTERSECTED
        if (document.getElementById('nickname2') && INTERSECTED !== '')  {
          document.getElementById('nickname2').innerText = INTERSECTED
        }
        if (INTERSECTED !== ''){
          const temp = props
          temp.name = INTERSECTED
          setProps(temp)
          console.log(props)
          
        }
				if (text) {
					textBox.style.display = 'none'
				}
				textBox.setAttribute(
          'style',
          `
          position: fixed;
          background-color: white;
          left:${textboxPointer2.x}px;
          top:${textboxPointer2.y}px;
          width: 100px
          `
        )
        if (INTERSECTED === '') {
      	  textBox.style.display = 'none'
        }
      }
    }
    renderer.render(scene, camera)
  }
  function animate() {
    requestAnimationFrame(animate)
    //if (webcam.readyState === webcam.HAVE_ENOUGH_DATA) {
    canvasCtx.drawImage(webcam, 0, 0, webcamCanvas.width, webcamCanvas.height)
    if (webcamTexture) webcamTexture.needsUpdate = true
    //}

    lat = Math.max( - 85, Math.min( 85, lat ) );
    phi = THREE.MathUtils.degToRad( 90 - lat );
    theta = THREE.MathUtils.degToRad( lon );
    const x = 500 * Math.sin( phi ) * Math.cos( theta );
    const y = 500 * Math.cos( phi );
    const z = 500 * Math.sin( phi ) * Math.sin( theta );
    camera.lookAt( x, y, z );
    render()
    stats.update()
  }
  skybox('bar')
  table('round_wooden_table_01')
  chairMake('dining_chair_02')
  sojumaker()
  anju()
	animate()
  return (
    <>
      <div id="profileBox" style={{display: 'none', position: "fixed", backgroundColor:'white' }}>
			  <button onClick={()=> {
			  	document.getElementById('profileBox').style.display = 'none'
			  }}>
			  	X
			  </button>
			  <br/>
			  <img alt='' src='https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F2513B53E55DB206927'/> 
			  <p id='nickname2'></p>
			  {
			  	arr.map((element)=>{
			  		return ( <p key={element}>{element}</p>)
			  	})
			  }
			  <p>활동뱃지</p>
      </div>
      {/* <Profile props={props}/> */}
    </>
  )
}

export default Meeting;