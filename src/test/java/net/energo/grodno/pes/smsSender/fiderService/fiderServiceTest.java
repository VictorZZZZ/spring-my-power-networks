package net.energo.grodno.pes.smsSender.fiderService;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.sms.Services.FiderService;
import net.energo.grodno.pes.sms.entities.Fider;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class fiderServiceTest extends AbstractClass {
    @Autowired
    private FiderService fiderService;

    @Test
    @Ignore
    public void testFiderService(){
        List<Fider> fiderList =  fiderService.findAllByResId(3);
        System.out.println(fiderList);
    }
}
