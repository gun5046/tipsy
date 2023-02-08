package com.ssafy.tipsyroom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.tipsyroom.service.RoomService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/main")
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.ssafy.domainnosql.dao.room"})
public class MainController {
private final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private final RoomService roomService;
	
	//load about building
	//���ڵ忡�� �ǹ� ��Ģ���� �ο��� ���� ���̺�
	//101~609
	//select count(*) from room where max > (select count(*) from member where right(code,3) = �ڵ�)
	//select count(*) from member where right(code,3) =  ������ȣ__
	
	
	//hello
	@GetMapping()
	@ApiOperation(value = "init", notes="���� �Ϸ�")
	public String init() {
		return "Hello";
	}
//	public ResponseEntity<?> getBuilding() {
//		try {
//			int[][] BuildingInfo = roomService.getBuilding();
//			logger.info("������ ����");
//			return new ResponseEntity<int[][]>(BuildingInfo, HttpStatus.CREATED);
//		} catch (Exception e) {
//			return exceptionHandling(e);
//		}
//	}
	
	private ResponseEntity<String> exceptionHandling(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Sorry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
