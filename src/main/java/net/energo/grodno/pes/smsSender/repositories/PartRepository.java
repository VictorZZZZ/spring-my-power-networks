package net.energo.grodno.pes.smsSender.repositories;


import net.energo.grodno.pes.smsSender.entities.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part,Long> {
    Optional<Part> findByNameAndLineId(String name, Long id);
}
