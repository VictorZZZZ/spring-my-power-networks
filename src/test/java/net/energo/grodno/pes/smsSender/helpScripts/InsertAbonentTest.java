package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class InsertAbonentTest extends AbstractClass {
    @Autowired
    AbonentRepository abonentRepository;

    @Test
    public void updateTest(){
        List<Abonent> abonentList = new ArrayList<>();
        Long accountNumber = 14142700232L;
        Abonent abonent = new Abonent(accountNumber,"Кудрова-v3","Елена","Vladimirovna","123","456","2123","fdf",null,true);
        abonentList.add(abonent);
        abonentRepository.saveAll(abonentList);
        List<Abonent> updated = abonentRepository.findAllByAccountNumber(accountNumber);
        Assert.assertEquals(1,updated.size());
    }
}
