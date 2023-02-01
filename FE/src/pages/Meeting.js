import * as THREE from 'three'
//import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import {RGBELoader} from 'three/examples/jsm/loaders/RGBELoader'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import Stats from 'three/examples/jsm/libs/stats.module'
import Profile from '../components/Profile';
//import { GUI } from 'dat.gui'

const pointer = new THREE.Vector2()
const textboxPointer = new THREE.Vector2(0,0)
let INTERSECTED

function Meeting() {
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(0xeeeeee)

  const camera = new THREE.PerspectiveCamera(65, window.innerWidth / window.innerHeight, 0.1, 1000)
  camera.position.set(-1, 10, 10)
	camera.constrainVertical = true
	console.log(camera)
	

  const renderer = new THREE.WebGLRenderer()
  renderer.setSize(window.innerWidth, window.innerHeight)
  document.body.appendChild(renderer.domElement)

  //const controls = new OrbitControls(camera, renderer.domElement)

  const gridHelper = new THREE.GridHelper(20, 20)   //바닥 격자 크기, 갯수
  gridHelper.position.y = 0
  scene.add(gridHelper)


  window.addEventListener('resize', onWindowResize, false)
    function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight
        camera.updateProjectionMatrix()
        renderer.setSize(window.innerWidth, window.innerHeight)
        render()
    }

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
          similarity: { value: 0.6 },
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

  const cam2 = cam.clone()
  cam2.name = 'CAT2'
  cam2.position.set(5.5, 9, 2)
  //cam2.rotateY(-0.44)

  scene.add(cam)
  //scene.add(cam2)

  const stats= Stats()
  document.body.appendChild(stats.dom)
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

  /* 배경 skybox */
	function skybox(place) {
		const left = textureLoader.load(`../3d/${place}/${place}_left.png`)
		const front = textureLoader.load(`../3d/${place}/${place}_front.png`)
		const right = textureLoader.load(`../3d/${place}/${place}_right.png`)
		const bottom = textureLoader.load(`../3d/${place}/${place}_bottom.png`)
		const skyMaterialArray = []
		skyMaterialArray.push(
		new THREE.MeshStandardMaterial({map: right,}),//오른쪽
		new THREE.MeshStandardMaterial({map: left,}),//왼쪽
		new THREE.MeshStandardMaterial({map: left,}),//위
		new THREE.MeshStandardMaterial({map: bottom,}),//바닥
		new THREE.MeshStandardMaterial({map: front,}),//뒤
		new THREE.MeshStandardMaterial({map: front,}),//앞
		)
			
		for (let i = 0 ; i < 6; i++){
			skyMaterialArray[i].side = THREE.BackSide
		}
			
		const skyGeometry = new THREE.BoxGeometry(240, 240, 240)
		const sky = new THREE.Mesh(skyGeometry, skyMaterialArray)
		sky.position.set(0, 0, 0)
		scene.add(sky)
	}
	skybox('bar')
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

	table('round_wooden_table_01')
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
			chair2.position.set(5,0,0)
			chair3.position.set(5,0,10)
			chair4.position.set(0,0,10)
			
			chair4.rotateY(Math.PI)
			chair3.rotateY(Math.PI)
			
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
	chairMake('dining_chair_02')


  let raycaster = new THREE.Raycaster()
  document.addEventListener( 'click', onPointerMove );
  function onPointerMove( event ) {
    pointer.x = ( event.clientX / window.innerWidth ) * 2 - 1;
    pointer.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
    textboxPointer.x = event.clientX
    textboxPointer.y = event.clientY
  }

  function animate() {
    requestAnimationFrame(animate)

    //if (webcam.readyState === webcam.HAVE_ENOUGH_DATA) {
    canvasCtx.drawImage(webcam, 0, 0, webcamCanvas.width, webcamCanvas.height)
    if (webcamTexture) webcamTexture.needsUpdate = true
    //}

    //controls.update()
    render()

    stats.update()
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

  btnTag1.onclick = () => {
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
				if (text) {
					textBox.style.display = 'none'
				}
				textBox.setAttribute(
          'style',
          `
          position: fixed;
          background-color: white;
          left:${textboxPointer.x}px;
          top:${textboxPointer.y}px;
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

	window.addEventListener("keydown", e => {
		if (e.key === 'ArrowLeft' && camera.rotation.y < 1){
			camera.rotateY(0.1)
		} else if (e.key === 'ArrowRight' && camera.rotation.y > -1){
			camera.rotateY(-0.1)
		}
	})
	window.addEventListener('mousedown', e => {
		window.addEventListener('mousemove', (e) => {
			console.log(e)
			camera.rotateY( -0.0005)})
	})
	window.addEventListener('mouseup', () => {
		console.log('up')
		window.removeEventListener('mousemove', () => {camera.rotateY( -0.0005)})
	})


	animate()

	
  return (
    <>
      <Profile/>
    </>
  )
}

export default Meeting;