package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.sms.Services.ResService;
import net.energo.grodno.pes.sms.Services.TpService;
import net.energo.grodno.pes.sms.repositories.ResRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

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
        tpService.removeDuplicatedAndEmpty(ResService.BRES_ID);
    }
}
