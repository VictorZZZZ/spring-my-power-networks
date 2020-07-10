package net.energo.grodno.pes.smsSender.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class AuthDetails {

    public static List<String> listRoles(Authentication authentication) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
                authentication.getAuthorities();
        List<String> roles = new ArrayList<>();
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
        return roles;
    }
}
