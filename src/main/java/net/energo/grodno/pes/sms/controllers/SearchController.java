package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.Services.TpService;
import net.energo.grodno.pes.sms.Services.AbonentService;
import net.energo.grodno.pes.sms.entities.Abonent;
import net.energo.grodno.pes.sms.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {
    private AbonentService abonentService;
    private TpService tpService;

    @Autowired
    public SearchController(AbonentService abonentService, TpService tpService) {
        this.abonentService = abonentService;
        this.tpService = tpService;
    }

    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String search(Model model, @RequestParam("searchLine") Optional<String> search, RedirectAttributes redirectAttributes) {
        String searchLine = search.orElse("");
        if(searchLine.length()>2){
            //строка должна быть не менее 3 символов
            List<Abonent> searchAbonentResult = new ArrayList<>();
            searchAbonentResult.addAll(abonentService.searchBySurname(searchLine));
            if(searchLine.length()>=4) {
                //поиск по номеру телефона и лицевому счёту только от четырех цифр, Иначе поиск будет очень долгим
                searchAbonentResult.addAll(abonentService.searchByNumber(searchLine));
                searchAbonentResult.addAll(abonentService.searchByAccountNumber(searchLine));
            }

            List<Tp> searchTpResult = new ArrayList<>();
            searchTpResult.addAll(tpService.searchByTpName(searchLine));

            model.addAttribute("tpList",searchTpResult);
            model.addAttribute("abonentList",searchAbonentResult);
            return "search/index";
        } else {
            redirectAttributes.addFlashAttribute("messageError","Строка поиска не должна быть пустой, либо быть длинной не менее 3 символов.");
            return "redirect:/";
        }

    }
}
