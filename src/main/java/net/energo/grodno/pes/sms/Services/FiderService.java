package net.energo.grodno.pes.sms.Services;

import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.repositories.FiderRepository;
import net.energo.grodno.pes.sms.repositories.ResRepository;
import net.energo.grodno.pes.sms.repositories.TpRepository;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiderService {
    Logger logger = LoggerFactory.getLogger(FiderService.class);
    private FiderRepository fiderRepository;
    private TpRepository tpRepository;
    private ResRepository resRepository;
    private AbonentService abonentService;

    @Autowired
    public FiderService(FiderRepository fiderRepository, TpRepository tpRepository, ResRepository resRepository, AbonentService abonentService) {
        this.tpRepository = tpRepository;
        this.fiderRepository = fiderRepository;
        this.resRepository = resRepository;
        this.abonentService = abonentService;
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

    @Transactional
    public List<String> updateAll(List<Fider> fidersList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Обработка Фидеров...");
        List<Fider> listToSave = new ArrayList<>();
        for (Fider fider:fidersList) {
            //поиск уже имеющихся фидеров, чтобы неделать лишний update в базе данных
            Fider bufferFider = fiderRepository.findTopByTpIdAndDbfId(fider.getTp().getId(),fider.getDbfId())
                    .orElse(null);
            if(bufferFider!=null){
                fider.setId(bufferFider.getId());
            } else {
                listToSave.add(fider);
            }
        }
        if(!listToSave.isEmpty()) {
            fiderRepository.saveAll(listToSave);
            resultList.add("В базу добавлено "+listToSave.size()+" новых Фидеров");
        }

        resultList.add("Обработано "+fidersList.size()+" фидеров");
        resultList.addAll(updateBackCouples(fidersList));
        return resultList;
    }

    @Transactional
    public List<String> updateBackCouples(List<Fider> fidersList) {
        logger.info("Обновление обратных пар фидеров.");
        //Обратное сравнение базы со списком фидеров
        List<Fider> listToDelete = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных Фидеров.....");
        List<Fider> listFromBase = findAllByResId(fidersList.get(0).getTp().getResId());
        for (Fider fiderFromBase: listFromBase) {
            boolean found=false;
            for(Fider fiderFromList:fidersList){
                if(fiderFromList.getId().equals(fiderFromBase.getId())){
                    found=true;
                    break;
                }
            }
            if(!found) {
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
        if(!listToDelete.isEmpty()) {
            fiderRepository.deleteAll(listToDelete);
            resultList.add("===============================================================================================");
            resultList.add("Из Базы удалено "+listToDelete.size()+" Фидеров т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("===============================================================================================");
        } else {
            resultList.add("Несоответствий не обнаружено.");
        }
        return resultList;
    }

    @Transactional
    public List<Fider> findAllByResId(Integer id) {
        List<Fider> fidersList =new ArrayList<>();
        Res res = resRepository.getOne(id);
        //todo: переделать
        List<Tp> tpList = tpRepository.findAllByResIdOrderByName(res);
        for(Tp tp:tpList){
            fidersList.addAll(tp.getFiders());
        }
        return fidersList;
    }


    public void deepSave(List<Fider> fiders) {
        for(Fider fider:fiders){
            Fider fiderFromBase = fiderRepository.findTopByTpIdAndDbfId(fider.getTp().getId(),fider.getDbfId())
                    .orElse(null);
            if(fiderFromBase!=null){
                fider.setId(fiderFromBase.getId());
                fider.setDbfId(fiderFromBase.getDbfId());
                if(!fider.getAbonents().isEmpty()) {
                    abonentService.deepSave(fider.getAbonents());
                }
            } else {
                if(!fider.getAbonents().isEmpty()) {
                    saveOne(fider);
                    abonentService.deepSave(fider.getAbonents());
                }
            }
        }
    }

    public List<Fider> getAllByTp(Long parentId) {
        return fiderRepository.findByTpId(parentId);
    }
}
