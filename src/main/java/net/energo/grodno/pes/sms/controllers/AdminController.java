package net.energo.grodno.pes.sms.controllers;

import lombok.AllArgsConstructor;
import net.energo.grodno.pes.sms.config.FtpProperties;
import net.energo.grodno.pes.sms.config.ImportData;
import net.energo.grodno.pes.sms.services.schedule.SchedulerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    @Value("${adminsNumbers}")
    private List<String> adminsNumbers;
    private final FtpProperties ftpProperties;
    private SchedulerService schedulerService;

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/ftpImport")
    public String ftpImport(Model model){
        model.addAttribute("bresImportData", ImportData.BRES.getResData());
        model.addAttribute("schresImportData", ImportData.SCHRES.getResData());
        model.addAttribute("gsresImportData", ImportData.GSRES.getResData());
        model.addAttribute("ggresImportData", ImportData.GGRES.getResData());
        model.addAttribute("baseFtpFolder", ftpProperties.getBaseFolder());
        model.addAttribute("adminPhone", adminsNumbers);

        return "admin/ftpImport";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "startFtpImport")
    public String startFtpImport(@Param("res") String res, HttpServletRequest request, RedirectAttributes resdirectAttributes){
        try{
            ImportData target = ImportData.valueOf(res);
            //todo: сделать проверку на поиск существующего thread, если interrupted, то запускать.
            schedulerService.importToTarget(target);
            resdirectAttributes.addFlashAttribute("messageInfo", "Старт импорта по: " + target);
        } catch (IllegalArgumentException e){
            resdirectAttributes.addFlashAttribute("messageError", "Нет такого РЭСа: " + res);
            return "redirect:/admin/ftpImport";
        }
        return "redirect:"+request.getHeader("Referer");
    }

}
