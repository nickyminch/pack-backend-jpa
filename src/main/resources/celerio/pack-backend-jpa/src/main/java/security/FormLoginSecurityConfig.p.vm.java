$output.java($Security, "FormLoginSecurityConfig")##

$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.springframework.context.annotation.Bean")##
$output.require("org.springframework.context.annotation.Configuration")##
$output.require("org.springframework.security.authentication.AuthenticationManager")##
$output.require("org.springframework.security.authentication.AuthenticationProvider")##
$output.require("org.springframework.security.authentication.dao.DaoAuthenticationProvider")##
$output.require("org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder")##
$output.require("org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter")##
$output.require("org.springframework.security.config.http.SessionCreationPolicy")##
$output.require("org.springframework.security.core.userdetails.UserDetails")##
$output.require("org.springframework.security.core.userdetails.UserDetailsService")##
$output.require("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder")##
$output.require("org.springframework.security.crypto.password.PasswordEncoder")##
$output.require("org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter")##
$output.require("org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy")##
$output.require("org.springframework.security.web.authentication.session.SessionAuthenticationStrategy")##
$output.require("org.springframework.security.web.context.HttpSessionSecurityContextRepository")##
$output.require("org.springframework.security.web.context.SecurityContextRepository")##
$output.require("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity")##


@Configuration
@EnableWebSecurity
public class $output.currentClass extends WebSecurityConfigurerAdapter {
	private static final Logger log = LoggerFactory.getLogger(FormLoginSecurityConfig.class);

	@Override
	protected void configure(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
		 http
		 	.csrf().disable()
//		 	.sessionManagement()
//		    .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
//		    .and()
		 	.authorizeRequests()
		 		.antMatchers("/*").permitAll()
		 		.antMatchers("/home.faces").permitAll()
				.antMatchers("/resources/**").permitAll()
//	            .antMatchers("/register").permitAll()
	            .antMatchers("/login.faces").permitAll()
	            .antMatchers("/domain/**").hasRole("USER")
	         .and()
	     .formLogin()
	         .loginPage("/login.faces")
	         .failureUrl("/login.faces?error")
	         .defaultSuccessUrl("/dashboard")
	         .loginProcessingUrl("/j_spring_security_check")
	         .usernameParameter("username")
	         .passwordParameter("password")
	         .and()
	     .logout()
	         .logoutUrl("/logout.faces")
	         .deleteCookies("JSESSIONID")
	         .logoutSuccessUrl("/home.faces");
		 
		 http.sessionManagement()
	        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);			
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
		auth.authenticationProvider(authenticationProvider());
//		auth.inMemoryAuthentication()
//        .withUser("admin").roles("ADMIN").password("{noop}admin");
	}
	
	@Bean
	protected UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder(11);
	}
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
    	AuthenticationManager manager =  super.authenticationManagerBean();
    	log.info("manager.class="+manager.getClass().getSimpleName());
    	return manager;
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    	return new NullAuthenticatedSessionStrategy();
    }
    
    @Bean
    public SecurityContextRepository securityContextRepository() {
    	return new HttpSessionSecurityContextRepository();
    }
    
    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
    	return new UsernamePasswordAuthenticationFilter(authenticationManager());
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
