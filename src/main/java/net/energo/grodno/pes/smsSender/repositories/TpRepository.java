package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Part;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TpRepository extends JpaRepository<Tp,Long> {
    Tp findTopByDbfId(int dbfId);

    @Query("SELECT t from Tp t WHERE t.part IN " +
            "(SELECT p from Part p WHERE p.line IN " +
                "(SELECT l from Line l WHERE l.section IN " +
                    "(SELECT s from Section s WHERE s.substation IN " +
                        "(SELECT subst FROM Substation subst WHERE subst.res=:res)))) ORDER BY t.name")
    List<Tp> findAllByResIdOrderByName(@Param("res") Res res);

    List<Tp> findByNameIgnoreCaseContains(String searchLine);

    List<Tp> findByResIdAndNameContains(Integer resId, String name);

    List<Tp> findByName(String name);

    List<Tp> findByNameAndResId(String name,Integer resId);

    List<Tp> findAll();

    List<Tp> findByPartId(Long parentId);

    Page<Tp> findAllByResIdAndPartIdOrderByName(Integer resId, Long partId, Pageable pageable);

    Long countByResIdAndPartIdOrderByName(Integer id,Long partId);
}
