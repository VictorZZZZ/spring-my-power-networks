package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.Lead;
import net.energo.grodno.pes.smsSender.entities.Master;
import net.energo.grodno.pes.smsSender.entities.Res;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterRepository extends JpaRepository<Master,Long> {

    List<Master> findAllByRes(Res res);
}
