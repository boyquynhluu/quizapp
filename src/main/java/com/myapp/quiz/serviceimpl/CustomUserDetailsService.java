package com.myapp.quiz.serviceimpl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myapp.quiz.entity.User;
import com.myapp.quiz.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Custom UserDetails")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        try {
            User user;
            if (usernameOrEmail.contains("@")) {
                user = userRepository.findByEmail(usernameOrEmail).orElseThrow(
                        () -> new UsernameNotFoundException("User not found with email: " + usernameOrEmail));
            } else {
                user = userRepository.findByUsername(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not exist by Username or Email"));
            }
            Set<GrantedAuthority> authorities = user.getRoles().stream()
                    .map((role) -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toSet());

            return new org.springframework.security.core.userdetails.User(usernameOrEmail, user.getPasswordHash(),
                    authorities);
        } catch (Exception e) {
            log.error("Find By username or email error: ", e);
            throw new UsernameNotFoundException("Login failed!");
        }
    }
}
