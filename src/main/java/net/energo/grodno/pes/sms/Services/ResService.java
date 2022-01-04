package net.energo.grodno.pes.sms.Services;

import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.repositories.ResRepository;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
import net.energo.grodno.pes.sms.entities.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResService {
    public static final Integer BRES_ID = 1;
    public static final Integer GSRES_ID = 2;
    public static final Integer GGRES_ID = 4;
    public static final Integer SCHRES_ID = 3;

    private ResRepository resRepository;
    private SubstationService substationService;
    private static Logger logger = LoggerFactory.getLogger(ResService.class);
    private UserService userService;

    public ResService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResRepository(ResRepository resRepository, SubstationService substationService) {
        this.substationService = substationService;
        this.resRepository = resRepository;
    }

    public List<Res> getAllRes() {
        return resRepository.findAll();
    }

    public Res getOne(Integer id) {
        Optional<Res> optRes = resRepository.findById(id);
        if (optRes.isPresent()) {
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
    public List<Long> getResStat(Integer id) {
        List<Long> stat = new ArrayList<>();
        Res res = getOne(id);
        List<Tp> tps = res.getTps();
        long tpSize = tps.size();
        stat.add(tpSize);
        stat.add(res.getCachedAbonentsCount());
        return stat;
    }

    @Transactional
    public void countAbonents(Integer id) {
        Res res = getOne(id);
        logger.info("{} старт пересчета пользователей.", res.getName());
        List<Tp> tps = res.getTps();
        logger.info("Количество ТП в РЭСе - {}", tps.size());
        int abonentSize = 0;
        for (Tp tp : tps) {
            for (Fider fider : tp.getFiders()) {
                abonentSize += fider.getAbonents().size();
            }
        }
        res.setCachedAbonentsCount((long) abonentSize);
        saveOne(res);
        logger.info("{} конец пересчета пользователей.", res.getName());
    }

    @Transactional
    public void deepSave(List<Res> resList) {
        System.out.println("Res:Deep Save");
        for (Res res : resList) {
            if (getOne(res.getId()) != null) {
                if (res.getId().equals(GSRES_ID)) {
                    logger.info("{} найден.", res.getName());
                    substationService.deepSave(res.getSubstations());
                }
            }
        }
    }

    public Res getResByUserName(String username) throws Exception {
        User user = userService.findByUsername(username);
        return user.getRes();
    }
}
