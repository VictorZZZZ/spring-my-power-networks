package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepository;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void create(UserRepr userRepr) {
        User user = new User();
        user.setUsername(userRepr.getUsername());
        user.setPassword(passwordEncoder.encode(userRepr.getPassword()));
        user.setRes(userRepr.getRes());
        user.setRoles(userRepr.getRoles());
        userRepository.save(user);
    }

    public User findByUsername(String name) throws Exception {
        Optional<User> optUser = userRepository.findByUsername(name);
        if(optUser.isPresent()) {
            return userRepository.findByUsername(name).get();
        } else {
            throw new Exception("User not found:"+name);
        }
    }
}
