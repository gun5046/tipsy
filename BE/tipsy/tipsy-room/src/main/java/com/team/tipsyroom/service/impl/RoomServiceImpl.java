package com.team.tipsyroom.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.domainnosql.entity.Member;
import com.team.domainnosql.entity.Room;
import com.team.domainnosql.entity.User;
import com.team.domainnosql.game.repo.RoomRepository;
import com.team.domainnosql.room.repo.RoomRepo;
import com.team.domainrdb.dao.user.UserDao;
import com.team.domainrdb.vo.UserVo;
import com.team.tipsyroom.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	private final UserDao userDao;
	private final RoomRepository roomRepository;
	@Autowired
	private RoomRepo roomRepo;

	@Override
	public boolean findRoomByCode(String rid) {
		boolean f = roomRepo.isExists("room:" + rid);
		if (!f) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String createRoom(Room room) {

		System.out.println("테이블 위치 : " + room.getCode());

		// 현재 room DB에 없는 랜덤 코드가 나올 때까지
		String roomcode = "";
		while (true) {
//			roomcode = getRandomCode() + room.getCode();
			roomcode = room.getCode();
			if (!roomRepo.isExists("room:" + roomcode)) {
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
		if (room.getPassword() != null) {
			logger.info("  공개범위 : 비공개(" + room.getPassword() + ")");
		} else {
			logger.info("  공개범위 : 공개방");
		}
		logger.info("  입장 시 효과 : " + (room.getEntrance() > 0 ? "on" : "off"));
		logger.info("  침묵 시 효과 : " + (room.getSilence() > 0 ? "on" : "off"));
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
	public void changeHost(User user) {
		roomRepo.changeHost(user);
		logger.info(user.getCode() + "방의 호스트가 " + String.valueOf(user.getId()) + "님으로 변경되었습니다.");
	}

	@Override
	public int enterRoom(Member member) {
		return roomRepo.enterRoom(member);
	}

	// 5자리 랜덤 코드 생성
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
	public String exitRoom(User user) {
		return roomRepo.exitRoom(user);
	}

	@Override
	public void banUser(User user) {
		roomRepo.banUser(user);
		roomRepo.exitRoom(user);
	}

	@Override
	public UserVo getUserInfo(Long uid) {
		try {
			return userDao.findUserByUid(uid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Map<Object, Object>> getTable(int bno) {
		List<Map<Object, Object>> info = roomRepo.getTable(bno);
		List<Map<Object, Object>> memberlist = new ArrayList<>();
		UserVo uservo;

		for (Map<Object, Object> map : info) {
			if (map.get("member") != null) {
				List<String[]> list = (List<String[]>) map.get("member");

				if (list.isEmpty()) {
					logger.info("list empty");
					continue;
				} else {
					logger.info(list.get(0)[0]);
				}

				for (String[] arr : list) {
					if (arr[0] == null || arr[0].equals(null) || arr[0].equals("null")) {
						continue;
					}
					Map<Object, Object> member = new HashMap();
					logger.info(arr[0] + "님이 앉은 자리는 " + arr[1] + "번입니다.");
					uservo = userDao.findUserByUid(Long.parseLong(arr[0]));
					if (uservo == null) {
						continue;
					} else {
						member.put("uid", uservo.getUid());
						member.put("kakao_id", uservo.getKakao_id());
						member.put("name", uservo.getName());
						member.put("nickname", uservo.getNickname());
						member.put("image", uservo.getImage());
						member.put("birth", uservo.getBirth());
						member.put("gender", uservo.getGender());
						member.put("interest", uservo.getInterest());
						member.put("reportcnt", uservo.getReportcnt());
						member.put("position", arr[1]);

						memberlist.add(member);
					}
				}
				map.put("member", memberlist);
			}
		}
		return info;
	}

	@Override
	public List<int[]> getBuilding() {
		return roomRepo.getBuilding();
	}

}
