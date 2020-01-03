package com.example.demo.common.config;

import com.example.demo.security.model.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
//@EnableJpaRepositories("com.example.demo.repos")
public class JPAConfiguration  implements AuditorAware<String> {

//    @Autowired
//    private CurrentUser currentUser;

//    @Override
//    public Optional<Integer> getCurrentAuditor() {
//        Integer userId = currentUser.getCurrentUser().getId();
//        if (userId != null){
//            return Optional.of(userId);
//        } else {
//            return Optional.empty();
//        }
//    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(authentication.getPrincipal().toString());
    }
}
