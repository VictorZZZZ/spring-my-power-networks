package net.energo.grodno.pes.sms.Services;

import net.energo.grodno.pes.sms.entities.Substation;
import net.energo.grodno.pes.sms.repositories.SubstationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubstationService {
    private SubstationRepository substationRepository;
    private SectionService sectionService;

    @Autowired
    public SubstationService(SubstationRepository substationRepository, SectionService sectionService) {
        this.substationRepository = substationRepository;
        this.sectionService = sectionService;
    }

    public void deepSave(List<Substation> substations) {
        System.out.println("\tSubstation:Deep Save");
        for(Substation substation:substations){
            Optional<Substation> optSubst = substationRepository.findByNameAndResId(substation.getName(),substation.getRes().getId());
            if(optSubst.isPresent()){
                //Подстанция найдена. Сверяем id рэса
                //System.out.println(substation.getName()+" - Подстанция найдена. Пересохраняем");
                substation.setId(optSubst.get().getId());
                substationRepository.save(substation);
            } else {
                //System.out.println(substation.getName() + " - Сохраняем");
                substationRepository.save(substation);
            }
            sectionService.deepSave(substation.getSections());
        }
    }

    public List<Substation> getAllByRes(Integer id) {
        return substationRepository.findByResIdOrderByName(id);
    }

    public Substation getOne(Integer id) {
        return substationRepository.getOne(id);
    }

    public void saveOne(Substation substation) {
        substationRepository.save(substation);
    }
}

