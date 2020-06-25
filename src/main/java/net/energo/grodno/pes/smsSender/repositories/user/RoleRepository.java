package net.energo.grodno.pes.smsSender.repositories.user;

import net.energo.grodno.pes.smsSender.entities.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAll();

    //List<Role> findAllByUserId(Long id);
}
