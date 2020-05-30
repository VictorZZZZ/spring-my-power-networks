package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
