package net.energo.grodno.pes.sms.services;

import net.energo.grodno.pes.sms.repositories.user.RoleRepository;
import net.energo.grodno.pes.sms.entities.users.Role;
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
