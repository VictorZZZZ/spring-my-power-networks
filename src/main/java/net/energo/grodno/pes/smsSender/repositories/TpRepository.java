package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TpRepository extends JpaRepository<Tp,Integer> {
    Tp findTopByDbfId(int dbfId);

    List<Tp> findAllByResId(Integer id);

    List<Tp> findByNameIgnoreCaseContains(String searchLine);
}
