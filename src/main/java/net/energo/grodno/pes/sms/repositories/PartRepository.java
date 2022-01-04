package net.energo.grodno.pes.sms.repositories;


import net.energo.grodno.pes.sms.entities.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part,Long> {
    Optional<Part> findByNameAndLineId(String name, Long id);

    List<Part> findByLineId(Long parentId);
}
