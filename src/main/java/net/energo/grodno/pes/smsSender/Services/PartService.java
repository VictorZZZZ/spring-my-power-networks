package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Part;
import net.energo.grodno.pes.smsSender.repositories.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartService {
    private PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public void deepSave(List<Part> parts) {
        for (Part part:parts) {
            Optional<Part> optPart = partRepository.findByNameAndLineId(part.getName(),part.getLine().getId());
            if(optPart.isPresent()){
                System.out.println(part.getName()+" - участок найден. Пересохраняем");
                part.setId(optPart.get().getId());
                partRepository.save(part);
            } else {
                System.out.println(part.getName()+" - новый участок. Сохраняем");
                partRepository.save(part);
            }
        }
    }
}
