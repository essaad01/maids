package com.maids.app.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.maids.app.config.PasswordEncoderConfig;

@Configuration
public class WebSecurityConfig {
	
	  private final RestAuthenticationEntryPoint authenticationEntryPoint;

	  public WebSecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint) {
	    this.authenticationEntryPoint = authenticationEntryPoint;
	  }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authz) ->{
					try {
						authz
				        		.requestMatchers("/books")
				        		.permitAll()
						        .anyRequest().authenticated()
						        .and()
						        .httpBasic(basic -> basic
						                .authenticationEntryPoint(authenticationEntryPoint));
					} catch (Exception e) {
					}
				}
                )
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,PasswordEncoder passwordEncoder) throws Exception {
      auth.inMemoryAuthentication()
              .passwordEncoder(PasswordEncoderConfig.passwordEncoder())
              .withUser("maids")
              .password(PasswordEncoderConfig.passwordEncoder().encode("password"))
              .roles("ADMIN");
    }
    
}