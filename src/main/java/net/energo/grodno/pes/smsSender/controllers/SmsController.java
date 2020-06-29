package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.entities.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/sms")
public class SmsController {

    @RequestMapping(value = {"/checkBalance"}, method = RequestMethod.GET)
    public String checkBalance(HttpServletRequest request,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("messageInfo","Баланс 0руб.");
        return "redirect:"+request.getHeader("Referer");
    }
}
