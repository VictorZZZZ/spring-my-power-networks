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
    public List<Integer> getResStat(Integer id){
        List<Integer> stat = new ArrayList<>();
        Res res=getOne(id);
        List<Tp> tps = res.getTps();
        int tpSize = tps.size();
        stat.add(tpSize);
        int abonentSize = 0;
        for(Tp tp: tps){
            for(Fider fider:tp.getFiders()){
                abonentSize+=fider.getAbonents().size();
            }
        }
        stat.add(abonentSize);
        return stat;
    }
}
