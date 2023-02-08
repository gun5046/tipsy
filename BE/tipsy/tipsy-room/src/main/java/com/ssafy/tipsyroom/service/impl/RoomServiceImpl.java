package com.ssafy.tipsyroom.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;
import com.ssafy.domainnosql.room.repo.RoomRepo;
import com.ssafy.tipsyroom.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	
	@Autowired
	private RoomRepo roomRepo;

	@Override
	public String createRoom(Room room) {

		System.out.println("테이블 위치 : " + room.getCode());
		
		// 현재 room DB에 없는 랜덤 코드가 나올 때까지
		String roomcode = "";
		while(true) {
			roomcode = getRandomCode() + room.getCode();
			if(!roomRepo.isExists("room:"+roomcode)) {
				break;
			}
		}
		System.out.println("roomcode = " + roomcode);
		room.setCode(roomcode);
		
		// 생성
		roomRepo.createRoom(room);
		
		
		logger.info(room.getCode() + "방이 생성되었습니다.");
		logger.info("  Title : " + room.getTitle());
		logger.info("  최대 인원 : " + room.getMax());
		if(room.getPassword() != null) {
			logger.info("  공개범위 : 비공개(" + room.getPassword() + ")");
		} else {
			logger.info("  공개범위 : 공개방");
		}
		logger.info("  입장 시 효과 : " + (room.getEntrance()>0?"on":"off"));
		logger.info("  침묵 시 효과 : " + (room.getSilence()>0?"on":"off"));
		logger.info("  방 생성 시간 : " + String.format("yyyy년 MM월 dd일 HH시 mm분 ss초", room.getTime()));
		String tags = "";
		for (String tag : room.getHashtag()) {
			tags += " #" + tag;
		}
		logger.info("  해시태그 :" + tags);
		
		return room.getCode();
	}
	
	@Override
	public void changeSet(Room room) {
		roomRepo.changeSet(room);	
	}
	
	@Override
	public int enterRoom(Member member) {
		return roomRepo.enterRoom(member);
	}
	
	
	//5자리 랜덤 코드 생성
	private String getRandomCode() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 5;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}


	@Override
	public void exitRoom(User user) {
		if(roomRepo.exitRoom(user)) {
			logger.info("남아있는 사람이 없어 " + user.getCode() + "방을 삭제하였습니다.");
		}
	}

	@Override
	public void banUser(User user) {
		roomRepo.banUser(user);
		roomRepo.exitRoom(user);
	}
	
	@Override
	public List<Map<Object, Object>> getTable(int bno) {
		return roomRepo.getTable(bno);
	}
	
	@Override
	public int[][] getBuilding() {
		return roomRepo.getBuilding();
	}

}
