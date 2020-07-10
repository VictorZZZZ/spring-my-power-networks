package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/res")
public class ResController {
    private ResService resService;
    private TpService tpService;

    public ResController(TpService tpService) {
        this.tpService = tpService;
    }

    @Autowired
    public void setResService(ResService resService) {
        this.resService = resService;
    }


    @GetMapping(value={"", "/", "/index"})
    public String showAll(Model model){
        List<Res> allRes=resService.getAllRes();
        model.addAttribute("allRes",allRes);
        return "res/index";
    }

    @GetMapping("/edit/{id}")
    public String editRes(Model model, @PathVariable("id") Integer id){
        Res res = resService.getOne(id);
        model.addAttribute("res",res);
        return "res/edit";
    }

    @GetMapping("/view/{id}")
    public String viewRes(Model model, @PathVariable("id") Integer id){
        Res res = resService.getOne(id);
        List<Tp> tps = tpService.getAllByRes(res);
        model.addAttribute("res",res);
        model.addAttribute("tps",tps);
        return "res/view";
    }

    @GetMapping("/add")
    public String addRes(Model model){
        Res res = new Res();
        model.addAttribute("res",res);
        return "res/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteResById(@PathVariable("id") Integer id){
        resService.deleteOne(id);
        return "redirect:/res";
    }


    @RequestMapping(value = "/saveRes", method = RequestMethod.POST)
    public String saveRes(@Valid Res res, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "res/edit";
        } else {
            resService.saveOne(res);
            return "redirect:/res/index";
        }
    }
}
