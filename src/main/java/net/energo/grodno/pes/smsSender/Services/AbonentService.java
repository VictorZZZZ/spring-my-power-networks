package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbonentService {
    private AbonentRepository abonentRepository;

    @Autowired
    public void setAbonentRepository(AbonentRepository abonentRepository) {
        this.abonentRepository = abonentRepository;
    }
}
