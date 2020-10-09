package net.energo.grodno.pes.smsSender.utils.importFromDbf;

import com.linuxense.javadbf.*;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Component
public class DBFManager {

    Logger logger = LoggerFactory.getLogger(DBFManager.class);

    private static DBFReader tpReader;

    private static DBFReader fiderReader;

    private static DBFReader abonentReader;

    private List<Fider> fiderList = new ArrayList<>();
    private Map<Integer,Tp> tpMap = new HashMap<>();
    private List<Abonent> abonentList= new ArrayList<>();
    private List<String> errors= new ArrayList<>();


    public DBFManager() {
    }

    public DBFManager(String tpFilename, String fiderFilename, String abonentFilename) {
        try {
            if ((tpFilename.endsWith(".dbf") || tpFilename.endsWith(".DBF"))
                    && (fiderFilename.endsWith(".dbf") || fiderFilename.endsWith(".DBF"))
                    && (abonentFilename.endsWith(".dbf") || abonentFilename.endsWith(".DBF"))) {
                // create a DBFReader object
                //reader = new DBFReader(new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/static/files/ГГРЭС/GGRES.DBF"));
                tpReader = new DBFReader(new FileInputStream(tpFilename));
                fiderReader = new DBFReader(new FileInputStream(fiderFilename));
                abonentReader = new DBFReader(new FileInputStream(abonentFilename));
            } else {
                this.errors.add("Файлы имеют неверное раширение(необходимо .dbf!)");
            }

            logger.info("Connected to DBF");
        } catch (DBFException  e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid(){
        if(errors.size()<1) {

            if (!tpReader.getField(0).getName().equals("COD")
                    || !tpReader.getField(1).getName().equals("RES")
                    || !tpReader.getField(2).getName().equals("TP")) {
                errors.add("Файл со списком ТП не валиден!");
            }
            if (!fiderReader.getField(0).getName().equals("COD_FID")
                    || !fiderReader.getField(1).getName().equals("НАИМЕНОВ")) {
                errors.add("Файл со списком Фидеров не валиден!");
            }

            if (!abonentReader.getField(0).getName().equals("LIC_SCH")
                    || !abonentReader.getField(1).getName().equals("FAM")
                    || !abonentReader.getField(2).getName().equals("NAME")
                    || !abonentReader.getField(3).getName().equals("OTCH")
                    || !abonentReader.getField(4).getName().equals("TELEF")
                    || !abonentReader.getField(6).getName().equals("COD_TP")
                    || !abonentReader.getField(7).getName().equals("COD_FID")
                    || !abonentReader.getField(8).getName().equals("OPORA")
                    || !abonentReader.getField(9).getName().equals("MTC")
                    || !abonentReader.getField(10).getName().equals("VEL")) {
                errors.add("Файл со списком Абонентов не валиден!");
            }

        }

        if(errors.size()>0) return false;
        else return true;
    }

    public static void main(String args[]) {
        DBFManager dbfManager = new DBFManager(
                System.getProperty("user.dir")+"\\src\\main\\resources\\static\\files\\БРЭС\\1KARTTP.DBF",
                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\БРЭС\\1KARTFID.DBF",
                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\БРЭС\\BRES.DBF");
        System.out.println(dbfManager.isValid());
        dbfManager.manage();
    }

    public void manage(){
        logger.info("Начало процесса преобразования базы данных DBF...");
        long startTime = System.currentTimeMillis(); // Get the start Time
        long endTime = 0;

        readAllFider();
        readAllTp();
        readAllAbonent();
        clearEmptyFiders();

        endTime = System.currentTimeMillis(); //Get the end Time
        logger.info("Процесс преобразования базы данных занял : "+ (endTime-startTime)/1000F +" секунд"); // Print the difference in seconds
    }

    private void clearEmptyFiders() {
        fiderList.removeIf(f-> f.getAbonents()==null);
        tpMap.forEach((s, tp) -> {
            tp.getFiders().removeIf(f-> f.getAbonents()==null);
        });
    }



    private static void printFields(DBFReader reader) {
        int numberOfFields = reader.getFieldCount();

        // use this count to fetch all field information
        // if required

        for (int i = 0; i < numberOfFields; i++) {

            DBFField field = reader.getField(i);
            System.out.println(i+":"+field.getName());
        }
        //System.out.println();
    }

    private void readAllTp(){
        List<Fider> newFiderList = new ArrayList<>();
        //printFields(tpReader);

        // Now, lets us start reading the rows

        DBFRow row;

        while ((row = tpReader.nextRow()) != null) {
            String cod = row.getString("COD");
            if(!cod.isEmpty())
                cod = cod.replaceAll("\\s+",""); //убирает пробелы из строки
            int dbfId = Integer.parseInt(cod);
            String resId = row.getString("RES");
            String name = row.getString("TP");
            Res res = new Res();
            res.setId(Integer.parseInt(resId));
            if(!name.equals("") && name!=null && !resId.equals("") && resId!=null) {
                Tp tp = new Tp(name, dbfId, null,false, res.getId());
                List<Fider> newFiderListForCurrentTp = new ArrayList<>();
                for(Fider fider :fiderList){
                    Fider newFider = new Fider();
                    newFider.setName(fider.getName());
                    newFider.setDbfId(fider.getDbfId());
                    newFider.setTp(tp);
                    newFiderList.add(newFider);
                    newFiderListForCurrentTp.add(newFider);
                }
                tp.setFiders(newFiderListForCurrentTp);
                tpMap.put(dbfId,tp);

            } else {
                logger.warn("ТП с панраметрами [COD:{} TP:{} RES:{}] - этот объект нельзя записать в базу данных.", dbfId, name, resId);
            }

        }

        fiderList.clear();
        newFiderList.stream().forEach(fider -> {fiderList.add(fider);});
    }


    private void readAllFider(){
        //printFields(fiderReader);

        // Now, lets us start reading the rows

        DBFRow row;

        while ((row = fiderReader.nextRow()) != null) {
            int dbfId =Integer.parseInt(row.getString("COD_FID"));
            String name = row.getString("НАИМЕНОВ");
            //System.out.printf("%s %s %s \n",dbf_id,name);
            fiderList.add(new Fider(name,dbfId,new Tp(),false));
        }
        fiderList.add(new Fider(Fider.EMPTY_FIDER_NAME,Fider.EMPTY_FIDER_ID,new Tp(),false));

    }

    private void readAllAbonent(){
        logger.info("Формирование списка Абонентов.");
        //printFields(abonentReader);

        // Now, lets us start reading the rows

        DBFRow row;

        while ((row = abonentReader.nextRow()) != null) {

            String accountNumber = row.getString("LIC_SCH");
            String surname = row.getString("FAM");
            String name = row.getString("NAME");
            String middlename = row.getString("OTCH");
            String homePhone = row.getString("TELEF");
            String firstPhone = row.getString("MTC");
            String secondPhone = row.getString("VEL");

            String opora = row.getString("OPORA");
            String codTp = row.getString("COD_TP");
            int tpCode=0;
            if(!codTp.isEmpty()) {
                codTp = codTp.replaceAll("\\s+","");//убирает пробелы из строки
                tpCode =Integer.parseInt(codTp);
            } else {
                logger.warn("Абонент не привязан к ТП(или имеет пустое значение в поле COD_TP: {} {} {} {} {} {} {} {} {}"
                        ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,opora);
                continue;
            }
            String codFid = row.getString("COD_FID");
            int fiderCode =0;
            if(!codFid.isEmpty()) {
                 codFid = codFid.replaceAll("\\s+",""); //убирает пробелы из строки
                 fiderCode = Integer.parseInt(codFid);
            } else {
                logger.warn("Абонент не привязан к фидеру(или имеет пустое значение в поле COD_FID) и будет привязан к фидеру \"Без названия\": {} {} {} {} {} {} {} {} {} {}"
                        ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,codFid,opora);
                fiderCode=Fider.EMPTY_FIDER_ID;
                //continue;
            }
            //System.out.println(fiderCode);
//            System.out.printf("%s %s %s %s %s %s %s %s %s %s \n"
//                    ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,fiderCode,opora);
            Tp tp = tpMap.get(tpCode);
            if(tp!=null){
                int finalFiderCode = fiderCode;
                Optional<Fider> optFider = tp.getFiders()
                        .stream()
                        .filter(f -> f.getDbfId()== finalFiderCode)
                        .findFirst();
                if (optFider.isPresent()) {
                    Fider fider = optFider.get();
                    Abonent abonent = new Abonent(Long.parseLong(accountNumber), surname, name, middlename, homePhone, firstPhone, secondPhone, opora, fider, false);
                    fider.addAbonent(abonent);
                    abonentList.add(abonent);
                } else {
                    //todo если фидер пуст.
                    Optional<Fider> emptyFider = tp.getFiders().stream().filter(f -> false).findFirst();
                    if (emptyFider.isPresent()) {
                        Fider fider = emptyFider.get();
                        Abonent abonent = new Abonent(Long.parseLong(accountNumber), surname, name, middlename, homePhone, firstPhone, secondPhone, opora, fider, false);
                        fider.addAbonent(abonent);
                        abonentList.add(abonent);
                    } else {
                        logger.warn(fiderCode + "  " + accountNumber + " " + tp.getName() + " " + " не добавлен ");
                    }
                }
            } else {
                logger.warn("Для Абонента: {} {} {} {} {} {} {} {} {} {} нарушена связь или ТП не валидно(имеет пустые поля)"
                        ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,fiderCode,opora);
            }
        }
        logger.info("Формирование списка ТП закончено.");

    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Fider> getFiderList() {
        return fiderList;
    }

    public Map<Integer, Tp> getTpMap() {
        return tpMap;
    }

    public List<Abonent> getAbonentList() {
        return abonentList;
    }
}
