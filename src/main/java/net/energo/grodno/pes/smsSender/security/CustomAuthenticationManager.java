package net.energo.grodno.pes.smsSender.security;

import net.energo.grodno.pes.smsSender.entities.users.Role;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.user.RoleRepository;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public class CustomAuthenticationManager implements AuthenticationManager {
    private UserRepository userRepository;
    private RoleRepository roleRepository;


    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";

        User user = userRepository.findByUsername(username).get();
        //add more code
        List<Role> roleList = roleRepository.findAll();
        return new UsernamePasswordAuthenticationToken(user, password, roleList.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList()));
    }
}
