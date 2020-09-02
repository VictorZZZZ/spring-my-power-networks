package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Section;
import net.energo.grodno.pes.smsSender.repositories.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {
    SectionRepository sectionRepository;
    LineService lineService;

    @Autowired
    public SectionService(SectionRepository sectionRepository,LineService lineService) {
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
    }


    public void deepSave(List<Section> sections) {
        for(Section section:sections){
            Optional<Section> optSection = sectionRepository.findByNameAndSubstationId(section.getName(),section.getSubstation().getId());
            if(optSection.isPresent()){
                //System.out.println(section.getName()+" - СШ найдена. Пересохраняем");
                section.setId(optSection.get().getId());
                sectionRepository.save(section);
            }else {
                //System.out.println(section.getName()+" - СШ не найдена. Сохраняем");
                sectionRepository.save(section);
            }
            lineService.deepSave(section.getLines());
        }
    }

    public List<Section> getAllBySubstation(Integer parentId) {
        return sectionRepository.findBySubstationId(parentId);
    }

    public Section getOne(Integer id){
        return sectionRepository.getOne(id);
    }

    public void saveOne(Section section) {
        sectionRepository.save(section);
    }
}
