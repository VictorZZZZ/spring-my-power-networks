package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiderRepository extends JpaRepository<Fider,Long> {
    Fider findOneByTpIdAndDbfId(Long tpId, int dbfId);

    List<Fider> findByTpId(Long parentId);
}
