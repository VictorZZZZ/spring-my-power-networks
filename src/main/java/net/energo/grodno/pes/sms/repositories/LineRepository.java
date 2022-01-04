package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line,Long> {

    Optional<Line> findByNameAndSectionId(String name, Integer id);

    List<Line> findBySectionId(Integer parentId);
}
