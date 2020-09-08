package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.controllers.CartController;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResService {
    private ResRepository resRepository;
    private SubstationService substationService;
    private static Logger logger = LoggerFactory.getLogger(ResService.class);

    @Autowired
    public void setResRepository(ResRepository resRepository,SubstationService substationService) {
        this.substationService = substationService;
        this.resRepository = resRepository;
    }

    public List<Res> getAllRes() {
        return resRepository.findAll();
    }

    public Res getOne(Integer id) {
        Optional<Res> optRes = resRepository.findById(id);
        if(optRes.isPresent()) {
            return resRepository.findById(id).get();
        } else {
            return null;
        }
    }

    public void saveOne(Res res) {
        resRepository.save(res);
    }

    public void deleteOne(Integer id) {
        resRepository.deleteById(id);
    }

    @Transactional
    @Cacheable("stat")
    public List<Long> getResStat(Integer id){
        List<Long> stat = new ArrayList<>();
        Res res=getOne(id);
        List<Tp> tps = res.getTps();
        long tpSize = tps.size();
        stat.add(tpSize);
        stat.add(res.getCachedAbonentsCount());
        return stat;
    }

    @Transactional
    public void countAbonents(Integer id){
        Res res=getOne(id);
        logger.info("{} старт пересчета пользователей.",res.getName());
        List<Tp> tps = res.getTps();
        logger.info("Количество ТП в РЭСе - {}", tps.size());
        int abonentSize = 0;
        for(Tp tp: tps){
            for(Fider fider:tp.getFiders()){
                abonentSize+=fider.getAbonents().size();
            }
        }
        res.setCachedAbonentsCount((long) abonentSize);
        saveOne(res);
        logger.info("{} конец пересчета пользователей.",res.getName());
    }

    @Transactional
    public void deepSave(List<Res> resList){
        System.out.println("Res:Deep Save");
        for (Res res:resList) {
            if(getOne(res.getId())!=null){
                System.out.println(res.getName() + " найден.");
                substationService.deepSave(res.getSubstations());
            }
        }
    }
}
