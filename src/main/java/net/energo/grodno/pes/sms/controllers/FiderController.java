package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.Services.TpService;
import net.energo.grodno.pes.sms.Services.FiderService;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
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
@RequestMapping("/fider")
public class FiderController{
    private FiderService fiderService;
    private TpService tpService;

    @Autowired
    public FiderController(FiderService fiderService, TpService tpService) {
        this.fiderService = fiderService;
        this.tpService = tpService;
    }

//    @GetMapping(value={"","/","index"})
//    public String showAll(Model model){
//        List<Fider> fiders = fiderService.getAll();
//        model.addAttribute("fiders",fiders);
//        return "fider/index";
//    }

    @GetMapping(value={"/add"})
    public String addFider(Model model){
        Fider fider = new Fider();
        List<Tp> tpList= tpService.getAll();
        model.addAttribute("fider",fider);
        model.addAttribute("tpList",tpList);
        return "fider/edit";
    }

    @GetMapping("/edit/{id}")
    public String editFider(Model model, @PathVariable("id") Long id){
        Fider fider = fiderService.getOne(id);
        List<Tp> tpList= tpService.getAll();
        model.addAttribute("fider",fider);
        model.addAttribute("tpList",tpList);
        return "fider/edit";
    }

    @GetMapping("/view/{id}")
    public String viewFider(Model model, @PathVariable("id") Long id){
        Fider fider = fiderService.getOne(id);
        model.addAttribute("fider",fider);
        return "fider/view";
    }

    @RequestMapping(value = "/saveFider", method = RequestMethod.POST)
    public String saveFider(@Valid Fider fider, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("tpList",tpService.getAll());
            return "fider/edit";
        } else {
            fiderService.saveOne(fider);
            return "redirect:/fider/index";
        }
    }
//todo сделать через POST + csrf
    @GetMapping("/delete/{id}")
    public String deleteFiderById(@PathVariable("id") Long id){
        fiderService.deleteOne(id);
        return "redirect:/fider";
    }
}
