package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Line;
import net.energo.grodno.pes.smsSender.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {
    private LineRepository lineRepository;
    private PartService partService;

    @Autowired
    public LineService(LineRepository lineRepository, PartService partService) {
        this.lineRepository = lineRepository;
        this.partService = partService;
    }


    public void deepSave(List<Line> lines) {
        for (Line line:lines) {
            Optional<Line> optLine = lineRepository.findByNameAndSectionId(line.getName(),line.getSection().getId());
            if(optLine.isPresent()){
                //System.out.println(line.getName()+" - линия найдена. Пересохраняем");
                line.setId(optLine.get().getId());
                lineRepository.save(line);
            } else {
                //System.out.println(line.getName()+" новая. Cохраняем");
                lineRepository.save(line);
            }
            partService.deepSave(line.getParts());
        }
    }
}
