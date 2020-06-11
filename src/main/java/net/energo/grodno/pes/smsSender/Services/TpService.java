package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TpService {
    private TpRepository tpRepository;

    @Autowired
    public void setTpRepository(TpRepository tpRepository) {
        this.tpRepository = tpRepository;
    }

    public List<Tp> getAll() {
        return tpRepository.findAll();
    }

    public void saveOne(Tp tp) {
        tpRepository.save(tp);
    }

    public void saveAll(List<Tp> tpList){
        tpRepository.saveAll(tpList);
        tpRepository.flush();
    }

    public Tp getOne(Integer id) {
        return tpRepository.getOne(id);
    }

    public void deleteOne(Integer id) {
        tpRepository.deleteById(id);
    }

    public Tp getOneByDbfId(int dbfId) {
        return tpRepository.findTopByDbfId(dbfId);
    }

    public List<String> updateAll(List<Tp> tpList) {
        List<Tp> listToSave = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        resultList.add("Обработка ТП...");
        for (Tp tp : tpList) {
            //поиск уже имеющихся в базе ТП, чтобы лишний раз не сохранять. И присвоить ID для дальнейшей работы
            Tp bufferTp = tpRepository.findTopByDbfId(tp.getDbfId());
            if(bufferTp!=null) {
                tp.setId(bufferTp.getId());
            } else {
                listToSave.add(tp);
            }
        }
        if(listToSave.size()>0) {
            tpRepository.saveAll(listToSave);
            resultList.add("В базу добавлено "+listToSave.size()+" новых ТП");
        }
        resultList.add("Обработано "+tpList.size()+" ТП");
        resultList.addAll(updateBackCouples(tpList));
        return resultList;
    }

    @Transactional
    public List<String> updateBackCouples(List<Tp> tpList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных ТП...");
        List<Tp> listFromBase = tpRepository.findAllByResId(tpList.get(0).getRes().getId());
        List<Tp> listToDelete = new ArrayList<>();
        for (Tp tp: listFromBase) {
            boolean foundTp=false;
            for(Tp compareTp:tpList){
                if(tp.getId()==compareTp.getId()){
                    foundTp=true;
                    break;
                }
            }
            if(foundTp==false) {
                if(!tp.isInputManually()) {
                    //Если ТП не было введено вручную
                    listToDelete.add(tp);
                    resultList.add("Удалено ТП: " + tp.toShortString());
                } else {
                    //Сообщить о том, что найдены абоненты введённые вручную
                    resultList.add("ТП: "
                            + tp.toShortString()+"было введено(или изменено) вручную, и может быть удалено только вручную.");
                }
            }
        }
        if(listToDelete.size()>0) {
            tpRepository.deleteAll(listToDelete);
            resultList.add("==============================================================================");
            resultList.add("Из Базы удалено "+listToDelete.size()+" ТП т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("==============================================================================");
        } else{
            resultList.add("Несоответствий не обнаружено");
        }
        return resultList;
    }
}
