package com.test.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${config.oauth2.privateKey}")
	private String privateKey;

	@Value("${config.oauth2.publicKey}")
	private String publicKey;
	
    @Value("${security.oauth2.resource.id}")
    private String resourceId;

    @Value("${access_token.validity_period}")
    private int accessTokenValiditySeconds;

    @Value("${refresh_token.validity_period}")
    private int refreshTokenValiditySeconds;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Bean
	public JwtAccessTokenConverter tokenEnhancer() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		//converter.setVerifierKey(publicKey);
		return converter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(tokenEnhancer());
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
        .tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
        .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
				.accessTokenConverter(tokenEnhancer());
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("normal-app").authorizedGrantTypes("authorization_code", "implicit")
				.authorities("ROLE_CLIENT").scopes("read", "write").resourceIds(resourceId)
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds).and().withClient("trusted-app")
				.authorizedGrantTypes("client_credentials", "password", "refresh_token")
				.authorities("ROLE_TRUSTED_CLIENT").scopes("read", "write").resourceIds(resourceId)
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds).secret("secret").and()
				.withClient("register-app").authorizedGrantTypes("client_credentials").authorities("ROLE_REGISTER")
				.scopes("read").resourceIds(resourceId).secret("secret").and()
				.withClient("my-client-with-registered-redirect").authorizedGrantTypes("authorization_code")
				.authorities("ROLE_CLIENT").scopes("read", "trust").resourceIds("oauth2-resource")
				.redirectUris("http://anywhere?key=value");
	}

}