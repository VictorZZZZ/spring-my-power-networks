package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.FiderRepository;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiderService {
    private FiderRepository fiderRepository;
    private TpRepository tpRepository;
    private ResRepository resRepository;

    @Autowired
    public FiderService(FiderRepository fiderRepository, TpRepository tpRepository, ResRepository resRepository) {
        this.tpRepository = tpRepository;
        this.fiderRepository = fiderRepository;
        this.resRepository = resRepository;
    }

    public List<Fider> getAll() {
        return fiderRepository.findAll();
    }

    public void saveOne(Fider fider) {
        fiderRepository.save(fider);
    }

    public void saveAll(List<Fider> fiderList) {
        fiderRepository.saveAll(fiderList);
        fiderRepository.flush();
    }

    public Fider getOne(Long id) {
        return fiderRepository.getOne(id);
    }

    public void deleteOne(Long id) {
        fiderRepository.deleteById(id);
    }

    public List<String> updateAll(List<Fider> fidersList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Обработка Фидеров...");
        List<Fider> listToSave = new ArrayList<>();
        for (Fider fider:fidersList) {
            //поиск уже имеющихся фидеров, чтобы неделать лишний update в базе данных
            Fider bufferFider = fiderRepository.findOneByTpIdAndDbfId(fider.getTp().getId(),fider.getDbfId());
            if(bufferFider!=null){
                fider.setId(bufferFider.getId());
            } else {
                listToSave.add(fider);
            }
        }
        if(listToSave.size()>0) {
            fiderRepository.saveAll(listToSave);
            resultList.add("В базу добавлено "+listToSave.size()+" новых Фидеров");
        }
        resultList.add("Обработано "+fidersList.size()+" фидеров");
        resultList.addAll(updateBackCouples(fidersList));
        return resultList;
    }

    @Transactional
    private List<String> updateBackCouples(List<Fider> fidersList) {
        //Обратное сравнение базы со списком фидеров
        List<Fider> listToDelete = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных Фидеров.....");
        List<Fider> listFromBase = findAllByResId(fidersList.get(0).getTp().getRes().getId());
        for (Fider fiderFromBase: listFromBase) {
            boolean found=false;
            for(Fider fiderFromList:fidersList){
                if(fiderFromList.getId().equals(fiderFromBase.getId())){
                    found=true;
                    break;
                }
            }
            if(found==false) {
                if(!fiderFromBase.isInputManually()) {
                    //Если фидер не введён вручную, то можно его удалить
                    listToDelete.add(fiderFromBase);
                    resultList.add("Удален Фидер: " + fiderFromBase.toShortString());
                } else {
                    //Сообщить о том, что найдены фидеры введённые вручную
                    resultList.add("Фидер: "
                            + fiderFromBase.toShortString()+" (" + fiderFromBase.getTp().getName()
                            +") был введён(или изменён) вручную, и может быть удален только вручную.");
                }
            }
        }
        if(listToDelete.size()>0) {
            fiderRepository.deleteAll(listToDelete);
            //abonentRepository.deleteInBatch(listToDelete);
            resultList.add("===============================================================================================");
            resultList.add("Из Базы удалено "+listToDelete.size()+" Фидеров т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("===============================================================================================");
        } else {
            resultList.add("Несоответствий не обнаружено.");
        }
        return resultList;
    }

    private List<Fider> findAllByResId(Integer id) {
        List<Fider> fidersList =new ArrayList<>();
        List<Abonent> abonentList =new ArrayList<>();
        Res res = resRepository.getOne(id);
        //todo: переделать
        List<Tp> tpList = tpRepository.findAllByResIdOrderByName(res);
        for(Tp tp:tpList){
            fidersList.addAll(tp.getFiders());
        }
        return fidersList;
    }


}
