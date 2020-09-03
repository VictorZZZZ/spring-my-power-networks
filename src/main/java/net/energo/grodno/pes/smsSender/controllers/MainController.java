package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.LeadService;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Lead;
import net.energo.grodno.pes.smsSender.entities.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class MainController {
    private ResService resService;
    private TpService tpService;
    private LeadService leadService;

    @Autowired
    public MainController(ResService resService, TpService tpService, LeadService leadService) {
        this.resService = resService;
        this.tpService = tpService;
        this.leadService = leadService;
    }

    @GetMapping(value={"", "/", "/index"})
    public String homePage(Model model, HttpSession session, Principal principal){
        List<Res> allRes=resService.getAllRes();
        if(principal != null) {
            long notLinkedTps;
            try {
                notLinkedTps = tpService.countNotLinkedTpsByUser(principal.getName());
                if(notLinkedTps>0) session.setAttribute("notLinkedTpsCount",notLinkedTps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("allRes",allRes);
        return "index";
    }

    @GetMapping(value={"/leadersList"})
    public String showLeaders(Model model, RedirectAttributes resdirectAttributes, HttpServletRequest request){
        List<Lead> leaders = leadService.getAll();
        model.addAttribute("leaders",leaders);
        return "main/leadersList";
    }

    @GetMapping(value={"/importPage"})
    public String importPage(Model model){
        return "importPage";
    }

}
