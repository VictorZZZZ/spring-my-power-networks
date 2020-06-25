package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.users.Role;
import net.energo.grodno.pes.smsSender.repositories.user.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll(){
        return roleRepository.findAll();
    }
}
