package com.ssafy.domainnosql.room.repo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.ssafy.domainnosql.vo.MemberVo;
import com.ssafy.domainnosql.vo.RoomVo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RoomRepoImpl implements RoomRepo {

	private final Logger logger = LoggerFactory.getLogger(RoomRepoImpl.class);

	@Autowired
	private final StringRedisTemplate stringRedisTemplate;

	HashOperations<String, Object, Object> stringHashOperations;
	SetOperations<String, String> stringSetOperations;
	ZSetOperations<String, String> stringZSetOperations;
	
	private void Hashinit() {
		stringHashOperations = stringRedisTemplate.opsForHash();
	}
	private void Setinit() {
		stringSetOperations = stringRedisTemplate.opsForSet();
	}
	private void ZSetinit() {
		stringZSetOperations = stringRedisTemplate.opsForZSet();
	}
	
	@Override
	public void createRoom(RoomVo roomvo) {
		Hashinit();
		Setinit();
		stringHashOperations.put("room:" + roomvo.getCode(), "title", roomvo.getTitle());
		stringHashOperations.put("room:" + roomvo.getCode(), "max", String.valueOf(roomvo.getMax()));
		if (roomvo.getPassword() != null)
			stringHashOperations.put("room:" + roomvo.getCode(), "password", roomvo.getPassword());
		stringHashOperations.put("room:" + roomvo.getCode(), "entrance", (roomvo.getEntrance() > 0 ? "on" : "off"));
		stringHashOperations.put("room:" + roomvo.getCode(), "silence", (roomvo.getSilence() > 0 ? "on" : "off"));
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		stringHashOperations.put("room:" + roomvo.getCode(), "time", formatNow);

		List<String> tags = roomvo.getHashtag();
		for (String tag : tags) {
			stringSetOperations.add("room:" + roomvo.getCode() + ":hashtag", tag);
		}
	}

	@Override
	public void changeSet(RoomVo roomvo) {
		Hashinit();
		Setinit();
		stringHashOperations.put("room:" + roomvo.getCode(), "title", roomvo.getTitle());
		stringHashOperations.put("room:" + roomvo.getCode(), "max", String.valueOf(roomvo.getMax()));
		System.out.println("password = " + roomvo.getPassword());
		if (roomvo.getPassword() != null)
			stringHashOperations.put("room:" + roomvo.getCode(), "password", roomvo.getPassword());
		else {
			// private to public
			if (stringHashOperations.get("room:" + roomvo.getCode(), "password") != null) {
				stringHashOperations.delete("room:" + roomvo.getCode(), "password");
			}
		}
		stringHashOperations.put("room:" + roomvo.getCode(), "entrance", (roomvo.getEntrance() > 0 ? "on" : "off"));
		stringHashOperations.put("room:" + roomvo.getCode(), "silence", (roomvo.getSilence() > 0 ? "on" : "off"));

		// init Hashtag
		stringRedisTemplate.delete("room:" + roomvo.getCode() + ":hashtag");

		// save Hashtag
		for (String tag : roomvo.getHashtag()) {
			stringSetOperations.add("room:" + roomvo.getCode() + ":hashtag", tag);
		}
	}

	@Override
	public int enterRoom(MemberVo membervo) {
		Hashinit();
		Setinit();
		ZSetinit();
		String roomcode = membervo.getCode();
		// no exist room
		if (!isExists("room:" + roomcode)) {
			logger.info(roomcode + "방은 존재하지 않는 방입니다.");
			return 1;
		}

		// check password
		if (!chkPassword(roomcode, membervo.getPassword())) {
			logger.info("비밀번호가 다릅니다.");
			return 2;
		}

		// check banlist
		if (stringSetOperations.isMember("room:" + roomcode + ":banlist", membervo.getId())) {
			logger.info(membervo.getId() + "님은 강퇴당한 유저입니다.");
			return 3;
		}

		// check ovecapacity
		if (stringZSetOperations.zCard("room:" + roomcode + ":member") >= Long
				.parseLong(String.valueOf(stringHashOperations.get("room:" + roomcode, "max")))) {
			logger.info("만석 테이블이라서 입장이 불가합니다.");
			return 4;
		}

		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		// 방에 들어와 있는 사람들
		stringZSetOperations.add("room:" + roomcode + ":member", String.valueOf(membervo.getId()),
				Double.parseDouble(formatNow));

		// 방에 들어온 사람들
		stringHashOperations.put("room:" + roomcode + ":member:" + membervo.getId(), "entertime", formatNow);
		stringHashOperations.put("room:" + roomcode + ":member:" + membervo.getId(), "position",
				membervo.getPosition());

		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
		logger.info(membervo.getId() + "님이 " + roomcode + "방에 입장하였습니다. (" + formatNow2 + ")");

		return 0;
	}
	
	private boolean chkPassword(String roomcode, String password) {
		Hashinit();
		Object pwd = stringHashOperations.get("room:" + roomcode, "password");
		if (pwd != null) {
			if (!pwd.equals(password)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean exitRoom(String roomcode, String uid) {
		Hashinit();
		ZSetinit();
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		// 생성
		// 방에 들어왔었던 사람들
		stringHashOperations.put("room:" + roomcode + ":member:" + uid, "exittime", formatNow);

		// 현재 방에 있는 사람들
		stringZSetOperations.remove("room:" + roomcode + ":member", uid);
		
		// 방에 아무도 없으면 방 삭제
		Long cur = stringZSetOperations.zCard("room:"+roomcode+":member");
		if(cur == 0) {
			stringRedisTemplate.delete("room:" + roomcode);
			stringRedisTemplate.delete("room:" + roomcode + ":banlist");
			stringRedisTemplate.delete("room:" + roomcode + ":hashtag");
			return true;
		}
		
		return false;
	}

	@Override
	public void banUser(String roomcode, String uid) {
		Setinit();
		stringSetOperations.add("room:" + roomcode + ":banlist", uid);
	}

	@Override
	public int[][] getBuilding() {
		Hashinit();
		ZSetinit();
		int[][] table = new int[6][2];
		
		List<Map<Object, Object>>[] list = new ArrayList[6];
		for (int i = 0; i < 6; i++) {
			list[i] = new ArrayList();
		}
		
		for (int i = 1; i <= 6; i++) {
			Set<String> set = stringRedisTemplate.keys("room:?????" + i + "??");
			
			logger.info(i + "번 건물에서 생성되어 있는 방 : " + set);
			
			int total = 0; // 해방 건물에 들어가 있는 사람 수
			int cnt = 0;   // 해당 건물에서 들어갈 수 없는 방(만석 테이블)
			
			// Iterator 사용
			Iterator iter = set.iterator();
			while(iter.hasNext()) {
				String str = (String) iter.next();
				
				Map<Object, Object> map = stringHashOperations.entries(str);
				map.put("code", str.substring(5));
				list[i].add(map);
				
				long max = Long.parseLong(String.valueOf(stringHashOperations.get(str, "max")));
				long cur = stringZSetOperations.zCard(str + ":member");
				if(max == cur) {
					logger.info(str + "방은 만석 테이블입니다.");
					cnt++;
				}
				total += cur;
			}
			
			table[i-1][0] = total;
			table[i-1][1] = cnt;
			
			set.clear();
		}
		return table;
	}

	@Override
	public List<Map<Object, Object>> getTable(int bno) {
		Hashinit();
		ZSetinit();
		Set<String> set = stringRedisTemplate.keys("room:?????" + bno + "??");
		List<Map<Object, Object>> list = new ArrayList<>();
		
		Iterator iter = set.iterator();
		
		logger.info(bno + "번 건물에 있는 테이블 정보들");
		
		while(iter.hasNext()) {
			String str = (String) iter.next();
			
			Map<Object, Object> map = stringHashOperations.entries(str);
			
			// save max from object to int
			Object max = stringHashOperations.get(str, "max");
			map.remove("max");
			map.put("max", Integer.parseInt(String.valueOf(max)));
			
			map.put("code", str.substring(5));
			map.put("current", stringZSetOperations.zCard(str + ":member"));
			map.put("hashtag", stringSetOperations.members("room:" + map.get("code") + ":hashtag"));
			
			logger.info(String.valueOf(map));
			
			list.add(map);
		}
		return list;
	}

	@Override
	public boolean isExists(String key) {
		return stringRedisTemplate.hasKey(key);
	}
	
	@Override
	public List<Long> assessList(String roomcode, long uid) {
		List<Long> assesslist = new ArrayList<>();
	
		try {
			Hashinit();
			ZSetinit();
			
			Date entertime = getDate(String.valueOf(stringHashOperations.get("room:" + roomcode + ":member:" + uid, "entertime")));
			Date exittime = getDate(String.valueOf(stringHashOperations.get("room:" + roomcode + ":member:" + uid, "exittime")));

			if(canAssess(exittime, entertime)) {
				logger.info(uid + "님은 " + roomcode + "방에 30분 미만 참가하여 평가를 할 수 없습니다.");
				return null;
			}
			
			Set<String> remain = stringZSetOperations.range("room:" + roomcode + ":member", 0, -1);
			
			// 평가를 할 수 있는 유저인지 판단
			Set<String> members = stringRedisTemplate.keys("room:" + roomcode + ":member:*");
			
			Iterator iter = members.iterator();
			while(iter.hasNext()) {
				String str = (String) iter.next();
				
				// 본인은 넘어가기
				if(str.equals("room:" + roomcode + ":member:" + uid)) {
					continue;
				}

				Date entertime2 = getDate(String.valueOf(stringHashOperations.get(str, "entertime")));			
				
				// 아직 안나가고 있는 유저들
				if(remain.contains(str.substring(21))) {
					long diffSec = (entertime.getTime() - entertime2.getTime()) / 1000;
					// 본인보다 먼저 들어온 사람들
					if(diffSec >= 0){
						assesslist.add(Long.parseLong(str.substring(21)));
					} else {
						// 사람들이 들어오고 내가 나가기까지 30분 이상 소요되었다면
						if(canAssess(entertime2, exittime)) {
							assesslist.add(Long.parseLong(str.substring(21)));
						}
					}
					continue;
				}

				Date exittime2 = getDate(String.valueOf(stringHashOperations.get(str, "exittime")));
				if(canAssess(entertime, exittime2)) {
					assesslist.add(Long.parseLong(str.substring(21)));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assesslist;
	}
	
	private boolean canAssess(Date entertime, Date exittime) {
		long diffMin = (exittime.getTime() - entertime.getTime()) / 60000; //분 차이
		
		if(diffMin < 30)
			return false;
		return true;
	}
	private Date getDate(String Date) throws ParseException {
		Date date = new SimpleDateFormat("yyyyMMDDHHmmss").parse(Date);
		return date;
	}

}
