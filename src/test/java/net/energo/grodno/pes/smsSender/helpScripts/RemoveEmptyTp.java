package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class RemoveEmptyTp extends AbstractClass {

    @Autowired
    private TpService tpService;
    @Autowired
    private ResRepository resRepository;


    /**
     *  - Присваивает участки для пустых ТП, если в базе есть с таким же именем и с заполненным участком.
     *  - Удаляет ТП у которых нет в подчинении фидеров
     */
    @Test
    @Rollback(false) // Чтобы не откатывало изменения в базе
    @Transactional
    public void removeEmptyTp(){
        tpService.removeDuplicatedAndEmpty(ResService.GSRES_ID);
    }
}
