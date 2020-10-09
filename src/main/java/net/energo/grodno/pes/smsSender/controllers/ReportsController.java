package net.energo.grodno.pes.smsSender.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    @Value("${sms.price}")
    private String smsPrice;

    @RequestMapping(value = {"", "/", "index"}, method = RequestMethod.GET)
    public String index(Model model){
        model.addAttribute("smsPrice",smsPrice);
        return "reports/index";
    }
}
