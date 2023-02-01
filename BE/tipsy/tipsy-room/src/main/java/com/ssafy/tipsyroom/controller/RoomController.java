package com.ssafy.tipsyroom.controller;

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

import com.ssafy.tipsyroom.dto.MemberDto;
import com.ssafy.tipsyroom.dto.RoomDto;
import com.ssafy.tipsyroom.service.RoomService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
private final Logger logger = LoggerFactory.getLogger(RoomController.class);
	
	private final RoomService roomService;

	@GetMapping()
	@ApiOperation(value = "������ ������ ����(���� �ο�, ���� ���̺�)", notes="���� �Ϸ�")
	public ResponseEntity<?> getBuilding() {
		try {
			int[][] BuildingInfo = roomService.getBuilding();
			logger.info("������ ����");
			return new ResponseEntity<int[][]>(BuildingInfo, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
	
	
	//create room
	@PostMapping("/create")
	@ApiOperation(value = "code[���̺�����], title[������], max[�ִ��ο�], (password[��й�ȣ]), antrance[����ȿ��], silence[ħ��ȿ��]", notes="�� �����Ѵ�.")
	public ResponseEntity<?> createRoom(@RequestBody RoomDto roomDto) {
		try {
			String roomcode = roomService.createRoom(roomDto);
			logger.info(roomcode + "�� ���� �Ϸ�");
			return new ResponseEntity<String>(roomcode, HttpStatus.CREATED);

		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
	
	// change room setting
	@PostMapping("/change")
	@ApiOperation(value = "code[���̺�����], title[������], max[�ִ��ο�], (password[��й�ȣ]), antrance[����ȿ��], silence[ħ��ȿ��]", notes = "�� ������ �����Ѵ�.")
	public ResponseEntity<?> changeRoomSet(@RequestBody RoomDto roomdto) {
		try {
			roomService.changeSet(roomdto);
			return new ResponseEntity<String>("changed", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
	
	//enter room
	@PostMapping("/join")
	@ApiOperation(value = "code[���ڵ�], id[�����id], (password[��й�ȣ]), position[������ġ]", notes="�� �����Ѵ�.")
	public ResponseEntity<?> enterRoom(@RequestBody MemberDto memberdto) {
		try {

			String status = "failed";
			int result = roomService.enterRoom(memberdto);
			if(result == 0) {
				status = "success";
			} else if(result == 1) {
				status = "does not exist room";
			} else if(result == 2) {
				status = "incorrect password";
			} else if(result == 3) {
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
	@ApiOperation(value = "code[���ڵ�], id[�����id]", notes = "�� ������.")
	public ResponseEntity<?> exitRoom(@RequestBody Map<String, String> param) {
		try {
			String roomcode = param.get("code");
			String uid = param.get("id");
			roomService.exitRoom(roomcode, uid);
			logger.info(uid+"���� " + roomcode + "���� �������ϴ�.");
			return new ResponseEntity<String>("success", HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
	
	// ban user
	@PostMapping("/ban")
	@ApiOperation(value = "code[���ڵ�], id[������ ����� id]", notes = "�����ϱ�")
	public ResponseEntity<?> banUser(@RequestBody Map<String, String> param) {
		try {
			String roomcode = param.get("code");
			String uid = param.get("id");
			roomService.banUser(roomcode, uid);
			logger.info(uid+"���� " + roomcode + "�濡�� ����Ǿ����ϴ�.");
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
