package com.ssafy.tipsyroom.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.domainnosql.vo.MemberVo;
import com.ssafy.domainnosql.vo.RoomVo;
import com.ssafy.tipsyroom.service.RoomService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

//http://localhost:8081/swagger-ui.html

@RestController()
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
	private final Logger logger = LoggerFactory.getLogger(RoomController.class);

	private final RoomService roomService;

	@GetMapping("/test")
	public ResponseEntity<?> Test() {
		try {
			roomService.test();
			return new ResponseEntity<String>("success", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@GetMapping()
	@ApiOperation(value = "술집별 정보를 제공(현재 인원, 만석 테이블)", notes = "실행 완료")
	public ResponseEntity<?> getBuilding() {
		
		try {
			int[][] BuildingInfo = roomService.getBuilding();
			logger.info("술집별 정보");
			return new ResponseEntity<int[][]>(BuildingInfo, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@GetMapping("/bno")
	@ApiOperation(value = "테이블별 정보를 제공", notes = "실행 완료")
	public ResponseEntity<?> getTable(int bno) {
		try {
			List<Map<Object, Object>> TableInfo = roomService.getTable(bno);
			logger.info("테이블별 정보");
			return new ResponseEntity<List<Map<Object, Object>>>(TableInfo, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	//create room
	@PostMapping("/create")
	@ApiOperation(value = "code[테이블정보], title[방제목], max[최대인원], (password[비밀번호]), antrance[입장효과], silence[침묵효과]", notes = "방 생성한다.")
	public ResponseEntity<?> createRoom(@RequestBody RoomVo roomDto) {
		try {
			String roomcode = roomService.createRoom(roomDto);
			logger.info(roomcode + "방 생성 완료");
			return new ResponseEntity<String>(roomcode, HttpStatus.CREATED);

		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// change room setting
	@PostMapping("/change")
	@ApiOperation(value = "code[테이블정보], title[방제목], max[최대인원], (password[비밀번호]), antrance[입장효과], silence[침묵효과]", notes = "방 설정을 변경한다.")
	public ResponseEntity<?> changeRoomSet(@RequestBody RoomVo roomvo) {
		try {
			roomService.changeSet(roomvo);
			return new ResponseEntity<String>("changed", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	//enter room
	@PostMapping("/join")
	@ApiOperation(value = "code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]", notes = "방 입장한다.")
	public ResponseEntity<?> enterRoom(@RequestBody MemberVo membervo) {
		try {

			String status = "failed";
			int result = roomService.enterRoom(membervo);
			if (result == 0) {
				status = "success";
			} else if (result == 1) {
				status = "does not exist room";
			} else if (result == 2) {
				status = "incorrect password";
			} else if (result == 3) {
				status = "banned user";
			} else {
				status = "overcapacity";
			}
			return new ResponseEntity<String>(status, HttpStatus.CREATED);

		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// exit room
	@PostMapping("/exit")
	@ApiOperation(value = "code[방코드], id[사용자id]", notes = "방 나간다.")
	public ResponseEntity<?> exitRoom(@RequestBody Map<String, String> param) {
		try {
			String roomcode = param.get("code");
			String uid = param.get("id");
			roomService.exitRoom(roomcode, uid);
			logger.info(uid + "님이 " + roomcode + "방을 나갔습니다.");
			return new ResponseEntity<String>("success", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// ban user
	@PostMapping("/ban")
	@ApiOperation(value = "code[방코드], id[강퇴할 사용자 id]", notes = "강퇴하기")
	public ResponseEntity<?> banUser(@RequestBody Map<String, String> param) {
		try {
			String roomcode = param.get("code");
			String uid = param.get("id");
			roomService.banUser(roomcode, uid);
			logger.info(uid + "님이 " + roomcode + "방에서 강퇴되었습니다.");
			return new ResponseEntity<String>("success", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	private ResponseEntity<String> exceptionHandling(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Sorry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
