package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import net.energo.grodno.pes.smsSender.repositories.TpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TpService {
    Logger logger = LoggerFactory.getLogger(TpService.class);
    private ResRepository resRepository;
    private TpRepository tpRepository;
    private FiderService fiderService;
    private UserService userService;

    @Autowired
    public TpService(ResRepository resRepository, TpRepository tpRepository, FiderService fiderService, UserService userService) {
        this.resRepository = resRepository;
        this.tpRepository = tpRepository;
        this.fiderService = fiderService;
        this.userService = userService;
    }

    public List<Tp> getAll() {
        return tpRepository.findAll();
    }

    public List<Tp> getAllByRes(Res res) {
        return tpRepository.findAllByResIdOrderByName(res);
    }

    public void saveOne(Tp tp) {
        tpRepository.save(tp);
    }

    public void saveAll(List<Tp> tpList){
        tpRepository.saveAll(tpList);
        tpRepository.flush();
    }

    public Tp getOne(Long id) {
        return tpRepository.getOne(id);
    }

    public void deleteOne(Long id) {
        tpRepository.deleteById(id);
    }

    public Tp getOneByDbfId(int dbfId) {
        return tpRepository.findTopByDbfId(dbfId);
    }

    public List<String> updateAll(List<Tp> tpList) {
        List<Tp> listToSave = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        messages.add("Обработка ТП...");
        for (Tp tp : tpList) {
            //поиск уже имеющихся в базе ТП, чтобы лишний раз не сохранять. И присвоить ID для дальнейшей работы
            Tp bufferTp = tpRepository.findTopByDbfId(tp.getDbfId());
            if(bufferTp!=null) {
                tp.setId(bufferTp.getId());
                tp.setPart(bufferTp.getPart());
            } else {
                listToSave.add(tp);
            }
        }
        if(listToSave.size()>0) {
            tpRepository.saveAll(listToSave);
            messages.add("В базу добавлено "+listToSave.size()+" новых ТП");
            logger.info("В базу добавлено {} новых ТП",listToSave.size());
        } else {
            String msg = "В базу не добавлено новых ТП";
            messages.add(msg);
            logger.info(msg);
        }
        messages.add("Обработано "+tpList.size()+" ТП");
        messages.addAll(updateBackCouples(tpList));
        return messages;
    }

    @Transactional
    public List<String> updateBackCouples(List<Tp> tpList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация обрытных пар ТП...");
        logger.info("Проверка обратных пар...");
        Res res = resRepository.getOne(tpList.get(0).getResId());
        List<Tp> listFromBase = tpRepository.findAllByResIdOrderByName(res);
        List<Tp> listToDelete = new ArrayList<>();
        for (Tp tp: listFromBase) {
            boolean foundTp=false;
            for(Tp compareTp:tpList){
                if(tp.getDbfId() == compareTp.getDbfId()){
                    foundTp=true;
                    break;
                }
            }
            if(!foundTp) {
                if(!tp.isInputManually()) {
                    //Если ТП не было введено вручную
                    listToDelete.add(tp);
                    logger.info("Будет удалено ТП: " + tp.toShortString());
                    resultList.add("Удалено ТП: " + tp.toShortString());
                } else {
                    //Сообщить о том, что найдены ТП введённые вручную
                    resultList.add("ТП: "
                            + tp.toShortString()+"было введено(или изменено) вручную, и может быть удалено только вручную.");
                }
            }
        }
        if(listToDelete.size()>0) {
            tpRepository.deleteAll(listToDelete);
            resultList.add("==============================================================================");
            resultList.add("Из Базы удалено "+listToDelete.size()+" ТП т.к. они не соответствовали списку из ДБФ файла");
            resultList.add("==============================================================================");
            logger.info("Из Базы удалено {} ТП т.к. они не соответствовали списку из ДБФ файла",listToDelete.size());
        } else{
            resultList.add("Несоответствий не обнаружено");
        }
        return resultList;
    }

    public List<Tp> searchByTpName(String searchLine) {
        return tpRepository.findByNameIgnoreCaseContains(searchLine);
    }


    /**
     * Для конкретного РЭСа
     * Удаляет все ТП у которых нет в подчинении Фидеров
     * Удаляет все ТП у которых совпадает название
     * @param resId {@link Integer}
     */
    public void removeDuplicatedAndEmpty(Integer resId){
        logger.info("Поиск пустых и дубликатов ТП!");
        List<Tp> tpList = tpRepository.findAllByResId(resId); //id res
        List<Tp> deleteList = new ArrayList<>();
        for(Tp tp: tpList){
            if(tp.getFiders().isEmpty()){
                deleteList.add(tp);
            }
            for (Tp tp1 : tpList) {
                if ((tp.getName().equals(tp1.getName())) && (!tp.getId().equals(tp1.getId()))) {

                    if (tp1.getPart() == null) {
                        tp1.setPart(tp.getPart());
                        System.out.println(tp1.getName());
                        tpRepository.save(tp1);
                    }
                    if (tp.getPart() == null) {
                        tp.setPart(tp1.getPart());
                        System.out.println(tp.getName());
                        tpRepository.save(tp);
                    }
                    break;
                }
            }
        }
        tpRepository.deleteAll(deleteList);
        tpRepository.flush();
        logger.info("Удалено {} пустых и дубликатов ТП!", deleteList.size());
    }


    public void deepSave(List<Tp> tps) {
        for (Tp tp:tps) {
            //полное совпадение
            List<Tp> tpList =tpRepository.findByNameAndResId(tp.getName(),tp.getResId());
            if(tpList.size()==1) {
                Tp tpFromBase = tpList.get(0);
                tp.setId(tpFromBase.getId());
                tp.setDbfId(tpFromBase.getDbfId());
                tpRepository.save(tp);
                fiderService.deepSave(tp.getFiders());
                continue;
            }

            //Берестовица
            /*if(tp.getResId()==1){
                //System.out.printf("%s \n",tp.getName());
                Pattern pattern = Pattern.compile("[а-яА-Я]{1,2}\\-\\d{1,3}:");//от 1 до 2 русских букв  + знак "тире" + 1 или 3 цифры+ :
                Matcher matcher = pattern.matcher(tp.getName());
                if(matcher.find()){
                    //System.out.printf("%s - %s\n",matcher.group(),tp.getName());
                    tpList = tpRepository.findByNameAndResId(getDigitString(matcher.group()),tp.getRes().getId());
                    if(tpList.size()==1){
                        Tp tpFromBase=tpList.get(0);
                        tp.setId(tpFromBase.getId());
                        tp.setDbfId(tpFromBase.getDbfId());
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());
                    }
                    if(tpList.size()==0){
                        if(tp.getFiders().get(0).getAbonents().size()>0){
                            tpRepository.save(tp);
                            fiderService.deepSave(tp.getFiders());
                        }
                    }
                    if(tpList.size()>1){
                        System.out.printf("%5s - %40s\t%d\n",matcher.group(),tp.getName(),tpList.size());
                    }

                } else {
                    //System.out.printf("%s - не соответствет шаблону\n", tp.getName());
                    if(tp.getFiders().get(0).getAbonents().size()>0) {
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());
                    }
                }

            }
*/
            //Cельский РЭС и Щучинский РЭС

            if(tp.getResId().equals(ResService.GSRES_ID)
                    /*|| tp.getResId()==3*/){
                Pattern pattern = Pattern.compile("[а-яА-Я]{1,2}-\\d{1,3}");//от 1 до 2 русских букв  + знак "тире" + 1 или 3 цифры
                Matcher matcher = pattern.matcher(tp.getName());
                if(matcher.find()){

                    tpList = tpRepository.findByNameAndResId(matcher.group(),tp.getRes().getId());
                    if(tpList.size()==1){
                        Tp tpFromBase=tpList.get(0);
                        tp.setId(tpFromBase.getId());
                        tp.setDbfId(tpFromBase.getDbfId());
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());
                    }
                    if(tpList.isEmpty() && (tp.getFiders().get(0).getAbonents().isEmpty())){
                            tpRepository.save(tp);
                            fiderService.deepSave(tp.getFiders());
                    }
                    if(tpList.size()>1){
                        System.out.printf("%5s - %40s\t%d\n",matcher.group(),tp.getName(),tpList.size());
                    }

                } else {
                    //System.out.printf("%s - совпадения не найдено\n",tp.getName());
                    if(tp.getFiders().get(0).getAbonents().size()>0) {
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());

                    }
                }
            }



            //ГГРЭС
            /*
            if(tp.getResId()==4){
                tpList = tpRepository.findByNameAndResId(tp.getName(),tp.getRes().getId());
                if(tpList.size()==1){
                    Tp tpFromBase=tpList.get(0);
                    tp.setId(tpFromBase.getId());
                    tp.setDbfId(tpFromBase.getDbfId());
                    tpRepository.save(tp);
                    fiderService.deepSave(tp.getFiders());
                } else {
                    if(tp.getFiders().get(0).getAbonents().size()>0){
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());
                    }
                }
            }*/


        }
    }

    private String getDigitString(String str) {
        char[] charName = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char ch:charName) {
            if(ch=='1' || ch=='2'|| ch=='3' || ch=='4' || ch=='5' || ch=='6' || ch=='7' || ch=='8' || ch=='9' || ch=='0'){
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public List<Tp> getAllByPart(Long parentId) {
        return tpRepository.findByPartId(parentId);
    }

    public Page<Tp> getNotLinkedTpsByUser(String name,Pageable pageable) throws Exception {
        User user = userService.findByUsername(name);
        return tpRepository.findAllByResIdAndPartIdOrderByName(user.getRes().getId(),null,pageable);
    }

    public Long countNotLinkedTpsByUser(String name) throws Exception{
        User user = userService.findByUsername(name);
        return tpRepository.countByResIdAndPartIdOrderByName(user.getRes().getId(),null);
    }

    public Page<Tp> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Tp> list;
        long tpCount = tpRepository.count();

        if (tpCount < startItem) {
            list = Collections.emptyList();
            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), tpCount);
        } else {
            return tpRepository.findAll(pageable);
        }

    }

    public long getCount() {
        return tpRepository.count();
    }
}

