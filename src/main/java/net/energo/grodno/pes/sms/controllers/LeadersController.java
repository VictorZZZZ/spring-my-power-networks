package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.entities.Lead;
import net.energo.grodno.pes.sms.services.LeadService;
import net.energo.grodno.pes.sms.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/leaders")
public class LeadersController {
    private LeadService leadService;
    private ShoppingCart cart;

    @Autowired
    public LeadersController(LeadService leadService, ShoppingCart cart) {
        this.leadService = leadService;
        this.cart = cart;
    }

    @GetMapping(value = {"/add"})
    public String viewAddForm(Model model){
        Lead lead = new Lead();
        model.addAttribute("lead",lead);
        return "leaders/edit";
    }

    @GetMapping(value = {"/edit/{id}"})
    public String viewEditForm(@PathVariable("id") Long accountNumber, Model model){
        Lead lead = leadService.getOne(accountNumber);
        model.addAttribute("lead",lead);
        return "leaders/edit";
    }

    @PostMapping(value = "/save")
    public String saveLead(@Valid Lead lead, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "leaders/edit";
        } else {
            leadService.saveOne(lead);
            return "redirect:/leadersList";
        }
    }

    @PostMapping("/delete")
    public String deleteLeadById(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){
        leadService.deleteOne(id);
        redirectAttributes.addFlashAttribute("messageInfo","Абонент удален.");
        return "redirect:/leadersList";
    }

    @GetMapping(value = {"/broadcast"})
    public String broadcast(RedirectAttributes redirectAttributes){
        int count=0;
        List<Lead> leadList = leadService.getAll();
        for (Lead lead:leadList) {
            count+=cart.addAbonent(lead.getAccountNumber());
        }
        redirectAttributes.addFlashAttribute("messageInfo","В список рассылки добавлено " + count + " абонентов");
        return "redirect:/cart";
    }


}
