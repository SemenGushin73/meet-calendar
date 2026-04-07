package com.example.MeetCalendar.service.impl;

import com.example.MeetCalendar.entity.User;
import com.example.MeetCalendar.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * Spring Security adapter that loads application users for authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads user and maps roles to Spring Security authorities.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Username is blank");
        }
        log.debug("Loading user for authentication: username={}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getName()
                ))
                .collect(Collectors.toSet());

        log.debug("User loaded: username={}, enabled={}, locked={}, roles={}",
                user.getUsername(),
                user.isEnabled(),
                user.isAccountLocked(),
                user.getRoles().size()
        );


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.isEnabled(),
                true,
                true,
                !user.isAccountLocked(),
                authorities
        );
    }
}