package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.services.ResService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/admin")
public class AdminController {
    private ResService resService;

    public AdminController(ResService resService) {
        this.resService = resService;
    }

    @RequestMapping(value = {"","/","/index"}, method = RequestMethod.GET)
    public String dashboard(){
        return "admin/dashboard";
    }

    @RequestMapping(value = {"/countAbonents"}, method = RequestMethod.GET)
    public String countAbonents(){

        return "admin/countAbonents";
    }

}
