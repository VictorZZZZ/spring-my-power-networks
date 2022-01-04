package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Substation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubstationRepository extends JpaRepository<Substation,Integer> {
    Optional<Substation> findByNameAndResId(String name,Integer id);

    List<Substation> findByResId(Integer id);

    List<Substation> findByResIdOrderByName(Integer id);
}
