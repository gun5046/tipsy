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
		
		System.out.println("���̺� ��ġ : " + roomdto.getCode());
		
		// ���� room DB�� ���� ���� �ڵ尡 ���� ������
		String roomcode = "";
		while(true) {
			roomcode = getRandomCode() + roomdto.getCode();
			if(!isExists("room:"+roomcode)) {
				break;
			}
		}
		
		System.out.println("roomcode = " + roomcode);

		roomdto.setCode(roomcode);
		
		// ����
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
		
		logger.info(roomdto.getCode() + "���� �����Ǿ����ϴ�.");
		logger.info("  Title : " + roomdto.getTitle());
		logger.info("  �ִ� �ο� : " + roomdto.getMax());
		if(roomdto.getPassword() != null) {
			logger.info("  �������� : �����(" + roomdto.getPassword() + ")");
		} else {
			logger.info("  �������� : ������");
		}
		
		logger.info("  ���� �� ȿ�� : " + (roomdto.getEntrance()>0?"on":"off"));
		logger.info("  ħ�� �� ȿ�� : " + (roomdto.getSilence()>0?"on":"off"));
		logger.info("  �� ���� �ð� : " + now.format(DateTimeFormatter.ofPattern("yyyy�� MM�� dd�� HH�� mm�� ss��")));
		logger.info("  �ؽ��±� :" + stag);
		
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

		// roomcode�� ���� ���� �ִ� �� üũ
		if(!isExists("room:"+roomcode)) {
			logger.info(roomcode + "���� �������� �ʴ� ���Դϴ�.");
			return 1;
		}
		
		//roomcode�� password�� ��ġ�ϴ��� Ȯ��
		Object pwd = stringHashOperations.entries("room:"+roomcode).get("password");
		if(pwd != null) {
			if(!pwd.equals(password)) {
				logger.info("��й�ȣ�� �ٸ��ϴ�.");
				return 2;
			}
		}
		
		// ���𸮽�Ʈ�� �ִ��� Ȯ��
		if(stringSetOperations.isMember("room:"+roomcode+":banlist", uid)) {
			logger.info(uid + "���� ������� �����Դϴ�.");
			return 3;
		}
		
		// ������ �ʰ��ϴ��� Ȯ��
		String max = (String)stringHashOperations.entries("room:"+roomcode).get("max");
		if(stringZSetOperations.zCard("room:"+roomcode+":member") >= Long.parseLong(max)) {
			logger.info("�ο��ʰ�");
			return 4;
		}
		
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		// �濡 ���� �ִ� �����
		stringZSetOperations.add("room:" + roomcode + ":member", uid, Double.parseDouble(formatNow));
		
		// �濡 ���� �����
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "entertime", formatNow);
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "position", position);
		
		
		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy�� MM�� dd�� HH�� mm�� ss��"));
		logger.info(uid + "���� " + roomcode + "�濡 �����Ͽ����ϴ�. (" + formatNow2 + ")");
		
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
		
		// roomcode�� ���� ���� �ִ� �� üũ
		if(!isExists("room:"+roomcode)) {
			logger.info(roomcode + "���� �������� �ʴ� ���Դϴ�.");
			return 1;
		}
		
		//roomcode�� password�� ��ġ�ϴ��� Ȯ��
		Object pwd = stringHashOperations.entries("room:"+roomcode).get("password");
		if(pwd != null) {
			if(!pwd.equals(password)) {
				logger.info("��й�ȣ�� �ٸ��ϴ�.");
				return 2;
			}
		}
		
		// ���𸮽�Ʈ�� �ִ��� Ȯ��
		if(stringSetOperations.isMember("room:"+roomcode+":banlist", uid)) {
			logger.info(uid + "���� ������� �����Դϴ�.");
			return 3;
		}
		
		// ������ �ʰ��ϴ��� Ȯ��
		String max = (String)stringHashOperations.entries("room:"+roomcode).get("max");
		if(stringZSetOperations.zCard("room:"+roomcode+":member") >= Long.parseLong(max)) {
			logger.info("�ο��ʰ�");
			return 4;
		}
		
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		// �濡 ���� �ִ� �����
		stringZSetOperations.add("room:" + roomcode + ":member", uid, Double.parseDouble(formatNow));
		
		// �濡 ���� �����
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "entertime", formatNow);
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "position", position);

		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy�� MM�� dd�� HH�� mm�� ss��"));
		logger.info(uid + "���� " + roomcode + "�濡 �����Ͽ����ϴ�. (" + formatNow2 + ")");
		
		return 0;
	}
	
	//5�ڸ� ���� �ڵ� ����
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
		
		// ����
		
		// �濡 ���Ծ��� �����
		stringHashOperations.put("room:"+roomcode+":member:"+uid, "exittime", formatNow);
		
		// ���� �濡 �ִ� �����
		stringZSetOperations.remove("room:"+roomcode+":member", uid);

		Long cur = stringZSetOperations.zCard("room:"+roomcode+":member");
		// ���� �ο��� �ƹ��� ���� ��
		if(cur == 0) {
			stringRedisTemplate.delete("room:"+roomcode);
			logger.info("�����ִ� ����� ���� " + roomcode + "���� �����Ͽ����ϴ�.");
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
			
			logger.info(i + "�� �ǹ����� �����Ǿ� �ִ� �� : " + set);
			
			Iterator iter = set.iterator();	// Iterator ���
			
			int total = 0; // �ش� �ǹ��� �� �ִ� ��� ��
			int cnt = 0;   // ��� �ǹ����� �� �� ���� ��
			while(iter.hasNext()) {//���� ������ true ������ false
				String str = (String) iter.next();
				
				Map<Object, Object> map = stringHashOperations.entries(str);
				map.put("code", str.substring(5));
				System.out.println(i+" map="+map);
				list[i].add(map);
				
				max = Long.parseLong(String.valueOf(stringHashOperations.get(str, "max")));
				cur = stringZSetOperations.zCard(str+":member");
				
				if(max == cur) {
					logger.info(str+"���� ����� �� á���ϴ�.");
					cnt++;
				}
				total += cur;
			}
			
			table[i-1][0] = total;
			table[i-1][1] = cnt;
			
			
			set.clear();
			
		}
		
		for (int i = 0; i < 6; i++) {
			logger.info(i + "�� �ǹ��� �����Ǿ� �ִ� ���̺� ������");
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


	// key�� �����ϴ��� Ȯ��
	public boolean isExists(String key) {
		return stringRedisTemplate.hasKey(key);
	}



}
