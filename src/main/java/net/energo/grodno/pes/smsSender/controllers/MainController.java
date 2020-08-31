package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class MainController {
    private ResService resService;
    private TpService tpService;

    @Autowired
    public MainController(ResService resService, TpService tpService) {
        this.resService = resService;
        this.tpService = tpService;
    }

    @GetMapping(value={"", "/", "/index"})
    public String homePage(Model model, HttpSession session, Principal principal){
        List<Res> allRes=resService.getAllRes();
        if(principal != null) {
            List<Tp> notLinkedTps = null;
            try {
                notLinkedTps = tpService.getNotLinkedTpsByUser(principal.getName());
                if(notLinkedTps.size()>0) session.setAttribute("notLinkedTpsCount",notLinkedTps.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("allRes",allRes);
        return "index";
    }

    @GetMapping(value={"/importPage"})
    public String importPage(Model model){
        return "importPage";
    }

}
