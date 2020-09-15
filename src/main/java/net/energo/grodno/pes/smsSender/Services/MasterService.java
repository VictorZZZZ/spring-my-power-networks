package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Master;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterService {
    private final MasterRepository masterRepository;

    @Autowired
    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public List<Master> getAll() {
        return masterRepository.findAll();
    }

    public long count() {
        return masterRepository.count();
    }

    public void saveOne(Master master) {
        masterRepository.save(master);
    }

    public Master getOne(Long id) {
        return masterRepository.getOne(id);
    }

    public void deleteOne(Long id) {
        masterRepository.deleteById(id);
    }

    public List<Master> getAllByRes(Res res) {
        return masterRepository.findAllByRes(res);
    }
}
