package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.AbonentService;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.repositories.AbonentRepository;
import net.energo.grodno.pes.smsSender.repositories.FiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/abonent")
public class AbonentController {
    private AbonentService abonentService;
    private FiderService fiderService;

    @Autowired
    public AbonentController(AbonentService abonentService, FiderService fiderService) {
        this.abonentService = abonentService;
        this.fiderService = fiderService;
    }

    @GetMapping(value={"","/","index"})
    public String showAll(Model model){
        List<Abonent> abonents = abonentService.getAll();
        model.addAttribute("abonents",abonents);
        return "abonent/index";
    }

    @GetMapping(value={"/add"})
    public String addAbonent(Model model){
        Abonent abonent = new Abonent();
        List<Fider> fiderList= fiderService.getAll();
        model.addAttribute("abonent",abonent);
        model.addAttribute("fiderList",fiderList);
        return "abonent/edit";
    }

    @GetMapping("/edit/{id}")
    public String editAbonent(Model model, @PathVariable("id") Integer id){
        Abonent abonent = abonentService.getOne(id);
        List<Fider> fiderList= fiderService.getAll();
        model.addAttribute("abonent",abonent);
        model.addAttribute("fiderList",fiderList);
        return "abonent/edit";
    }

    @RequestMapping(value = "/saveAbonent", method = RequestMethod.POST)
    public String saveAbonent(@Valid Abonent abonent, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("fiderList",fiderService.getAll());
            return "abonent/edit";
        } else {
            abonentService.saveOne(abonent);
            return "redirect:/abonent/index";
        }
    }
    //todo сделать через POST + csrf
    @GetMapping("/delete/{id}")
    public String deleteAbonentById(@PathVariable("id") Integer id){
        abonentService.deleteOne(id);
        return "redirect:/abonent";
    }
}
