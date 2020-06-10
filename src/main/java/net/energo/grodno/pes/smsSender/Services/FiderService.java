package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.FiderRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FiderService {
    private FiderRepository fiderRepository;
    private TpRepository tpRepository;

    @Autowired
    public FiderService(FiderRepository fiderRepository,TpRepository tpRepository) {
        this.tpRepository = tpRepository;
        this.fiderRepository = fiderRepository;
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

    public Fider getOne(Integer id) {
        return fiderRepository.getOne(id);
    }

    public void deleteOne(Integer id) {
        fiderRepository.deleteById(id);
    }

    public void updateAll(List<Fider> fidersList) {
        for (Fider fider:fidersList) {
            Tp parentTp = tpRepository.findTopByDbfId(fider.getTp().getDbfId());
            Optional<Fider> optFider = parentTp.getFiders().stream().filter(fider1 -> fider1.getDbfId()==fider.getDbfId()).findFirst();
            if(optFider.isPresent()){
                Fider bufferFider = optFider.get();
                fider.setId(bufferFider.getId());
            }
        }
        fiderRepository.saveAll(fidersList);
    }


}
