package net.energo.grodno.pes.smsSender.Template;


import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.sms.services.TemplateService;
import net.energo.grodno.pes.sms.services.UserService;
import net.energo.grodno.pes.sms.entities.TextTemplate;
import net.energo.grodno.pes.sms.entities.users.User;
import net.energo.grodno.pes.sms.repositories.TemplateRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;


public class TemplateTest extends AbstractClass {
    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private UserService userService;

    @Test
    @Ignore
    public void insertRecord(){
        Optional<User> optUser = userService.findById(4L);
        if(optUser.isPresent()){
            User user = optUser.get();
            TextTemplate template = new TextTemplate();
            template.setUser(user);
            template.setTemplate("My Tamplate");
            TextTemplate newTemplate = templateService.saveOne(template);
            System.out.println(newTemplate);
            if(newTemplate.getId()!=null) {
                //templateRepository.delete(newTemplate);
                assert(true);
            } else {
                assert(false);
            }
        } else {
            assert(false);
        }

    }

    @Test
    @Ignore
    public void getTemplates(){
        try {
            List<TextTemplate> templates = templateService.getAllByUser("user");
            System.out.println(templates.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
