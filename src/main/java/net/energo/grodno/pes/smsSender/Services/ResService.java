package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResService {
    private ResRepository resRepository;

    @Autowired
    public void setResRepository(ResRepository resRepository) {
        this.resRepository = resRepository;
    }

    public List<Res> getAllRes() {
        return resRepository.findAll();
    }

    public Res getOne(Integer id) {
        return resRepository.findById(id).get();
    }

    public void saveOne(Res res) {
        resRepository.save(res);
    }

    public void deleteOne(Integer id) {
        resRepository.deleteById(id);
    }
}
