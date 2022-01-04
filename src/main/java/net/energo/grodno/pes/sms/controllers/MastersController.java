package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.entities.Master;
import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.services.MasterService;
import net.energo.grodno.pes.sms.services.UserService;
import net.energo.grodno.pes.sms.entities.users.User;
import net.energo.grodno.pes.sms.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/masters")
public class MastersController {
    private MasterService masterService;
    private ShoppingCart cart;
    private UserService userService;

    @Autowired
    public MastersController(MasterService masterService, ShoppingCart cart, UserService userService) {
        this.masterService = masterService;
        this.cart = cart;
        this.userService = userService;
    }

    @GetMapping(value = {"/add"})
    public String viewAddForm(Model model,Principal principal,RedirectAttributes redirectAttributes){
        try {
            User user = userService.findByUsername(principal.getName());
            Res res = user.getRes();
            Master master = new Master();
            master.setRes(res);
            model.addAttribute("master",master);
            return "masters/edit";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError",e.getMessage());
            return "redirect:/mastersList";
        }
    }

    @GetMapping(value = {"/edit/{id}"})
    public String viewEditForm(@PathVariable("id") Long accountNumber, Model model, Principal principal,RedirectAttributes redirectAttributes){
        try {
            User user = userService.findByUsername(principal.getName());
            Master master = masterService.getOne(accountNumber);
            model.addAttribute("master",master);
            return "masters/edit";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError",e.getMessage());
            return "redirect:/mastersList";
        }

    }

    @PostMapping(value = "/save")
    public String saveMaster(@Valid Master master, BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "masters/edit";
        } else {
            masterService.saveOne(master);
            redirectAttributes.addFlashAttribute("messageInfo","Работник добавлен");
            return "redirect:/mastersList";
        }
    }

    @PostMapping("/delete")
    public String deleteMasterById(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){
        masterService.deleteOne(id);
        redirectAttributes.addFlashAttribute("messageInfo","Абонент удален.");
        return "redirect:/mastersList";
    }

    @GetMapping(value = {"/broadcast"})
    public String broadcast(RedirectAttributes redirectAttributes){
        int count=0;
        List<Master> masterList = masterService.getAll();
        for (Master master:masterList) {
            count+=cart.addAbonent(master.getAccountNumber());
        }
        redirectAttributes.addFlashAttribute("messageInfo","В список рассылки добавлено " + count + " абонентов");
        return "redirect:/cart";
    }


}
