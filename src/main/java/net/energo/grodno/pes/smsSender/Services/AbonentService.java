package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AbonentService {
    private AbonentRepository abonentRepository;

    @Autowired
    public void setAbonentRepository(AbonentRepository abonentRepository) {
        this.abonentRepository = abonentRepository;
    }

    public List<Abonent> getAll() {
        return abonentRepository.findAll();
    }

    public Abonent getOne(Integer id) {
        return abonentRepository.getOne(id);
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

    public void deleteOne(Integer id) {
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
        } else {
            int toIndex = (int) Math.min(startItem + pageSize,abonentCount );
            list = abonentRepository.findAll().subList(startItem, toIndex);

        }

        Page<Abonent> abonentPage
                //= new PageImpl<Abonent>(list, PageRequest.of(currentPage, pageSize), abonentCount);
                = abonentRepository.findAll(pageable);

        return abonentPage;
    }
}
