package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveDuplicatedAbonents extends AbstractClass {

    @Autowired
    private FiderService fiderService;

    @Test
    @Transactional
    @Ignore
    public void removeDuplicatedAbonents(){
        Fider fider= fiderService.getOne(705L);
        List<Abonent> abonentList = fider.getAbonents();
        List<String> numbersToClean = new ArrayList<>();
        System.out.printf("\n\n\n\n %d - abonents in List",fider.getAbonents().size());


        for(int i=0;i<abonentList.size();i++){
            Abonent abonent = abonentList.get(i);
            for(Abonent compareAbonent: abonentList.subList(i+1,abonentList.size())){
                if(abonent.getFirstPhone().equals(compareAbonent.getFirstPhone())
                        && !abonent.getAccountNumber().equals(compareAbonent.getAccountNumber())
                        && !abonent.getFirstPhone().equals("0")){
                    numbersToClean.add(abonent.getFirstPhone());
                    break;
                }
            }
        }
        System.out.printf("\n list to clean \n %s", numbersToClean);
        numbersToClean = numbersToClean.stream().distinct().collect(Collectors.toList());
        System.out.printf("\n list to clean \n %s", numbersToClean);
        System.out.printf("\n %d - abonents to Remove \n\n\n\n",numbersToClean.size());
        System.out.printf("\n %d - abonents left \n\n\n\n",abonentList.size());
    }
}
