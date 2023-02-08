package com.ssafy.tipsyroom.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;
import com.ssafy.tipsyroom.service.RoomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

//http://localhost:8081/swagger-ui.html

@RestController()
@RequestMapping("/room")
@RequiredArgsConstructor
@Api(tags = {"미팅룸 관련 API"})
public class RoomController {
	private final Logger logger = LoggerFactory.getLogger(RoomController.class);

	private final RoomService roomService;

	@GetMapping()
	@ApiOperation(value = "술집별 정보를 제공(현재 인원, 만석 테이블)", notes = "거리 페이지에서 술집별로 현재 들어간 인원, 합석하지 못하는 테이블을 제공한다.")
	public ResponseEntity<?> getBuilding() {
		
		try {
			int[][] BuildingInfo = roomService.getBuilding();
			logger.info("술집별 정보");
			return new ResponseEntity<int[][]>(BuildingInfo, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@GetMapping("/{bno}")
	@ApiOperation(value = "테이블별 정보를 제공", notes = "술집 내에서 테이블별 정보를 제공한다.")
	public ResponseEntity<?> getTable(@PathVariable("bno") Integer bno) {
		try {
			List<Map<Object, Object>> TableInfo = roomService.getTable(bno);
			logger.info("테이블별 정보");
			return new ResponseEntity<List<Map<Object, Object>>>(TableInfo, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	//create room
	@PostMapping()
	@ApiOperation(value = "code[테이블정보], title[방제목], max[최대인원], (password[비밀번호]), antrance[입장효과], silence[침묵효과]", notes = "방 생성한다.")
	public ResponseEntity<?> createRoom(@RequestBody Room room) {
		try {
			System.out.println(room);
			String roomcode = roomService.createRoom(room);
			logger.info(roomcode + "방 생성 완료");
			return new ResponseEntity<String>(roomcode, HttpStatus.CREATED);

		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// change room setting
	@PostMapping("/setting")
	@ApiOperation(value = "code[테이블정보], title[방제목], max[최대인원], (password[비밀번호]), antrance[입장효과], silence[침묵효과]", notes = "방 설정을 변경한다.")
	public ResponseEntity<?> changeRoomSet(@RequestBody Room room) {
		try {
			roomService.changeSet(room);
			return new ResponseEntity<String>("changed", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	//enter room
	@PostMapping("/entry")
	@ApiOperation(value = "code[방코드], id[사용자id], (password[비밀번호]), position[의자위치]", notes = "미팅룸 입장")
	public ResponseEntity<?> enterRoom(@RequestBody Member member) {
		try {

			String status = "failed";
			int result = roomService.enterRoom(member);
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
	@ApiOperation(value = "code[방코드], id[사용자id]", notes = "미팅룸 나간다.")
	public ResponseEntity<?> exitRoom(@RequestBody User user) {
		try {
			roomService.exitRoom(user);
			logger.info(String.valueOf(user.getId()) + "님이 " + user.getCode() + "방을 나갔습니다.");
			return new ResponseEntity<String>("success", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// ban user
	@PostMapping("/ban")
	@ApiOperation(value = "code[방코드], id[강퇴할 사용자 id]", notes = "강퇴하기")
	public ResponseEntity<?> banUser(@RequestBody User user) {
		try {
			roomService.banUser(user);
			logger.info(String.valueOf(user.getId()) + "님이 " + user.getCode() + "방에서 강퇴되었습니다.");
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
