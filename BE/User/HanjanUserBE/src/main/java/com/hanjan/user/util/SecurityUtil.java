package com.hanjan.user.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
	private SecurityUtil() {
	}

	// SecurityContext �� ���� ������ ����Ǵ� ����
	// Request �� ���� �� JwtFilter �� doFilter ���� ����
	public static Long getCurrentMemberId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new RuntimeException("Security Context �� ���� ������ �����ϴ�.");
		}

		return Long.parseLong(authentication.getName());
	}
}
