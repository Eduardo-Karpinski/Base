package br.com.base.utils;

import java.util.Optional;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {

	private CookieUtils() {

	}

	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (name.equals(cookie.getName())) {
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	public static String getCookieValue(HttpServletRequest request, String name) {
		return getCookie(request, name).map(Cookie::getValue).orElse(null);
	}

	public static ResponseCookie createResponseCookie(String data) {
		return ResponseCookie.from("jwt", data)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.sameSite(SameSite.STRICT.toString())
				.build();
	}

}