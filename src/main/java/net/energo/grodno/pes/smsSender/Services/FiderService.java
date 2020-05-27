package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.repositories.FiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FiderService {
    private FiderRepository fiderRepository;

    @Autowired
    public FiderRepository getFiderRepository() {
        return fiderRepository;
    }
}
