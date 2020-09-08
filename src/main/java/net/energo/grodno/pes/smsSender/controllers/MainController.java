package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.LeadService;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Lead;
import net.energo.grodno.pes.smsSender.entities.Res;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(ResService.class);
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
        return "leaders/leadersList";
    }

    @GetMapping(value={"/importPage"})
    public String importPage(Model model){
        return "importPage";
    }

    @GetMapping(value={"/countAbonents/"})
    public String countAbonentsThread(RedirectAttributes redirectAttributes,HttpServletRequest request){
        //Запускаем пересчёт в новом потоке, чтобы не мешало работать
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Запущен процесс пересчета пользователей");
                long startTime=System.currentTimeMillis();
                resService.countAbonents(1); //22 секунды 11000 абонентов
                resService.countAbonents(2); // 128 секунд 28000 абонентов
                resService.countAbonents(3);//133 секунды 28000 абонентов
                resService.countAbonents(4); // 199 секунд 147000 абонентов
                long endTime = System.currentTimeMillis();
                logger.info("Пересчёт пользователей закончен. Время выполнения {} секунд",(endTime-startTime)/1000F);
                redirectAttributes.addFlashAttribute("Пересчет абонентов закончен");
            }
        });
        t.start();


        redirectAttributes.addFlashAttribute("messageInfo","Запущен процесс пересчёта пользователей");
        return "redirect:"+request.getHeader("Referer");
    }


}
