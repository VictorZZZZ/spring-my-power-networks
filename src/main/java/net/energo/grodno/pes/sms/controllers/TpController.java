package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.services.ResService;
import net.energo.grodno.pes.sms.services.TpService;
import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/tp")
public class TpController {
    private static final int ELEMENTS_PER_PAGE = 20;
    private TpService tpService;
    private ResService resService;

    @Autowired
    public TpController(TpService tpService, ResService resService) {
        this.tpService = tpService;
        this.resService = resService;
    }


    public String showAll(Model model){
        //todo: сделать пагинацию
        List<Tp> tps = tpService.getAll();
        model.addAttribute("tps",tps);
        return "tp/index";
    }

    @GetMapping(value={"","/","index","/listTp"})
    public String showPaginated(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(ELEMENTS_PER_PAGE);

        Page<Tp> tpPage = tpService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("tpsPaginated", tpPage);

        int totalPages = tpPage.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        long totalTp = tpService.getCount();
        model.addAttribute("totalTp", totalTp);
        model.addAttribute("linkTo","/listTp");
        return "tp/index_paginated";
    }

    @GetMapping("/viewNotLinkedTp")
    public String viewNotLinkedTp(Model model,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(ELEMENTS_PER_PAGE);
        try {
            Page<Tp> tpPage = tpService.getNotLinkedTpsByUser(principal.getName(), PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("tpsPaginated", tpPage);

            int totalPages = tpPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            long totalTp = tpService.countNotLinkedTpsByUser(principal.getName());
            model.addAttribute("totalTp", totalTp);
            model.addAttribute("linkTo","/viewNotLinkedTp");
            return "tp/index_paginated";
        } catch (Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("messageError","Ошибка запроса.");
        return "redirect:"+request.getHeader("Referer");
    }

    @GetMapping(value={"/add"})
    public String addTp(Model model){
        Tp tp = new Tp();
        List<Res> resList= resService.getAllRes();
        model.addAttribute("tp",tp);
        model.addAttribute("resList",resList);
        return "tp/edit";
    }

    @GetMapping("/edit/{id}")
    public String editTp(Model model, @PathVariable("id") Long id){
        Tp tp = tpService.getOne(id);
        List<Res> resList= resService.getAllRes();
        model.addAttribute("tp",tp);
        model.addAttribute("resList",resList);
        return "tp/edit";
    }

    @GetMapping("/view/{id}")
    public String viewTp(Model model, @PathVariable("id") Long id){
        Tp tp = tpService.getOne(id);
        model.addAttribute("tp",tp);
        List<Fider> fiderList = tp.getFiders();
        int abonentCount = fiderList.stream().mapToInt(fider -> (int) fider.getAbonents().size()).sum();
        model.addAttribute("abonentCount",abonentCount);
        return "tp/view";
    }

    @RequestMapping(value = "/saveTp", method = RequestMethod.POST)
    public String saveTp(@Valid Tp tp, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult.getAllErrors());
            model.addAttribute("resList",resService.getAllRes());
            return "tp/edit";
        } else {
            tpService.saveOne(tp);
            return "redirect:/tp/index";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteResById(@PathVariable("id") Long id, HttpServletRequest request){
        tpService.deleteOne(id);
        return "redirect:"+request.getHeader("Referer");
    }



}
