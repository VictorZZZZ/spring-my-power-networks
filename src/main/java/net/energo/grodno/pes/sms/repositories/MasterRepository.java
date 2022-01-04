package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Master;
import net.energo.grodno.pes.sms.entities.Res;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterRepository extends JpaRepository<Master,Long> {

    List<Master> findAllByRes(Res res);
}
