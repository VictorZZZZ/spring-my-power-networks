package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.repositories.ResRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    private ResService resService;

    @Autowired
    public void setResService(ResService resService) {
        this.resService = resService;
    }

    @GetMapping(value={"", "/", "/index"})
    public String homePage(Model model){
        List<Res> allRes=resService.getAllRes();
        model.addAttribute("allRes",allRes);
        return "index";
    }

}
