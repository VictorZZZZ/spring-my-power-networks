package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsAPI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/sms")
public class SmsController {

    @RequestMapping(value = {"/checkBalance"}, method = RequestMethod.GET)
    public String checkBalance(HttpServletRequest request,RedirectAttributes redirectAttributes){
        try {
            String balance = SmsAPI.checkBalance();
            redirectAttributes.addFlashAttribute("messageInfo","Текущий баланс: "+balance+"руб.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Ошибка связи с сервером (IOException | InterruptedException) проверьте интернет соединение и попробуйте ещё раз.");
        }

        return "redirect:"+request.getHeader("Referer");
    }
}
