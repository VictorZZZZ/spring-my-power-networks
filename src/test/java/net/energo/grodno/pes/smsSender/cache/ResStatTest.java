package net.energo.grodno.pes.smsSender.cache;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.ResService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ResStatTest extends AbstractClass {
    @Autowired
    private ResService resService;

    @Test
    @Ignore
    public void getInfo(){
        //проверка времени кеширования результатов res.getInfo()
        System.out.println("Первый раз:");
        long startTime=System.currentTimeMillis();
        resService.getResStat(2);
        long endTime = System.currentTimeMillis();
        System.out.printf("Время выполнения %f секунд\n",(endTime-startTime)/1000F);


        System.out.println("Второй раз:");
        startTime=System.currentTimeMillis();
        resService.getResStat(2);
        endTime = System.currentTimeMillis();
        System.out.printf("Время выполнения %f секунд\n",(endTime-startTime)/1000F);
    }

    @Test
    public void countStat(){
        System.out.println("Старт:");
        long startTime=System.currentTimeMillis();
        resService.countAbonents(1); //22 секунды 11000 абонентов
        //resService.countAbonents(2); // 128 секунд 28000 абонентов
        //resService.countAbonents(3);//133 секунды 28000 абонентов
        //resService.countAbonents(4); // 199 секунд 147000 абонентов
        long endTime = System.currentTimeMillis();
        System.out.printf("Время выполнения %f секунд\n",(endTime-startTime)/1000F);
    }

}
