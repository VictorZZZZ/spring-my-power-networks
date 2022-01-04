package net.energo.grodno.pes.sms.Services;

import net.energo.grodno.pes.sms.entities.TextTemplate;
import net.energo.grodno.pes.sms.repositories.TemplateRepository;
import net.energo.grodno.pes.sms.entities.users.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {
    private TemplateRepository templateRepository;
    private UserService userService;

    public TemplateService(TemplateRepository templateRepository, UserService userService) {
        this.templateRepository = templateRepository;
        this.userService = userService;
    }

    public List<TextTemplate> getAllByUser(String userName) throws Exception {
        User user =  userService.findByUsername(userName);
        System.out.println(user);
        return templateRepository.findByUser(user);
    }

    public TextTemplate saveOne(TextTemplate template) {
        return templateRepository.save(template);
    }

    public TextTemplate getOne(Long id) {
        return templateRepository.getOne(id);
    }

    public void deleteOne(Long id) {
        templateRepository.deleteById(id);
    }
}
