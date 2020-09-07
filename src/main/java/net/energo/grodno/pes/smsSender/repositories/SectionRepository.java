package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section,Integer> {
    Optional<Section> findByNameAndSubstationId(String name,Integer Id);

    List<Section> findBySubstationId(Integer parentId);
}
