package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.entities.users.User;
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
    private TpRepository tpRepository;
    private FiderService fiderService;
    private UserService userService;

    @Autowired
    public TpService(TpRepository tpRepository, FiderService fiderService, UserService userService) {
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
        List<String> resultList = new ArrayList<>();
        resultList.add("Обработка ТП...");
        for (Tp tp : tpList) {
            //поиск уже имеющихся в базе ТП, чтобы лишний раз не сохранять. И присвоить ID для дальнейшей работы
            Tp bufferTp = tpRepository.findTopByDbfId(tp.getDbfId());
            if(bufferTp!=null) {
                tp.setId(bufferTp.getId());
            } else {
                listToSave.add(tp);
            }
        }
        if(listToSave.size()>0) {
            tpRepository.saveAll(listToSave);
            resultList.add("В базу добавлено "+listToSave.size()+" новых ТП");
            logger.info("В базу добавлено {} новых ТП",listToSave.size());
        }
        resultList.add("Обработано "+tpList.size()+" ТП");
        resultList.addAll(updateBackCouples(tpList));
        return resultList;
    }

    @Transactional
    public List<String> updateBackCouples(List<Tp> tpList) {
        List<String> resultList = new ArrayList<>();
        resultList.add("Синхронизация базы Базы данных ТП...");
        logger.info("Проверка обратных пар...");
        List<Tp> listFromBase = tpRepository.findAllByResIdOrderByName(tpList.get(0).getRes());
        List<Tp> listToDelete = new ArrayList<>();
        for (Tp tp: listFromBase) {
            boolean foundTp=false;
            for(Tp compareTp:tpList){
                if(tp.getId()==compareTp.getId()){
                    foundTp=true;
                    break;
                }
            }
            if(foundTp==false) {
                if(!tp.isInputManually() && tp.getDbfId()!=0) {
                    //Если ТП не было введено вручную
                    listToDelete.add(tp);
                    logger.info("Будет удалено ТП: " + tp.toShortString());
                    resultList.add("Удалено ТП: " + tp.toShortString());
                } else {
                    //Сообщить о том, что найдены ТП введённые вручную
                    logger.warn("ТП: "
                            + tp.toShortString()+"было введено(или изменено) вручную, и может быть удалено только вручную.");
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


    public void deepSave(List<Tp> tps) {
        for (Tp tp:tps) {
            //полное совпадение
            List<Tp> tpList =tpRepository.findByNameAndResId(tp.getName(),tp.getResId());
            if(tpList.size()==1) {
                Tp tpFromBase = tpList.get(0);
                tp.setId(tpFromBase.getId());
                tpRepository.save(tp);
                fiderService.deepSave(tp.getFiders());
                continue;
            }

            //Берестовица
            if(tp.getResId()==1){
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

            //Cельский РЭС и Щучинский РЭС
            if(tp.getResId()==2 || tp.getResId()==3){
                Pattern pattern = Pattern.compile("[а-яА-Я]{1,2}\\-\\d{1,3}");//от 1 до 2 русских букв  + знак "тире" + 1 или 3 цифры
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
                    //System.out.printf("%s - совпадения не найдено\n",tp.getName());
                    if(tp.getFiders().get(0).getAbonents().size()>0) {
                        tpRepository.save(tp);
                        fiderService.deepSave(tp.getFiders());

                    }
                }
            }

            //ГГРЭС
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
            }

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

    public List<Tp> getNotLinkedTpsByUser(String name) throws Exception {
        User user = userService.findByUsername(name);
        return tpRepository.findAllByResIdAndPartIdOrderByName(user.getRes().getId(),null);
    }

    public Page<Tp> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Tp> list;
        long tpCount = tpRepository.count();

        if (tpCount < startItem) {
            list = Collections.emptyList();
            Page<Tp> tpPage
                    = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), tpCount);
            return tpPage;
        } else {
            Page<Tp> tpPage
                    = tpRepository.findAll(pageable);
            return tpPage;
        }

    }

    public long getCount() {
        return tpRepository.count();
    }
}

