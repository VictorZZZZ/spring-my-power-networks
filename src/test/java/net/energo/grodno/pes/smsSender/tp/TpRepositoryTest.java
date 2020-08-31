package net.energo.grodno.pes.smsSender.tp;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.entities.Part;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

public class TpRepositoryTest extends AbstractClass {
    @Autowired
    private TpRepository tpRepository;
    @Autowired
    private ResRepository resRepository;

    @Test
    @Transactional
    @Ignore
    public void testGetUnlinkedTp(){
        Long count = tpRepository.countByResIdAndPartIdOrderByName(1,null);
        System.out.println(count);
    }
}
