package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class RemoveEmptyTp extends AbstractClass {
    @Autowired
    private TpRepository tpRepository;
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
        List<Tp> tpList = tpRepository.findAllByResId(4); //id res
        List<Tp> deleteList = new ArrayList<>();
        for(Tp tp: tpList){
            if(tp.getFiders().size()==0){
                deleteList.add(tp);
            }
            for (Tp tp1 : tpList) {
                if ((tp.getName().equals(tp1.getName())) && (tp.getId() != tp1.getId())) {

                    if (tp1.getPart() == null) {
                        tp1.setPart(tp.getPart());
                        tpRepository.save(tp1);
                        System.out.print(".");
                    }
                    if (tp.getPart() == null) {
                        tp.setPart(tp1.getPart());
                        tpRepository.save(tp);
                        System.out.print(":");
                    }

//                    System.out.printf("Id:%d Name:%s DbfId:%d Fiders:%d PartId:%s\nId:%d Name:%s DbfId:%d Fiders:%d PartId:%s \n \n"
//                            ,tp.getId(),tp.getName(),tp.getDbfId(),tp.getFiders().size(),tp.getPart(),
//                             tp1.getId(),tp1.getName(),tp1.getDbfId(),tp1.getFiders().size(),tp1.getPart());
                    break;
                }
            }
        }
        tpRepository.deleteAll(deleteList);
        tpRepository.flush();
        System.out.println("\nУдалено ТП " + deleteList.size());
    }
}
