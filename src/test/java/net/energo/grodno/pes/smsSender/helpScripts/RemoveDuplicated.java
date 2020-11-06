package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveDuplicated extends AbstractClass {

    @Autowired
    private FiderService fiderService;

    @Test
    @Transactional
    public void removeDuplicatedAbonents(){
        Fider fider= fiderService.getOne(6697L);
        List<Abonent> abonentList = fider.getAbonents();
        List<String> numbersToClean = new ArrayList<>();
        System.out.printf("\n\n\n\n %d - abonents in List",fider.getAbonents().size());

        for(Abonent abonent:abonentList){
            for(Abonent compareAbonent: abonentList){
                if(abonent.getFirstPhone().equals(compareAbonent.getFirstPhone())
                        && abonent.getAccountNumber()!=compareAbonent.getAccountNumber()){
                    numbersToClean.add(abonent.getFirstPhone());
                }
            }
        }
        numbersToClean = numbersToClean.stream().distinct().collect(Collectors.toList());
        System.out.printf("\n %d - abonents to Remove \n\n\n\n",numbersToClean.size());
        System.out.printf("\n %d - abonents left \n\n\n\n",abonentList.size());
    }
}
