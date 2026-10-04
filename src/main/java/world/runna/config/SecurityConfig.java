package world.runna.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    @Bean SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http.csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().permitAll());
        return http.build();
    }
}
