$output.java($Security, "AuthenticationEntryPoint")##

$output.require("java.io.IOException")##
$output.require("java.io.PrintWriter")##

$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##

$output.require("org.springframework.context.annotation.Configuration")##
$output.require("org.springframework.security.core.AuthenticationException")##
$output.require("org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint")##

@Configuration
public class $output.currentClass extends BasicAuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException authException) throws IOException{

	    response.addHeader("WWW-Authenticate", "Basic realm -" +getRealmName());
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    PrintWriter writer = response.getWriter();
	    writer.println("Http Status 401 "+authException.getMessage());
	}

	@Override
	public void afterPropertiesSet() {
	    super.afterPropertiesSet();
	}
}
