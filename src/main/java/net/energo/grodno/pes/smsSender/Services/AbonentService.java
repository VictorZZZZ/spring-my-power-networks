package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
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
