package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ReportService;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.utils.dates.ZvvDates;
import net.energo.grodno.pes.smsSender.utils.statistics.ResStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    @Value("${sms.price}")
    private String smsPrice;

    private ReportService reportService;
    private ResService resService;

    @Autowired
    public ReportsController(ReportService reportService, ResService resService) {
        this.reportService = reportService;
        this.resService = resService;
    }

    @RequestMapping(value = {"", "/", "index"}, method = RequestMethod.GET)
    public String index(Model model) {
        List<Res> resList = resService.getAllRes();
        HashMap<Res, ResStatistics> report = new HashMap<>();
        int totalSms = 0;
        double totalMoney = 0.0;
        long totalAbonents = 0;
        for (Res res : resList) {
            ResStatistics resStatistics = new ResStatistics();
            resStatistics.setRes(res);
            int smsCount = reportService.getSmsCountForPeriodAndResId(
                    ZvvDates.firstDayofThisMonth(),
                    ZvvDates.lastDayofThisMonth(),
                    res.getId()
            );
            resStatistics.setThisMonthSmsCount(smsCount);
            totalSms += smsCount;
            double spentMoney = resStatistics.getThisMonthSmsCount() * Double.valueOf(smsPrice);
            resStatistics.setSpentMoneyThisMonth(spentMoney);
            totalMoney = totalMoney + spentMoney;
            totalAbonents += res.getCachedAbonentsCount();
            report.put(res, resStatistics);
        }
        ;
        model.addAttribute("totalSms", totalSms);
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("totalAbonents", totalAbonents);
        model.addAttribute("report", report);
        model.addAttribute("smsPrice", smsPrice);
        model.addAttribute("firstDayOfThisMonth",ZvvDates.firstDayofThisMonth());
        model.addAttribute("today", new Date());
        return "reports/index";
    }
}
