package com.example.board;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class BoardApplicationTests {

	@Test
	void stringToBase64() {
		String text = "jwt test";
		String encodedString = Base64.getEncoder().encodeToString(text.getBytes());
		log.info("encodedString {}", encodedString);

		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		String decodedString = new String(decodedBytes);
		log.info("decodedString {}", decodedString);
	}

	@Test
	void createJwt() {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqr");
		Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
		JwtBuilder builder = Jwts.builder()
				// Header
				.setHeaderParam("typ", "JWT")
				// Payload - Registered Claim
				.setSubject("제목").setIssuer("ggoreb.com").setAudience("service user")
				// Payload - Secret Claim
				.claim("username", "ggoreb").claim("password", 1234).claim("hasPermission", "ADMIN")
				// Signature
				.signWith(signingKey, signatureAlgorithm);
		long now = System.currentTimeMillis();
		builder.setExpiration(new Date(now + 3600000)); // 1시간 뒤 토큰 유효기간 만료
		String token = builder.compact();
		log.info("jwt {}", token);
	}

	@Test
	void getDataFromJwt() {
		byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqr");
		String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsoJzrqqkiLCJpc3MiOiJnZ29yZWIuY29tIiwiYXVkIjoic2VydmljZSB1c2VyIiwidXNlcm5hbWUiOiJnZ29yZWIiLCJwYXNzd29yZCI6MTIzNCwiaGFzUGVybWlzc2lvbiI6IkFETUlOIiwiZXhwIjoxNzQ2NzcyNDQxfQ.frM35ZQCUnGp5WA522xKgiUgHQe9no27lq6NYHPd3O4";
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKeyBytes).build();
		JwsHeader<?> header = jwtParser.parseClaimsJws(jwt).getHeader();
		String algorithm = header.getAlgorithm();
		log.info("Algorithm {}", algorithm);
		String type = header.getType();
		log.info("Type {}", type);
		Claims claims = jwtParser.parseClaimsJws(jwt).getBody();
		log.info("Subject {}", claims.getSubject());
		log.info("Issuer {}", claims.getIssuer());
		log.info("Audience {}", claims.getAudience());
		log.info("claim {}", claims.get("username"));
		log.info("claim {}", claims.get("password"));
		log.info("claim {}", claims.get("hasPermission"));
	}

}
