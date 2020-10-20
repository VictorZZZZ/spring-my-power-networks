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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicInteger totalSms = new AtomicInteger();
        AtomicReference<Double> totalMoney = new AtomicReference<>((double) 0);
        AtomicLong totalAbonents = new AtomicLong();
        resList.stream().forEach(res -> {
            ResStatistics resStatistics = new ResStatistics();
            resStatistics.setRes(res);
            int smsCount = reportService.getSmsCountForPeriodAndResId(
                    ZvvDates.firstDayofThisMonth(),
                    ZvvDates.lastDayofThisMonth(),
                    res.getId()
            );
            resStatistics.setThisMonthSmsCount(smsCount);
            totalSms.set(smsCount);
            double spentMoney = resStatistics.getThisMonthSmsCount()*Double.valueOf(smsPrice);
            resStatistics.setSpentMoneyThisMonth(spentMoney);
            totalMoney.set(spentMoney);
            totalAbonents.addAndGet(res.getCachedAbonentsCount());
            report.put(res,resStatistics);
        });
        model.addAttribute("totalSms",totalSms);
        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("totalAbonents",totalAbonents);
        model.addAttribute("report",report);
        model.addAttribute("smsPrice", smsPrice);
        return "reports/index";
    }
}
