package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResService {
    private ResRepository resRepository;

    @Autowired
    public void setResRepository(ResRepository resRepository) {
        this.resRepository = resRepository;
    }

    public List<Res> getAllRes() {
        return resRepository.findAll();
    }

    public Res getOne(Integer id) {
        return resRepository.findById(id).get();
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
        List<Tp> tps = res.getTps();
        int tpSize = tps.size();
        int abonentSize = 0;
        for(Tp tp: tps){
            for(Fider fider:tp.getFiders()){
                abonentSize+=fider.getAbonents().size();
            }
        }
        res.setCachedAbonentsCount((long) abonentSize);
        saveOne(res);
    }
}
