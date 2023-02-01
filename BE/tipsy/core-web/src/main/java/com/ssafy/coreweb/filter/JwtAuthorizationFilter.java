package com.ssafy.coreweb.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.coreweb.dto.UserDto;
import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainauth.entity.Auth;
import com.ssafy.domainauth.repo.AuthRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	private final AuthRepository authRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		if (req.getCookies() != null) {
			System.out.println("token 비교");
			Cookie cookie[] = req.getCookies();

			String accessToken = "";
			String refreshToken = "";

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

			if (!jwtTokenProvider.validateToken(accessToken)) { // access토큰 만료시
				Long uid = (long) jwtTokenProvider.getUserPk(accessToken);

				String name = jwtTokenProvider.getUserName(accessToken);

				String nickname = jwtTokenProvider.getUserNickname(accessToken);

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
				System.out.println(2);
				accessToken = jwtTokenProvider.createAccessToken(new UserDto(uid, name, nickname));
			}

			
			
			Cookie cookie1 = new Cookie("Authorization", accessToken);

			refreshToken = jwtTokenProvider.createRefreshToken();
			jwtTokenProvider.saveRefreshToken((long) jwtTokenProvider.getUserPk(accessToken), refreshToken);
			System.out.println(2);
			Cookie cookie2 = new Cookie("RefreshToken", refreshToken);

			Cookie deleteCookie1 = new Cookie("Authorization", null);
			deleteCookie1.setMaxAge(0);
			res.addCookie(deleteCookie1);
			Cookie deleteCookie2 = new Cookie("RefreshToken", null);
			deleteCookie2.setMaxAge(0);
			res.addCookie(deleteCookie2);
			cookie1.setHttpOnly(true);
			cookie2.setHttpOnly(true);
			res.addCookie(cookie1);
			res.addCookie(cookie2);
			System.out.println(1);
			chain.doFilter(req, res);
			return;
		} else {
			res.setStatus(403);
			return;
		}
	}

	@Override
	public void destroy() {
		System.out.println("destroy");
	}

}