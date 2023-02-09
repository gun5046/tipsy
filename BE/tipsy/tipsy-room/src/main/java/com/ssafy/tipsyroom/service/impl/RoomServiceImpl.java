package com.ssafy.tipsyroom.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.ssafy.domainrdb.dao.user.UserDao;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyroom.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	private final UserDao userDao;
	
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
		List<Map<Object, Object>> info = roomRepo.getTable(bno);
		List<Map<Object, Object>> memberlist = new ArrayList<>(); 
		UserVo uservo;
		
		for (Map<Object, Object> map : info) {
			if(map.get("member") != null) {
				List<String[]> list = (List<String[]>) map.get("member");			
				
				for (String[] arr : list) {
					Map<Object, Object> member = new HashMap();
					String position = arr[1];
					System.out.println(arr[0] + " " + arr[1]);
					uservo = userDao.findUserByUid(Long.parseLong(arr[0]));
					System.out.println(uservo);
					member.put("uid", uservo.getUid());
					member.put("kakao_id", uservo.getKakao_id());
					member.put("name", uservo.getName());
					member.put("nickname", uservo.getNickname());
					member.put("image", uservo.getImage());
					member.put("birth", uservo.getBirth());
					member.put("gender", uservo.getGender());
					member.put("interest", uservo.getInterest());
					member.put("reportcnt", uservo.getReportcnt());
					member.put("position", position);
					
					memberlist.add(member);
				}
			}		
			map.put("member", memberlist);
		}
		return info;
	}
	
	@Override
	public List<int[]> getBuilding() {
		return roomRepo.getBuilding();
	}

}
