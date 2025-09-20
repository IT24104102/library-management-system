package lk.sliit.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin").password(encoder.encode("password")).roles("ADMIN").build();
        UserDetails librarian = User.withUsername("librarian").password(encoder.encode("password")).roles("LIBRARIAN").build();
        UserDetails assistant = User.withUsername("assistant").password(encoder.encode("password")).roles("ASSISTANT").build();
        UserDetails student = User.withUsername("student").password(encoder.encode("password")).roles("STUDENT").build();
        UserDetails it = User.withUsername("itsupport").password(encoder.encode("password")).roles("IT_SUPPORT").build();
        UserDetails chief = User.withUsername("chief").password(encoder.encode("password")).roles("CHIEF_LIBRARIAN").build();
        return new InMemoryUserDetailsManager(admin, librarian, assistant, student, it, chief);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults());
        return http.build();
    }
}

