package com.ssafy.tipsyroom.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.ssafy.tipsyroom.dto.MemberDto;
import com.ssafy.tipsyroom.dto.RoomDto;
import com.ssafy.tipsyroom.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService{
private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	public RoomServiceImpl(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public String createRoom(RoomDto roomdto) {

		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		
		System.out.println("테이블 위치 : " + roomdto.getCode());
		
		// 현재 room DB에 없는 랜덤 코드가 나올 때까지
		String roomcode = "";
		while(true) {
			roomcode = getRandomCode() + roomdto.getCode();
			if(!isExists("room:"+roomcode)) {
				break;
			}
		}
		
		System.out.println("roomcode = " + roomcode);

		roomdto.setCode(roomcode);
		
		// 생성
		stringHashOperations.put("room:" + roomdto.getCode(), "title", roomdto.getTitle());
		stringHashOperations.put("room:" + roomdto.getCode(), "max", String.valueOf(roomdto.getMax()));
		if(roomdto.getPassword() != null)
			stringHashOperations.put("room:" + roomdto.getCode(), "password", roomdto.getPassword());
		stringHashOperations.put("room:" + roomdto.getCode(), "entrance", (roomdto.getEntrance()>0?"on":"off"));
		stringHashOperations.put("room:" + roomdto.getCode(), "silence", (roomdto.getSilence()>0?"on":"off"));
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		stringHashOperations.put("room:" + roomdto.getCode(), "time", formatNow);
		
		// hashtag
		SetOperations<String, String> stringSetOperations = stringRedisTemplate.opsForSet();
		List<String> tags = roomdto.getHashtag();
		String stag = "";
		for (String s : tags) {
			stag += " #" + s;
			stringSetOperations.add("room:" + roomcode + ":hashtag", s);
		}
		
		logger.info(roomdto.getCode() + "방이 생성되었습니다.");
		logger.info("  Title : " + roomdto.getTitle());
		logger.info("  최대 인원 : " + roomdto.getMax());
		if(roomdto.getPassword() != null) {
			logger.info("  공개범위 : 비공개(" + roomdto.getPassword() + ")");
		} else {
			logger.info("  공개범위 : 공개방");
		}
		
		logger.info("  입장 시 효과 : " + (roomdto.getEntrance()>0?"on":"off"));
		logger.info("  침묵 시 효과 : " + (roomdto.getSilence()>0?"on":"off"));
		logger.info("  방 생성 시간 : " + now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
		logger.info("  해시태그 :" + stag);
		
		System.out.println("create success");
		
		return roomdto.getCode();
	}
	
	@Override
	public void changeSet(RoomDto roomdto) {
		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		
		stringHashOperations.put("room:" + roomdto.getCode(), "title", roomdto.getTitle());
		stringHashOperations.put("room:" + roomdto.getCode(), "max", String.valueOf(roomdto.getMax()));
		System.out.println("password = "+roomdto.getPassword());
		if(roomdto.getPassword() != null)
			stringHashOperations.put("room:" + roomdto.getCode(), "password", roomdto.getPassword());
		else {
			// private to public
			if(stringHashOperations.get("room:"+roomdto.getCode(), "password") != null) {
				stringHashOperations.delete("room:"+roomdto.getCode(), "password");
			}
		}
		
		// hashtag
		SetOperations<String, String> stringSetOperations = stringRedisTemplate.opsForSet();
		stringRedisTemplate.delete("room:"+roomdto.getCode()+":hashtag");
		List<String> tags = roomdto.getHashtag();
		for (String tag : tags) {
			stringSetOperations.add("room:" + roomdto.getCode() + ":hashtag", tag);
		}
		
		
		stringHashOperations.put("room:" + roomdto.getCode(), "entrance", (roomdto.getEntrance()>0?"on":"off"));
		stringHashOperations.put("room:" + roomdto.getCode(), "silence", (roomdto.getSilence()>0?"on":"off"));
		
	}
	
	@Override
	public int enterRoom(MemberDto memberdto) {

		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		ZSetOperations<String, String> stringZSetOperations = stringRedisTemplate.opsForZSet();
		SetOperations<String, String> stringSetOperations = stringRedisTemplate.opsForSet();
		
		String roomcode = memberdto.getCode();
		String uid = String.valueOf(memberdto.getId());
		String password = memberdto.getPassword();
		String position = memberdto.getPosition();

		// roomcode를 가진 방이 있는 지 체크
		if(!isExists("room:"+roomcode)) {
			logger.info(roomcode + "방은 존재하지 않는 방입니다.");
			return 1;
		}
		
		//roomcode와 password가 일치하는지 확인
		Object pwd = stringHashOperations.entries("room:"+roomcode).get("password");
		if(pwd != null) {
			if(!pwd.equals(password)) {
				logger.info("비밀번호가 다릅니다.");
				return 2;
			}
		}
		
		// 강퇴리스트에 있는지 확인
		if(stringSetOperations.isMember("room:"+roomcode+":banlist", uid)) {
			logger.info(uid + "님은 강퇴당한 유저입니다.");
			return 3;
		}
		
		// 정원을 초과하는지 확인
		String max = (String)stringHashOperations.entries("room:"+roomcode).get("max");
		if(stringZSetOperations.zCard("room:"+roomcode+":member") >= Long.parseLong(max)) {
			logger.info("인원초과");
			return 4;
		}
		
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		// 방에 들어와 있는 사람들
		stringZSetOperations.add("room:" + roomcode + ":member", uid, Double.parseDouble(formatNow));
		
		// 방에 들어온 사람들
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "entertime", formatNow);
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "position", position);
		
		
		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
		logger.info(uid + "님이 " + roomcode + "방에 입장하였습니다. (" + formatNow2 + ")");
		
		return 0;
	}
	@Override
	public int enterRoom2(MemberDto memberdto) {
		
		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		ZSetOperations<String, String> stringZSetOperations = stringRedisTemplate.opsForZSet();
		SetOperations<String, String> stringSetOperations = stringRedisTemplate.opsForSet();
		
		String roomcode = memberdto.getCode();
		String uid = String.valueOf(memberdto.getId());
		String password = memberdto.getPassword();
		String position = memberdto.getPosition();
		
		// roomcode를 가진 방이 있는 지 체크
		if(!isExists("room:"+roomcode)) {
			logger.info(roomcode + "방은 존재하지 않는 방입니다.");
			return 1;
		}
		
		//roomcode와 password가 일치하는지 확인
		Object pwd = stringHashOperations.entries("room:"+roomcode).get("password");
		if(pwd != null) {
			if(!pwd.equals(password)) {
				logger.info("비밀번호가 다릅니다.");
				return 2;
			}
		}
		
		// 강퇴리스트에 있는지 확인
		if(stringSetOperations.isMember("room:"+roomcode+":banlist", uid)) {
			logger.info(uid + "님은 강퇴당한 유저입니다.");
			return 3;
		}
		
		// 정원을 초과하는지 확인
		String max = (String)stringHashOperations.entries("room:"+roomcode).get("max");
		if(stringZSetOperations.zCard("room:"+roomcode+":member") >= Long.parseLong(max)) {
			logger.info("인원초과");
			return 4;
		}
		
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		// 방에 들어와 있는 사람들
		stringZSetOperations.add("room:" + roomcode + ":member", uid, Double.parseDouble(formatNow));
		
		// 방에 들어온 사람들
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "entertime", formatNow);
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "position", position);

		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
		logger.info(uid + "님이 " + roomcode + "방에 입장하였습니다. (" + formatNow2 + ")");
		
		return 0;
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
	public void exitRoom(String roomcode, String uid) {
		
		ZSetOperations<String, String> stringZSetOperations = stringRedisTemplate.opsForZSet();
		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));	
		
		// 생성
		
		// 방에 들어왔었던 사람들
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "exittime", formatNow);
		
		// 현재 방에 있는 사람들
		stringZSetOperations.remove("room:"+roomcode+":member", uid);

		Long cur = stringZSetOperations.zCard("room:"+roomcode+":member");
		// 남은 인원이 아무도 없을 때
		if(cur == 0) {
			stringRedisTemplate.delete("room:"+roomcode);
			logger.info("남아있는 사람이 없어 " + roomcode + "방을 삭제하였습니다.");
		}

	}

	@Override
	public void banUser(String roomcode, String uid) {
		
		SetOperations<String, String> stringSetOperations = stringRedisTemplate.opsForSet();
		stringSetOperations.add("room:" + roomcode + ":banlist", uid);
		exitRoom(roomcode, uid);
	}


	@Override
	public int[][] getBuilding() {
		
		Set<String> set = new HashSet<String>();
		ZSetOperations<String, String> stringZSetOperations = stringRedisTemplate.opsForZSet();
		HashOperations<String, Object, Object> stringHashOperations = stringRedisTemplate.opsForHash();
		int[][] table = new int[6][2];
		
		List<Map<Object, Object>>[] list = new ArrayList[6];
		for (int i = 0; i < 6; i++) {
			list[i] = new ArrayList();
		}
		
		long max = 0;
		long cur = 0;
		for (int i = 1; i <= 6; i++) {
			set = stringRedisTemplate.keys("room:?????"+i+"??");
			
			logger.info(i + "번 건물에서 생성되어 있는 방 : " + set);
			
			Iterator iter = set.iterator();	// Iterator 사용
			
			int total = 0; // 해당 건물에 들어가 있는 사람 수
			int cnt = 0;   // 헤당 건물에서 들어갈 수 없는 방
			while(iter.hasNext()) {//값이 있으면 true 없으면 false
				String str = (String) iter.next();
				
				Map<Object, Object> map = stringHashOperations.entries(str);
				map.put("code", str.substring(5));
				System.out.println(i+" map="+map);
				list[i].add(map);
				
				max = Long.parseLong(String.valueOf(stringHashOperations.get(str, "max")));
				cur = stringZSetOperations.zCard(str+":member");
				
				if(max == cur) {
					logger.info(str+"방은 사람이 다 찼습니다.");
					cnt++;
				}
				total += cur;
			}
			
			table[i-1][0] = total;
			table[i-1][1] = cnt;
			
			
			set.clear();
			
		}
		
		for (int i = 0; i < 6; i++) {
			logger.info(i + "번 건물에 생성되어 있는 테이블 정보들");
			for (Map<Object, Object> map : list[i]) {
				logger.info(String.valueOf(map));
			}
		}
		
		return table;
	}
	
	@Override
	public void test() {
		
		

		
		
		
		
		
		
		
		
				
		
//		try {
//
//			ZSetOperations<String, String> stringZSetOperations = stringRedisTemplate.opsForZSet();
//			
//			String Date = String.valueOf(stringZSetOperations.score("room:"+roomcode+":member", uid));
//			Date date = new SimpleDateFormat("yyyyMMDDHHmmss").parse(Date);
//	        long time = date.getTime();
//
//	        Timestamp ts = new Timestamp(time);
//
//	        System.out.println(ts);
//			
//	        stringZSetOperations.range
//	        
//	        
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		
		
		
	}


	// key가 존재하는지 확인
	public boolean isExists(String key) {
		return stringRedisTemplate.hasKey(key);
	}



}
