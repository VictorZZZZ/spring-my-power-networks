package net.energo.grodno.pes.smsSender.helpScripts;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.utils.importFromJSON.JsonManager;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Делает импорт структуры из старой базы данных
 * Чтобы с нуля залить РЭС:
 * 1. Удалить Аьонентов, Фидера, ТП
 * 2. Провести импорт с помощью GUI
 * 3. Сделать ImportJsonTest
 * 4. Сделать RemoveEmptyTp
 */
public class importJsonTest extends AbstractClass {
    @Autowired
    ResService resService;
    JsonManager jsonManager;

    @Test
    public void deepSave(){
        try {
            List<Res> resList = jsonManager.readJson();
            resService.deepSave(resList);
        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }
}
