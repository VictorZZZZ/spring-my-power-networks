package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbonentRepository extends JpaRepository<Abonent,Long> {
    Abonent findOneByAccountNumber(Long id);
    long count();

}
