package net.energo.grodno.pes.smsSender.security.configs;

import net.energo.grodno.pes.smsSender.security.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.AuthProvider;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter {
    private PasswordEncoder passwordEncoder;
    private UserAuthService userAuthService;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //todo лог истории входов
        http.authorizeRequests()
                .antMatchers(new String[]{"/",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/login,",
                        "/ajax/resTree/**",
                        "/reports/detailed/getResStat**"}).permitAll()
                .antMatchers(new String[]{
                        "/register",
                        "/admin/**",
                        "/importFromDbf/**",
                        "/users/**",
                        "/res/add/**",
                        "/res/edit/**",
                        "/res/delete/**",
                        "/tp/delete/**",
                        "/orders/byUser/**"}).hasRole("ADMIN")
                .antMatchers("/**").authenticated()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutSuccessUrl("/login")
                  .permitAll();
//                .antMatchers("/").permitAll()
//                .anyRequest().hasAnyRole("ADMIN")
//                .anyRequest().permitAll()
//                .and().formLogin().loginPage("/login").permitAll()
//                .loginProcessingUrl("/authenticateTheUser");

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userAuthService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
