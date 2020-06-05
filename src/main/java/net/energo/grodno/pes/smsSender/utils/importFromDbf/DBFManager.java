package net.energo.grodno.pes.smsSender.utils.importFromDbf;

import com.linuxense.javadbf.*;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DBFManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class.getName());

    private static DBFReader tpReader;
    private static DBFReader fiderReader;
    private static DBFReader abonentReader;
    private List<Fider> fiderList = new ArrayList<>();
    private Map<String,Tp> tpMap = new HashMap<>();
    private List<Abonent> abonentList= new ArrayList<>();

    public DBFManager(String tpFilename,String fiderFilename, String abonentFilename) {
        try {
            // create a DBFReader object
            //reader = new DBFReader(new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/static/files/ГГРЭС/GGRES.DBF"));
            tpReader = new DBFReader(new FileInputStream(tpFilename));
            fiderReader = new DBFReader(new FileInputStream(fiderFilename));
            abonentReader = new DBFReader(new FileInputStream(abonentFilename));


        } catch (DBFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        DBFManager dbfManager = new DBFManager(
                System.getProperty("user.dir")+"\\src\\main\\resources\\static\\files\\ГГРЭС\\4KARTTP.DBF",
                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\ГГРЭС\\4KARTFID.DBF",
                System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\ГГРЭС\\GGRES.DBF");

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
            //System.out.println(field.getName());
        }
        //System.out.println();
    }

    private void readAllTp(){
        List<Fider> newFiderList = new ArrayList<>();
        printFields(tpReader);

        // Now, lets us start reading the rows

        DBFRow row;

        while ((row = tpReader.nextRow()) != null) {
            String dbf_id = row.getString("COD");
            String res_id = row.getString("RES");
            String name = row.getString("TP");
            //System.out.printf("%s %s %s \n",dbf_id,res_id,name);
            Res res = new Res();
            res.setId(Integer.parseInt(res_id));
            Tp tp = new Tp(name,dbf_id,res);
            List<Fider> newFiderListForCurrentTp = new ArrayList<>();
            for(Fider fider :fiderList){
                Fider newFider = new Fider();
                newFider.setName(fider.getName());
                newFider.setDbf_id(fider.getDbf_id());
                newFider.setTp(tp);
                newFiderList.add(newFider);
                newFiderListForCurrentTp.add(newFider);
            }
            tp.setFiders(newFiderListForCurrentTp);
            tpMap.put(dbf_id,tp);
        }

        fiderList.clear();
        newFiderList.stream().forEach(fider -> {fiderList.add(fider);});
    }


    private void readAllFider(){
        printFields(fiderReader);

        // Now, lets us start reading the rows

        DBFRow row;

        while ((row = fiderReader.nextRow()) != null) {
            String dbf_id = row.getString("COD_FID");
            String name = row.getString("НАИМЕНОВ");
            //System.out.printf("%s %s %s \n",dbf_id,name);
            fiderList.add(new Fider(name,dbf_id,new Tp()));
        }
        fiderList.add(new Fider("Без номера","",new Tp()));

    }

    private void readAllAbonent(){
        printFields(abonentReader);

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
            String tpCode = row.getString("COD_TP");
            String fiderCode = row.getString("COD_FID");
            String opora = row.getString("OPORA");
            //System.out.println(fiderCode);
//            System.out.printf("%s %s %s %s %s %s %s %s %s %s \n"
//                    ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,fiderCode,opora);
            if((!tpCode.equals(""))) {
                Tp tp = tpMap.get(tpCode);
                Optional<Fider> optFider = tp.getFiders()
                        .stream()
                        .filter(f -> f.getDbf_id().equals(fiderCode))
                        .findFirst();
                if(optFider.isPresent()){
                    Fider fider=optFider.get();
                    Abonent abonent = new Abonent(accountNumber, surname, name, middlename, homePhone, firstPhone, secondPhone, opora, fider);
                    fider.addAbonent(abonent);
                    abonentList.add(abonent);
                } else {
                    //todo если фидер пуст.
                    Optional<Fider> emptyFider = tp.getFiders().stream().filter(f-> f.getDbf_id().equals("")).findFirst();
                    if(emptyFider.isPresent()){
                        Fider fider=emptyFider.get();
                        Abonent abonent = new Abonent(accountNumber, surname, name, middlename, homePhone, firstPhone, secondPhone, opora, fider);
                        fider.addAbonent(abonent);
                        abonentList.add(abonent);
                    } else {
                        logger.warn(fiderCode + "  " +accountNumber + " " + tp.getName() + " " + " не добавлен ");
                    }
                }
            } else {
                logger.warn("В файле .dbf найден Абонент с пустым полем COD_TP: \n {} {} {} {} {} {} {} {} {} {}"
                        ,accountNumber,surname,name,middlename,homePhone,firstPhone,secondPhone,tpCode,fiderCode,opora);
            }

        }

    }


}
