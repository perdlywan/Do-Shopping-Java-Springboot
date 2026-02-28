package com.example.do_shopping.config.audit;

import com.example.do_shopping.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // CASE 1: belum login (register, init data)
        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM");
        }

        // CASE 2: sudah login
        return Optional.of(auth.getName());
    }
}
