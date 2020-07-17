package net.energo.grodno.pes.smsSender.Template;


import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.Services.TemplateService;
import net.energo.grodno.pes.smsSender.Services.UserService;
import net.energo.grodno.pes.smsSender.entities.TextTemplate;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.TemplateRepository;
import org.hibernate.sql.Template;
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
    public void getTemplates(){
        try {
            List<TextTemplate> templates = templateService.getAllByUser("user");
            System.out.println(templates.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
