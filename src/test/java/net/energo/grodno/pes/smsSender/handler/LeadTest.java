package net.energo.grodno.pes.smsSender.handler;


import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.LeadService;

import net.energo.grodno.pes.smsSender.entities.Lead;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LeadTest extends AbstractClass {
    @Autowired
    private LeadService leadService;

    @Test
    @Ignore
    public void getLeads(){
        List<Lead> handlers = leadService.getAll();
        System.out.println(handlers);
    }
}
