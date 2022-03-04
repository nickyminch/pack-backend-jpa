$output.java($Security, "DemoSecurityConfig")##


$output.require("org.springframework.beans.factory.annotation.Autowired")##
$output.require("org.springframework.context.annotation.Configuration")##
$output.require("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity")##

@Configuration
@EnableWebSecurity
public class $output.currentClass {
	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private AuthenticationEntryPoint entryPoint;
}
