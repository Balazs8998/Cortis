package com.cortis.auth.security;

import com.cortis.auth.entity.UserEntity;
import com.cortis.auth.repository.UserRepository;
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
                .findByUsernameWithAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid username or password"
                ));



        return new CortisUserPrincipal(user);
    }


}