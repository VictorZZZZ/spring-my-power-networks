package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.services.*;
import net.energo.grodno.pes.sms.entities.Lead;
import net.energo.grodno.pes.sms.entities.Master;
import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.entities.users.User;
import net.energo.grodno.pes.sms.security.AuthDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private static Logger logger = LoggerFactory.getLogger(ResService.class);
    private ResService resService;
    private TpService tpService;
    private LeadService leadService;
    private MasterService masterService;
    private UserService userService;

    @Autowired
    public MainController(ResService resService, TpService tpService, LeadService leadService, MasterService masterService, UserService userService) {
        this.resService = resService;
        this.tpService = tpService;
        this.leadService = leadService;
        this.masterService = masterService;
        this.userService = userService;
    }

    @GetMapping(value = {"", "/", "/index"})
    public String homePage(Model model, HttpSession session, Principal principal) {
        List<Res> allRes = resService.getAllRes();
        if (principal != null) {
            long notLinkedTps;
            try {
                notLinkedTps = tpService.countNotLinkedTpsByUser(principal.getName());
                if (notLinkedTps > 0) session.setAttribute("notLinkedTpsCount", notLinkedTps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("allRes", allRes);
        return "index";
    }

    @GetMapping(value = {"/leadersList"})
    public String showLeaders(Model model) {
        List<Lead> leaders = leadService.getAll();
        model.addAttribute("leaders", leaders);
        return "leaders/leadersList";
    }

    @GetMapping(value = {"/mastersList"})
    public String showMasters(Model model, RedirectAttributes resdirectAttributes, HttpServletRequest request,
                              Principal principal,
                              Authentication authentication) {
        try {

            List<Master> masters = new ArrayList<>();
            if (AuthDetails.listRoles(authentication).contains("ROLE_ADMIN")) {
                //Если Admin, то показывать заказы всех пользователей
                masters = masterService.getAll();
            } else {
                User user = userService.findByUsername(principal.getName());
                Res res = user.getRes();
                masters = masterService.getAllByRes(res);
            }
            model.addAttribute("masters", masters);
            //оставляем leaders/leadersList потому что он такой же
            return "masters/mastersList";
        } catch (Exception e) {
            e.printStackTrace();
            resdirectAttributes.addFlashAttribute("messageError", "");
            return "redirect:/";
        }

    }

    @GetMapping(value = {"/importPage"})
    public String importPage(Model model) {
        return "importPage";
    }

    @GetMapping(value = {"/countAbonents/"})
    public String countAbonentsThread(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        //Запускаем пересчёт в новом потоке, чтобы не мешало работать
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Запущен процесс пересчета пользователей");
                long startTime = System.currentTimeMillis();
                resService.countAbonents(1); //22 секунды 11000 абонентов
                resService.countAbonents(2); // 128 секунд 28000 абонентов
                resService.countAbonents(3);//133 секунды 28000 абонентов
                resService.countAbonents(4); // 199 секунд 147000 абонентов
                long endTime = System.currentTimeMillis();
                logger.info("Пересчёт пользователей закончен. Время выполнения {} секунд", (endTime - startTime) / 1000F);
                redirectAttributes.addFlashAttribute("Пересчет абонентов закончен");
            }
        });
        t.start();


        redirectAttributes.addFlashAttribute("messageInfo", "Запущен процесс пересчёта пользователей");
        return "redirect:" + request.getHeader("Referer");
    }


}
