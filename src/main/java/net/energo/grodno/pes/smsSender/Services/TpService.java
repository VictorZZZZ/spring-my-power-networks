package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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

    public void saveOne(Tp tp) {
        tpRepository.save(tp);
    }

    public void saveAll(List<Tp> tpList){
        tpRepository.saveAll(tpList);
        tpRepository.flush();
    }

    public Tp getOne(Integer id) {
        return tpRepository.getOne(id);
    }

    public void deleteOne(Integer id) {
        tpRepository.deleteById(id);
    }

    public void updateAll(List<Tp> tpList) {
        List<Tp> listToSave = new ArrayList<>();
        for (Tp tp : tpList) {
            Tp bufferTp = tpRepository.findTopByDbfId(tp.getDbfId());
            if(bufferTp!=null)
                tp.setId(bufferTp.getId());
        }
        tpRepository.saveAll(tpList);
    }

    public Tp getOneByDbfId(int dbfId) {
        return tpRepository.findTopByDbfId(dbfId);
    }

    @Transactional
    public List<String> updateBackCouples(List<Tp> tpList) {
        List<String> resultList = new ArrayList<>();
        List<Tp> listFromBase = tpRepository.findAllByResId(tpList.get(0).getRes().getId());
        int counter = 0;
        for (Tp tp: listFromBase) {
            boolean foundTp=false;
            for(Tp compareTp:tpList){
                if(tp.getId()==compareTp.getId()){
                    foundTp=true;
                    break;
                }
            }
            if(foundTp==false) {
                counter++;
                deleteOne(tp.getId());
                resultList.add("Удалено ТП: "+tp.toShortString());
            }
        }
        if(counter>0) {
            resultList.add("==============================================================================");
            resultList.add("Из Базы удалено "+counter+" ТП т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("==============================================================================");
        }
        return resultList;
    }
}
