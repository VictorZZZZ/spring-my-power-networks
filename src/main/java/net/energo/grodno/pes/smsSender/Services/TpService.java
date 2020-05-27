package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpService {
    private TpRepository tpRepository;

    @Autowired
    public void setTpRepository(TpRepository tpRepository) {
        this.tpRepository = tpRepository;
    }

    public List<Tp> getAll() {
        return tpRepository.findAll();
    }
}
