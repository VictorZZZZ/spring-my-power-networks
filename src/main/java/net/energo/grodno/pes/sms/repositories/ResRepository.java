package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Res;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResRepository extends JpaRepository<Res,Integer> {

}
