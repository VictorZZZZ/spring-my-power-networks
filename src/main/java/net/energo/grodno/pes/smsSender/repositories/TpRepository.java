package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TpRepository extends JpaRepository<Tp,Integer> {
    Tp findTopByDbfId(int dbfId);

    @Query("SELECT t from Tp t WHERE t.res=:res ORDER BY t.name")
    List<Tp> findAllByResIdOrderByName(@Param("res") Res res);

    List<Tp> findByNameIgnoreCaseContains(String searchLine);
}
