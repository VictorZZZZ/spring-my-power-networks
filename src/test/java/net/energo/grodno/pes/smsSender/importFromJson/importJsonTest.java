package net.energo.grodno.pes.smsSender.importFromJson;

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

public class importJsonTest extends AbstractClass {
    @Autowired
    ResService resService;
    JsonManager jsonManager;

    @Test
    @Ignore
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
