package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/tp")
public class TpController {
    private TpService tpService;
    private ResService resService;

    @Autowired
    public TpController(TpService tpService, ResService resService) {
        this.tpService = tpService;
        this.resService = resService;
    }

    @GetMapping(value={"","/","index"})
    public String showAll(Model model){
        List<Tp> tps = tpService.getAll();
        model.addAttribute("tps",tps);
        return "tp/index";
    }

    @GetMapping(value={"/add"})
    public String addTp(Model model){
        Tp tp = new Tp();
        List<Res> resList= resService.getAllRes();
        model.addAttribute("tp",tp);
        model.addAttribute("resList",resList);
        return "tp/edit";
    }

    @GetMapping("/edit/{id}")
    public String editTp(Model model, @PathVariable("id") Integer id){
        Tp tp = tpService.getOne(id);
        List<Res> resList= resService.getAllRes();
        model.addAttribute("tp",tp);
        model.addAttribute("resList",resList);
        return "tp/edit";
    }

    @RequestMapping(value = "/saveTp", method = RequestMethod.POST)
    public String saveTp(@Valid Tp tp, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult.getAllErrors());
            model.addAttribute("resList",resService.getAllRes());
            return "tp/edit";
        } else {
            tpService.saveOne(tp);
            return "redirect:/tp/index";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteResById(@PathVariable("id") Integer id){
        tpService.deleteOne(id);
        return "redirect:/tp";
    }


}
