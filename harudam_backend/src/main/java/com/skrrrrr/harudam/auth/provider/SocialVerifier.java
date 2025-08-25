package com.skrrrrr.harudam.auth.provider;

import com.skrrrrr.harudam.common.enums.SocialLogin;

public interface SocialVerifier {
	SocialProfile verify(SocialLogin provider, String accessToken);

}
