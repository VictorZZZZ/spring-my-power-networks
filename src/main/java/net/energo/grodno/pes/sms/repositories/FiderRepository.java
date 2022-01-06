package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Fider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiderRepository extends JpaRepository<Fider, Long> {
    Optional<Fider> findTopByTpIdAndDbfId(Long tpId, int dbfId);

    @Query("SELECT f from Fider f WHERE f.tp.id IN :tpIds")
    List<Fider> findAllByTpId(@Param("tpIds") List<Long> tpIds);

    List<Fider> findByTpId(Long parentId);
}
