package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class AbonentService {
    private AbonentRepository abonentRepository;
    private TpRepository tpRepository;

    @Autowired
    public void setAbonentRepository(AbonentRepository abonentRepository,TpRepository tpRepository) {
        this.abonentRepository = abonentRepository;
        this.tpRepository = tpRepository;
    }

    public List<Abonent> getAll() {
        return abonentRepository.findAll();
    }

    public Abonent getOne(Long id) {
        return abonentRepository.getOne(id);
    }

    public Abonent findOne(Long id) {
        return abonentRepository.findOneByAccountNumber(id);
    }

    public void saveOne(Abonent abonent) {
        abonentRepository.save(abonent);
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
            Page<Abonent> abonentPage
                = new PageImpl<Abonent>(list, PageRequest.of(currentPage, pageSize), abonentCount);

            return abonentPage;
        } else {
            Page<Abonent> abonentPage
                    = abonentRepository.findAll(pageable);
            return abonentPage;
        }

    }

    public List<String> updateAll(List<Abonent> abonentList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Обработка абонентов...");
        List<Abonent> listToSave = new ArrayList<>();
        for(Abonent abonent:abonentList){
            //поиск абонентов для присвоения поля notes из Базы(Остальные данные считаем верными из DBF)
            Abonent bufferAbonent = abonentRepository.findOneByAccountNumber(abonent.getAccountNumber());
            if(bufferAbonent!=null){
                abonent.setNotes(bufferAbonent.getNotes());
            }

        }
        abonentRepository.saveAll(abonentList);
        resultList.add("Обработано "+abonentList.size()+" абонентов");
        resultList.addAll(updateBackCouples(abonentList));
        return resultList;
    }

    private List<String> updateBackCouples(List<Abonent> abonentList) {
        //Обратное сравнение базы со списком абонентов
        List<Abonent> listToDelete = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных Абонентов.....");
        List<Abonent> listFromBase = findAllByResId(abonentList.get(0).getFider().getTp().getRes().getId());
        int counter = 0;
        for (Abonent abonentFromBase: listFromBase) {
            boolean found=false;
            for(Abonent abonentFromList:abonentList){
                if(abonentFromList.getAccountNumber().equals(abonentFromBase.getAccountNumber())){
                    found=true;
                    break;
                }
            }
            if(found==false) {
                if(!abonentFromBase.getInputManually()) {
                    //Если абонент не введён вручную, то можно его удалить
                    listToDelete.add(abonentFromBase);
                    resultList.add("Удален Абонент: " + abonentFromBase.toShortString());
                } else {
                    //Сообщить о том, что найдены абоненты введённые вручную
                    resultList.add("Абонент: "
                            + abonentFromBase.toShortString()+" (" + abonentFromBase.getFider().getName() +" - "+ abonentFromBase.getFider().getTp().getName()
                            +") был введён(или изменён) вручную, и может быть удален только вручную.");
                }
            }
        }
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


    private List<Abonent> findAllByResId(Integer id) {
        List<Abonent> abonentList =new ArrayList<>();
        List<Tp> tpList = tpRepository.findAllByResId(id);
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
    };
}
