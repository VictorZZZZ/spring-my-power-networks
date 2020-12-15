package net.energo.grodno.pes.smsSender.helpScripts;

import lombok.extern.slf4j.Slf4j;
import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class clearSpacesInNumbers extends AbstractClass {
    @Autowired
    private AbonentRepository abonentRepository;

    /**
     * Очистка пробелов в номерах потребителей
     */
    @Test
    public void clearSpacesInNumbers(){
        List<Abonent> withSpacesInNumbers = abonentRepository.findWithSpacesInNumbers();
        for (Abonent abonent:withSpacesInNumbers) {
            abonent.setFirstPhone(abonent.getFirstPhone().replaceAll("\\s+",""));
            abonent.setSecondPhone(abonent.getSecondPhone().replaceAll("\\s+",""));
        }
        abonentRepository.saveAll(withSpacesInNumbers);
        withSpacesInNumbers = abonentRepository.findWithSpacesInNumbers();
        Assert.assertEquals(0,withSpacesInNumbers.size());
    }
}
