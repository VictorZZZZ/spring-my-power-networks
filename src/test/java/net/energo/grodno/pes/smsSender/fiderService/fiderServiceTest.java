package net.energo.grodno.pes.smsSender.fiderService;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.entities.Fider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class fiderServiceTest extends AbstractClass {
    @Autowired
    private FiderService fiderService;

    @Test
    public void testFiderService(){
        List<Fider> fiderList =  fiderService.findAllByResId(3);
        System.out.println(fiderList);
    }
}
