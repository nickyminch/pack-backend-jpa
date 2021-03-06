## Copyright 2015 JAXIO http://www.jaxio.com
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
$output.java($Security, "UserDetailsServiceImpl")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.Collection")##
$output.require("java.util.List")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.springframework.context.annotation.Bean")##
$output.require("org.springframework.security.crypto.password.PasswordEncoder")##
$output.require("org.springframework.security.crypto.password.NoOpPasswordEncoder")##
$output.require("org.springframework.security.core.GrantedAuthority")##
$output.require("org.springframework.security.core.authority.SimpleGrantedAuthority")##
$output.require("org.springframework.security.core.userdetails.UserDetails")##
$output.require("org.springframework.security.core.userdetails.UserDetailsService")##
$output.require("org.springframework.security.core.userdetails.UsernameNotFoundException")##
$output.require("org.springframework.transaction.annotation.Transactional")##
$output.require($Context, "UserWithId")##

/**
 * An implementation of Spring Security's {@link UserDetailsService}.
 * 
 * @see http://static.springsource.org/spring-security/site/reference.html
 */
$output.dynamicAnnotationTakeOver("org.springframework.stereotype.Service")##
public class $output.currentClass implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(${output.currentClass}.class);

#if ($project.isAccountEntityPresent())
$output.require("javax.inject.Inject")##
$output.require($project.accountEntity.model)##
$output.require($project.accountEntity.repository)##
	@Inject
    private $project.accountEntity.repository.type $project.accountEntity.repository.var;

//    public ${output.currentClass}($project.accountEntity.repository.type $project.accountEntity.repository.var) {
//        this.${project.accountEntity.repository.var} = $project.accountEntity.repository.var;
//    }

#end
	@Inject
	private PasswordEncoder passwordEncoder;
    /**
     * Retrieve an account depending on its login this method is not case sensitive.
     *
     * @param username the user's username
     * @return a Spring Security userdetails object that matches the username
     * @throws UsernameNotFoundException when the user could not be found
     * @throws DataAccessException when an error occurred while retrieving the account
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Empty username");
        }
        log.debug("Security verification for user '{}'", username);

#if ($project.isAccountEntityPresent())
        $project.accountEntity.model.type account = ${project.accountEntity.repository.var}.${project.accountEntity.accountAttributes.username.uniqueGetter}(username);

        if (account == null) {
            log.info("User {} could not be found", username);
            throw new UsernameNotFoundException("user " + username + " could not be found");
        }

        Collection<GrantedAuthority> grantedAuthorities = toGrantedAuthorities(account.getRoleNames());
        String password = account.${project.accountEntity.accountAttributes.password.getter}();
        //password = "{noop}".concat(password);
        boolean enabled = #if ($project.accountEntity.accountAttributes.isEnabledSet())account.${project.accountEntity.accountAttributes.enabled.getter}()#{else}true#{end};
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new org.springframework.security.core.userdetails.User(account.getUsername(),passwordEncoder.encode(password),grantedAuthorities);
#else
        if ("user".equals(username)) {
            String password = "user";
            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            List<String> roles = newArrayList("ROLE_USER");
            return new UserWithId(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, toGrantedAuthorities(roles), null);
        }
        if ("admin".equals(username)) {
            String password = "admin";
            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            List<String> roles = newArrayList("ROLE_ADMIN");
            return new UserWithId(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, toGrantedAuthorities(roles), null);
        }
        return null;
#end
    }

    private Collection<GrantedAuthority> toGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> result = newArrayList();
        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }
}
