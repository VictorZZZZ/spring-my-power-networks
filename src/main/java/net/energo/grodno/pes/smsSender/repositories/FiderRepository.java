package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Fider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiderRepository extends JpaRepository<Fider, Long> {
    Optional<Fider> findTopByTpIdAndDbfId(Long tpId, int dbfId);

    List<Fider> findByTpId(Long parentId);
}
