package com.ssafy.tipsyroom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MainController {
private final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private final RoomService roomService;
	
	//load about building
	//방코드에서 건물 규칙보고 인원과 남은 테이블
	//101~609
	//select count(*) from room where max > (select count(*) from member where right(code,3) = 코드)
	//select count(*) from member where right(code,3) =  빌딩번호__
	
	
	//hello
	@GetMapping()
	@ApiOperation(value = "init", notes="실행 완료")
//	public ResponseEntity<?> getBuilding() {
//		try {
//			int[][] BuildingInfo = roomService.getBuilding();
//			logger.info("술집별 정보");
//			return new ResponseEntity<int[][]>(BuildingInfo, HttpStatus.CREATED);
//		} catch (Exception e) {
//			return exceptionHandling(e);
//		}
//	}
//	public String init() {
//		return "Hello";
//	}
	
	private ResponseEntity<String> exceptionHandling(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Sorry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
