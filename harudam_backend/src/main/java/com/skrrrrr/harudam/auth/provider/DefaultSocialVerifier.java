package com.skrrrrr.harudam.auth.provider;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.skrrrrr.harudam.common.enums.SocialLogin;

@Component
public class DefaultSocialVerifier implements SocialVerifier{
	
	private final RestTemplate rest = new RestTemplate();
	
	@Value("${app.oauth.kakao.userinfo:https://kapi.kakao.com/v2/user/me}")
	private String kakaoUserinfo;
	
	@Value("${app.oauth.google.userinfo:https://www.googleapis.com/oauth2/v3/userinfo}")
	private String googleUserinfo;
	
	@Value("${app.oauth.naver.userinfo:https://openapi.naver.com/v1/nid/me}")
	private String naverUserinfo;
	
	@Override
	public SocialProfile verify(SocialLogin provider, String accessToken) {
		return switch (provider) {
		case KAKAO -> fetchKakao(accessToken);
		case GOOGLE -> fetchGoogle(accessToken);
		case NAVER -> fetchNaver(accessToken);
		};
	}
	
	private SocialProfile fetchKakao(String token) {
		HttpHeaders h = new HttpHeaders();
		h.setBearerAuth(token);
		h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		Map body = exchange(kakaoUserinfo, h);
		
		Object id = body.get("id");
		if (id == null) throw new RuntimeException("Kakao: id 없음");
		
		String email = null, nickname = null;
		Map acc = (Map) body.get("kakao_account");
		if(acc != null && acc.get("email") != null) email = String.valueOf(acc.get("email"));
        Map props = (Map) body.get("properties");
        if (props != null && props.get("nickname") != null) nickname = String.valueOf(props.get("nickname"));
        if (nickname == null && acc != null) {
            Map prof = (Map) acc.get("profile");
            if (prof != null && prof.get("nickname") != null) nickname = String.valueOf(prof.get("nickname"));
        }
        if (nickname == null) nickname = "카카오사용자";
        return new SocialProfile(String.valueOf(id), email, nickname);
    }
	
    private SocialProfile fetchGoogle(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        Map body = exchange(googleUserinfo, h);
        Object sub = body.get("sub"); // 구글 고유 id
        if (sub == null) throw new RuntimeException("Google: sub 없음");
        String email = (String) body.get("email");         // 항상 존재(스코프에 email 포함 가정)
        String name  = (String) body.getOrDefault("name", "Google사용자");
        return new SocialProfile(String.valueOf(sub), email, name);
    }

    private SocialProfile fetchNaver(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        Map res = exchange(naverUserinfo, h);
        Map resp = (Map) res.get("response");
        if (resp == null) throw new RuntimeException("Naver: response 없음");
        Object id = resp.get("id");
        if (id == null) throw new RuntimeException("Naver: id 없음");
        String email = (String) resp.get("email");         // 선택 동의면 null 가능
        String name  = (String) resp.getOrDefault("name", "네이버사용자");
        return new SocialProfile(String.valueOf(id), email, name);
    }

    private Map exchange(String url, HttpHeaders h) {
        ResponseEntity<Map> res = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(h), Map.class);
        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            throw new RuntimeException("소셜 검증 실패: " + res.getStatusCode());
        }
        return res.getBody();
    }
		
}