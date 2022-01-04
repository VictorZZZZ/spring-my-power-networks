package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead,Long> {

}
