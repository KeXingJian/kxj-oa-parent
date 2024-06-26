package com.kxj.security.custom;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService{
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
