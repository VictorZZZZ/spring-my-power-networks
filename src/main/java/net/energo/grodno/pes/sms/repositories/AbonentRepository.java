package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Abonent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbonentRepository extends JpaRepository<Abonent,Long> {
    Abonent findOneByAccountNumber(Long id);
    long count();

    List<Abonent> findBySurnameIgnoreCaseContaining(String searchLine);

    List<Abonent> findByHomePhoneContaining(String searchLine);

    List<Abonent> findByFirstPhoneContaining(String searchLine);

    List<Abonent> findBySecondPhoneContaining(String searchLine);

    List<Abonent> findAllByAccountNumber(Long search);

    Abonent findByFiderIdAndSurname(Long fiderId, String surname);

    @Query("SELECT a from Abonent a WHERE a.firstPhone like '% %' OR a.secondPhone like '% %'")
    List<Abonent> findWithSpacesInNumbers();

    @Query("SELECT a from Abonent a WHERE a.accountNumber IN :idList")
    List<Abonent> findAllWhereIdIn(@Param("idList") List<Long> idList);

    @Query("SELECT a from Abonent a WHERE a.accountNumber=:id AND a.notes IS NOT NULL")
    Optional<Abonent> findByIdWhereNotesNotNull(@Param("id") Long id);
}
