package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tp")
public class TpController {
    private TpService tpService;

    @Autowired
    public void setTpService(TpService tpService) {
        this.tpService = tpService;
    }

    @GetMapping(value={"","/","index"})
    public String showAll(Model model){
        List<Tp> tps = tpService.getAll();
        model.addAttribute("tps",tps);
        return "tp/index";
    }


}
