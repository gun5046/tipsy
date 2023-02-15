package com.ssafy.domainnosql.room.repo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;

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
	public void createRoom(Room room) {
		Hashinit();
		Setinit();
		stringHashOperations.put("room:" + room.getCode(), "title", room.getTitle());
		stringHashOperations.put("room:" + room.getCode(), "max", String.valueOf(room.getMax()));
		if (room.getPassword() != null)
			stringHashOperations.put("room:" + room.getCode(), "password", room.getPassword());
		stringHashOperations.put("room:" + room.getCode(), "entrance", (room.getEntrance() > 0 ? "on" : "off"));
		stringHashOperations.put("room:" + room.getCode(), "silence", (room.getSilence() > 0 ? "on" : "off"));
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		stringHashOperations.put("room:" + room.getCode(), "time", formatNow);

		List<String> tags = room.getHashtag();
		for (String tag : tags) {
			stringSetOperations.add("room:" + room.getCode() + ":hashtag", tag);
		}
	}

	@Override
	public void changeSet(Room room) {
		Hashinit();
		Setinit();
		stringHashOperations.put("room:" + room.getCode(), "title", room.getTitle());
		stringHashOperations.put("room:" + room.getCode(), "max", String.valueOf(room.getMax()));
		System.out.println("password = " + room.getPassword());
		if (room.getPassword() != null)
			stringHashOperations.put("room:" + room.getCode(), "password", room.getPassword());
		else {
			// private to public
			if (stringHashOperations.get("room:" + room.getCode(), "password") != null) {
				stringHashOperations.delete("room:" + room.getCode(), "password");
			}
		}
		stringHashOperations.put("room:" + room.getCode(), "entrance", (room.getEntrance() > 0 ? "on" : "off"));
		stringHashOperations.put("room:" + room.getCode(), "silence", (room.getSilence() > 0 ? "on" : "off"));

		// init Hashtag
		stringRedisTemplate.delete("room:" + room.getCode() + ":hashtag");

		// save Hashtag
		for (String tag : room.getHashtag()) {
			stringSetOperations.add("room:" + room.getCode() + ":hashtag", tag);
		}
	}

	@Override
	public int enterRoom(Member member) {
		Hashinit();
		Setinit();
		ZSetinit();
		String roomcode = member.getCode();
		// no exist room
		if (!isExists("room:" + roomcode)) {
			logger.info(roomcode + "방은 존재하지 않는 방입니다.");
			return 1;
		}

		// check password
		if (!chkPassword(roomcode, member.getPassword())) {
			logger.info("비밀번호가 다릅니다.");
			return 2;
		}

		// check banlist
		if (stringSetOperations.isMember("room:" + roomcode + ":banlist", member.getUid())) {
			logger.info(member.getUid() + "님은 강퇴당한 유저입니다.");
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
		stringZSetOperations.add("room:" + roomcode + ":member", String.valueOf(member.getUid()),
				Double.parseDouble(formatNow));

		// 방에 들어온 사람들
		stringHashOperations.put("room:" + roomcode + ":member:" + member.getUid(), "entertime", formatNow);
		stringHashOperations.put("room:" + roomcode + ":member:" + member.getUid(), "position",
				member.getPosition());

		String formatNow2 = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
		logger.info(member.getUid() + "님이 " + roomcode + "방에 입장하였습니다. (" + formatNow2 + ")");

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
	public boolean exitRoom(User user) {
		Hashinit();
		ZSetinit();
		LocalDateTime now = LocalDateTime.now();
		String formatNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		String roomcode = user.getCode();
		String uid = String.valueOf(user.getId());
		
		// 생성
		// 방에 들어왔었던 사람들
		stringHashOperations.put("room:" + roomcode + ":member:" + uid, "exittime", formatNow);

		// 현재 방에 있는 사람들
		stringZSetOperations.remove("room:" + roomcode + ":member", uid);

		// 방에 아무도 없으면 방 삭제
		Long cur = stringZSetOperations.zCard("room:" + roomcode + ":member");
		if (cur == 0) {
			stringRedisTemplate.delete("room:" + roomcode);
			stringRedisTemplate.delete("room:" + roomcode + ":banlist");
			stringRedisTemplate.delete("room:" + roomcode + ":hashtag");
			return true;
		}

		return false;
	}

	@Override
	public void banUser(User user) {
		Setinit();
		String roomcode = user.getCode();
		String uid = String.valueOf(user.getId());
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
			int cnt = 0; // 해당 건물에서 들어갈 수 없는 방(만석 테이블)

			// Iterator 사용
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				String str = (String) iter.next();

				Map<Object, Object> map = stringHashOperations.entries(str);
				map.put("code", str.substring(5));
				list[i].add(map);

				long max = Long.parseLong(String.valueOf(stringHashOperations.get(str, "max")));
				long cur = stringZSetOperations.zCard(str + ":member");
				if (max == cur) {
					logger.info(str + "방은 만석 테이블입니다.");
					cnt++;
				}
				total += cur;
			}

			table[i - 1][0] = total;
			table[i - 1][1] = cnt;

			set.clear();
		}
		return table;
	}

	@Override
	public List<Map<Object, Object>> getTable(int bno) {
		Hashinit();
		ZSetinit();
		Setinit();
		Set<String> set = stringRedisTemplate.keys("room:?????" + bno + "??");
		List<Map<Object, Object>> list = new ArrayList<>();

		Iterator iter = set.iterator();

		logger.info(bno + "번 건물에 있는 테이블 정보들");

		while (iter.hasNext()) {
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

}
