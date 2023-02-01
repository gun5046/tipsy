package com.ssafy.coreweb.filter;

import static org.mockito.Mockito.timeout;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.coreweb.dto.UserDto;
import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainauth.entity.Auth;
import com.ssafy.domainauth.repo.AuthRepository;

import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AuthRepository authRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		if (req.getCookies() != null) {
			System.out.println("token 비교");
			Cookie cookie[] = req.getCookies();

			String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsIm5hbWUiOiLrsJXsooXqsbQiLCJuaWNrbmFtZSI6IuyihSIsImV4cCI6MTY3NTE4NDA1Nn0.xBItunzZvTVHV-vi6dNMEQNta_I__Kp9v5_QmYE8890";
			String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzU3ODg1NTZ9.SPeJxv9HjgXGwqD8LukD_IEoS-mQNf98WuxPFxHcFFA";

			for (Cookie c : cookie) {
				if (c.getName().equals("Authorization")) {
					accessToken = c.getValue();
				}
				if (c.getName().equals("RefreshToken")) {
					refreshToken = c.getValue();
				}
			}

			if (accessToken == null || accessToken.length() == 0) { // accessToken이 없거나 길이가=0
				System.out.println("accessToken null");
				res.setStatus(403);
				return;
			}

			System.out.println("======= " + accessToken);
			System.out.println("======= " + refreshToken);

//			if (accessToken != null) {
			Long uid = (long) jwtTokenProvider.getUserPk(accessToken);
			String name = jwtTokenProvider.getUserName(accessToken);
			String nickname = jwtTokenProvider.getUserNickname(accessToken);

			if (!jwtTokenProvider.validateToken(accessToken)) { // access토큰 만료시
				// refrestoken 검사
				Optional<Auth> originRefreshToken = authRepository.findById(uid);
				if (!originRefreshToken.isPresent()) {
					System.out.println("refreshtToken Not Exist");
					return;
				}
				if (!originRefreshToken.get().getRefreshToken().equals(refreshToken)) {
					System.out.println("refreshtToken Not Equal");
					return;
				}

				if (!jwtTokenProvider.validateToken(refreshToken)) {
					System.out.println("refreshToken NotValid");
					return;
				}

				accessToken = jwtTokenProvider.createAccessToken(new UserDto(uid, name, nickname));

				chain.doFilter(req, res);
				res.setStatus(403);
				;
			}

			Cookie cookie1 = new Cookie("Authorization", accessToken);

			refreshToken = jwtTokenProvider.createRefreshToken();
			jwtTokenProvider.saveRefreshToken(uid, refreshToken);
			System.out.println(authRepository.findById(uid));
			Cookie cookie2 = new Cookie("RefreshToken", refreshToken);
			cookie1.setHttpOnly(true);
			cookie2.setHttpOnly(true);
			res.addCookie(cookie1);
			res.addCookie(cookie2);

			chain.doFilter(req, res);
		}
		res.setStatus(403);
		return;
	}

	@Override
	public void destroy() {
		System.out.println("destroy");
	}

}