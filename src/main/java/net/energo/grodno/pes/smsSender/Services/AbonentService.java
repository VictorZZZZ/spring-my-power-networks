package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AbonentService {
    Logger logger = LoggerFactory.getLogger(AbonentService.class);

    private AbonentRepository abonentRepository;
    private TpRepository tpRepository;
    private ResRepository resRepository;

    @Autowired
    public void setAbonentRepository(AbonentRepository abonentRepository,TpRepository tpRepository,ResRepository resRepository) {
        this.abonentRepository = abonentRepository;
        this.tpRepository = tpRepository;
        this.resRepository = resRepository;
    }

    public List<Abonent> getAll() {
        return abonentRepository.findAll();
    }

    public Abonent getOne(Long id) {
        return abonentRepository.findOneByAccountNumber(id);
    }

    public Abonent findOne(Long id) {
        return abonentRepository.findOneByAccountNumber(id);
    }

    public Abonent saveOne(Abonent abonent) {
        return abonentRepository.save(abonent);
    }

    public void saveAll(List<Abonent> abonentList) {
//        if(abonentList.size()>1000) {
//            for (int i = 0; i < abonentList.size() / 1000; i++) {
//                System.out.printf("Вставка с %d по %d записей: ",i*1000,i * 1000 + 1000 - 1);
//                long startTime=System.currentTimeMillis();
//                long endTime = 0;
//                List<Abonent> subList = abonentList.subList(i * 1000, i * 1000 + 1000 - 1);
//                abonentRepository.saveAll(subList);
//                endTime=System.currentTimeMillis();
//                System.out.printf("заняло %f секунд \n",((endTime-startTime)/1000F));
//                System.out.printf("Записано %d записей \n================\n",subList.size());
//            }
//        } else {
            abonentRepository.saveAll(abonentList);
            //abonentRepository.flush();
        //}
    }

    public void deleteOne(Long id) {
        abonentRepository.deleteById(id);
    }

    public Page<Abonent> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Abonent> list;
        long abonentCount = abonentRepository.count();

        if (abonentCount < startItem) {
            list = Collections.emptyList();

            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), abonentCount);
        } else {
            return abonentRepository.findAll(pageable);
        }

    }

    public List<String> updateAll(List<Abonent> abonentList) {
        List<String> messages = new ArrayList<>();
        messages.add("Обработка абонентов...");
        for(Abonent abonent:abonentList){
            //поиск абонентов для присвоения поля notes из Базы(Остальные данные считаем верными из DBF)
            Abonent bufferAbonent = abonentRepository.findOneByAccountNumber(abonent.getAccountNumber());
            if(bufferAbonent!=null){
                abonent.setNotes(bufferAbonent.getNotes());
            }

        }
        logger.info("Сохранение абонентов.");
        abonentRepository.saveAll(abonentList);
        messages.add("Обработано "+abonentList.size()+" абонентов");
        logger.info("Проверка обратных пар.");
        messages.addAll(updateBackCouples(abonentList));
        return messages;
    }

    private List<String> updateBackCouples(List<Abonent> abonentList) {
        //Обратное сравнение базы со списком абонентов
        List<Abonent> listToDelete = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных Абонентов.....");
        List<Abonent> listFromBase = findAllByResId(abonentList.get(0).getFider().getTp().getResId());
        logger.info("В Базе Данного РЭСа {} абонентов. В списке для сравнения {}",listFromBase.size(),abonentList.size());
        for (Abonent abonentFromBase: listFromBase) {
            boolean found=false;
            for(Abonent abonentFromList:abonentList){
                if(abonentFromList.getAccountNumber().equals(abonentFromBase.getAccountNumber())){
                    found=true;
                    break;
                }
            }
            if(!found) {
                if(!abonentFromBase.getInputManually()) {
                    //Если абонент не введён вручную, то можно его удалить
                    listToDelete.add(abonentFromBase);
                    logger.info("Будет удален абонент {}",abonentFromBase.toShortString());
                    resultList.add("Удален Абонент: " + abonentFromBase.toShortString());
                } else {
                    //Сообщить о том, что найдены абоненты введённые вручную
                    resultList.add("Абонент: "
                            + abonentFromBase.toShortString()+" (" + abonentFromBase.getFider().getName() +" - "+ abonentFromBase.getFider().getTp().getName()
                            +") был введён(или изменён) вручную, и может быть удален только вручную.");
                }
            }
        }
        logger.info("Из Базы будет удалено {} Абонентов т.к. они не соответствовали списку из ДБФ файла",listToDelete.size());
        if(listToDelete.size()>0) {
            abonentRepository.deleteAll(listToDelete);
            //abonentRepository.deleteInBatch(listToDelete);
            resultList.add("=====================================================================================");
            resultList.add("Из Базы удалено "+listToDelete.size()+" Абонентов т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("=====================================================================================");
        } else {
            resultList.add("Несоответствий не обнаружено.");
        }
        return resultList;
    }


    public List<Abonent> findAllByResId(Integer id) {
        List<Abonent> abonentList =new ArrayList<>();
        Res res = resRepository.getOne(id);
        List<Tp> tpList = tpRepository.findAllByResIdOrderByName(res);
        for(Tp tp:tpList){
            List<Fider> fiderList=tp.getFiders();
            for(Fider fider:fiderList){
                if(!fider.getAbonents().isEmpty())
                    abonentList.addAll(fider.getAbonents());
            }
        }
        return abonentList;
    }

    @Cacheable("totalAbonents")
    public Long getCount(){
        return abonentRepository.count();
    }

    public List<Abonent> searchBySurname(String searchLine) {
        return abonentRepository.findBySurnameIgnoreCaseContaining(searchLine);
    }

    public List<Abonent> searchByNumber(String searchLine) {
        List<Abonent> abonentList=new ArrayList<>();
        abonentList.addAll(abonentRepository.findByHomePhoneContaining(searchLine));
        abonentList.addAll(abonentRepository.findByFirstPhoneContaining(searchLine));
        abonentList.addAll(abonentRepository.findBySecondPhoneContaining(searchLine));

        return abonentList;
    }

    public List<Abonent> searchByAccountNumber(String searchLine) {
        try {
            Long.valueOf(searchLine);
            return abonentRepository.findAllByAccountNumber(Long.valueOf(searchLine));
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public void deepSave(List<Abonent> abonents) {
        for (Abonent abonent:abonents){
            Abonent abonentFromBase = abonentRepository.findByFiderIdAndSurname(abonent.getFider().getId(),abonent.getSurname());
            if(abonentFromBase != null){
                abonent.setAccountNumber(abonentFromBase.getAccountNumber());
                saveOne(abonent);
            } else {
                saveOne(abonent);
            }
        }
    }
}
