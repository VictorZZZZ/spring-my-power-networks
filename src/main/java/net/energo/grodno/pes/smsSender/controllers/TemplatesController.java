package net.energo.grodno.pes.smsSender.controllers;


import net.energo.grodno.pes.smsSender.Services.TemplateService;
import net.energo.grodno.pes.smsSender.Services.UserService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.TextTemplate;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.entities.users.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/templates")
public class TemplatesController {
    private TemplateService templateService;
    private UserService userService;

    public TemplatesController(TemplateService templateService, UserService userService) {
        this.templateService = templateService;
        this.userService = userService;
    }


    @GetMapping(value={"", "/", "/index"})
    public String showAll(Model model, Principal principal, RedirectAttributes redirectAttributes){
        try {
            List<TextTemplate> templates=templateService.getAllByUser(principal.getName());
            model.addAttribute("templates",templates);
            return "textTemplates/index";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Ошибка: Пользователя не существет, или таблица шаблонов пуста. ");
            return "redirect:/";
        }
    }

    @GetMapping("/add")
    public String addRes(Model model,Principal principal,RedirectAttributes redirectAttributes){
        try {
            User user = userService.findByUsername(principal.getName());
            TextTemplate template = new TextTemplate();
            template.setUser(user);
            model.addAttribute("template",template);
            return "textTemplates/edit";
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Ошибка: Пользователя не существет");
            return "redirect:/";
        }
    }
    @RequestMapping(value = "/saveTemplate", method = RequestMethod.POST)
    public String saveTemplate(@Valid TextTemplate template, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "textTemplates/edit";
        } else {

            templateService.saveOne(template);
            return "redirect:/templates";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTemplate(Model model, @PathVariable("id") Long id){
        TextTemplate template = templateService.getOne(id);
        model.addAttribute("template",template);
        return "textTemplates/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteTemplateById(@PathVariable("id") Long id,RedirectAttributes redirectAttributes,Principal principal){
        TextTemplate template = templateService.getOne(id);
        if(template.getUser().getUsername().equals(principal.getName())){
            templateService.deleteOne(id);
            redirectAttributes.addFlashAttribute("messageInfo","Шаблон удален");
        } else {
            redirectAttributes.addFlashAttribute("messageError","Вы не создавали, этот шаблон, поэтому не можете его удалить.");
        }
        return "redirect:/templates";
    }


//    @GetMapping("/view/{id}")
//    public String viewRes(Model model, @PathVariable("id") Integer id){
//        Res res = resService.getOne(id);
//        List<Tp> tps = tpService.getAllByRes(res);
//        model.addAttribute("res",res);
//        model.addAttribute("tps",tps);
//        return "res/view";
//    }
//

//

//


}
