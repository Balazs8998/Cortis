package com.cortis.auth.security;

import com.cortis.auth.repositorie.UserRepository;
import com.cortis.core.entity_legacy.user.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CortisUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CortisUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Hibás felhasználónév vagy jelszó"
                ));

        return new CortisUserPrincipal(user);
    }
}