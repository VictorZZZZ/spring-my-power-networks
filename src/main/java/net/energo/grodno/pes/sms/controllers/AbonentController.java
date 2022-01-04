package net.energo.grodno.pes.sms.controllers;

import lombok.RequiredArgsConstructor;
import net.energo.grodno.pes.sms.services.AbonentService;
import net.energo.grodno.pes.sms.services.FiderService;
import net.energo.grodno.pes.sms.services.TpService;
import net.energo.grodno.pes.sms.entities.Abonent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/abonent")
@RequiredArgsConstructor
public class AbonentController {
    private final AbonentService abonentService;
    private final TpService tpService;
    private final FiderService fiderService;

    //@GetMapping(value={"","/","index"})
    public String showAll(Model model) {
        List<Abonent> abonents = abonentService.getAll();
        model.addAttribute("abonents", abonents);
        return "abonent/index";
    }

    @RequestMapping(value = {"", "/", "index", "/listAbonents"}, method = RequestMethod.GET)
    public String listAbonents(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(50);

        Page<Abonent> abonentPage = abonentService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("abonentsPaginated", abonentPage);

        int totalPages = abonentPage.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        long totalAbonents = abonentService.getCount();
        model.addAttribute("totalAbonents", totalAbonents);

        return "abonent/index_paginated";
    }

    @GetMapping(value = {"/add"})
    public String addAbonent(Model model) {
        var abonent = new Abonent();
        var tpList = tpService.getAll();
        model.addAttribute("abonent", abonent);
        model.addAttribute("tpList", tpList);
        model.addAttribute("fiderList", new ArrayList<>());
        return "abonent/edit";
    }

    @GetMapping("/edit/{id}")
    public String editAbonent(Model model, @PathVariable("id") Long id) {
        Abonent abonent = abonentService.getOne(id);
        var tpList = tpService.getAll();
        var fider = abonent.getFider();
        var tp = fider.getTp();
        var fiderList = tp.getFiders();
        model.addAttribute("abonent", abonent);
        model.addAttribute("tpList", tpList);
        model.addAttribute("fiderList", fiderList);
        model.addAttribute("tp", tp);
        model.addAttribute("fider", fider);
        return "abonent/edit";
    }

    @GetMapping("/view/{id}")
    public String viewAbonent(Model model, @PathVariable("id") Long id) {
        Abonent abonent = abonentService.getOne(id);
        model.addAttribute("abonent", abonent);
        return "abonent/view";
    }

    @PostMapping("/saveAbonent")
    public String saveAbonent(@Valid Abonent abonent, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("fiderList", fiderService.getAll());
            return "abonent/edit";
        } else {
            Abonent savedAbonent = abonentService.saveOne(abonent);
            return "redirect:/abonent/view/" + savedAbonent.getAccountNumber();
        }
    }

    //todo сделать через POST + csrf
    @GetMapping("/delete/{id}")
    public String deleteAbonentById(@PathVariable("id") Long id) {
        abonentService.deleteOne(id);
        return "redirect:/abonent";
    }
}
