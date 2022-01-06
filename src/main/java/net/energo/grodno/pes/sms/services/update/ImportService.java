package net.energo.grodno.pes.sms.services.update;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.energo.grodno.pes.sms.entities.Abonent;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
import net.energo.grodno.pes.sms.services.AbonentService;
import net.energo.grodno.pes.sms.services.FiderService;
import net.energo.grodno.pes.sms.services.TpService;
import net.energo.grodno.pes.sms.utils.importFromDbf.DBFManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ImportService {

    private final DBFManager dbfManager;
    private final TpService tpService;
    private final FiderService fiderService;
    private final AbonentService abonentService;

    @Transactional
    public List<String> updateDb() {
        List<String> resultOfImport = new ArrayList<>();

        Map<Integer, Tp> tpsFromDbf = dbfManager.getTpMap();
        List<Tp> tps = new ArrayList<>(tpsFromDbf.values());
        Integer resId = tps.get(0).getResId();

        log.info("Начало процесса Импорта");

        resultOfImport.addAll(tpService.updateAll(tps));

        List<Fider> fiders = new ArrayList<>();
        List<Abonent> abonents = new ArrayList<>();
        for (Tp tp : tps) {
            fiders.addAll(tp.getFiders());
            for (Fider fider : tp.getFiders()) {
                abonents.addAll(fider.getAbonents());
            }
        }
        log.info("Обновляем фидера.");
        resultOfImport.addAll(fiderService.updateAll(fiders));
        log.info("Обновляем абонентов.");
        resultOfImport.addAll(abonentService.updateAll(abonents));
        log.info("Убираем пустых и дубли.");
        tpService.removeDuplicatedAndEmpty(resId);
        return resultOfImport;
    }
}
