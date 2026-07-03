package com.codenzyme.todolist.security;

import com.codenzyme.todolist.entity.AppUser;
import com.codenzyme.todolist.entity.Tier;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final AppUser appUser;

    public CustomUserDetails (AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Tier tier = appUser.getTier();
        String authorityName = "ROLE_" + tier;
        return List.of(new SimpleGrantedAuthority(authorityName));
    }

    @Override
    public String getPassword() {
        return appUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

}
