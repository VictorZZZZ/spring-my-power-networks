package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.repositories.FiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiderService {
    private FiderRepository fiderRepository;

    @Autowired
    public void setFiderRepository(FiderRepository fiderRepository) {
        this.fiderRepository = fiderRepository;
    }

    public List<Fider> getAll() {
        return fiderRepository.findAll();
    }

    public void saveOne(Fider fider) {
        fiderRepository.save(fider);
    }

    public Fider getOne(Integer id) {
        return fiderRepository.getOne(id);
    }

    public void deleteOne(Integer id) {
        fiderRepository.deleteById(id);
    }
}
