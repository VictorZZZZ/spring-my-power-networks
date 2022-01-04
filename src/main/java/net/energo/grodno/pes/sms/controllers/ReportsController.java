package net.energo.grodno.pes.sms.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.utils.statistics.ResStatistics;
import net.energo.grodno.pes.sms.Services.ReportService;
import net.energo.grodno.pes.sms.Services.ResService;
import net.energo.grodno.pes.sms.utils.dates.ZvvDates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    Logger logger = LoggerFactory.getLogger(ReportsController.class);
    private static final String ERROR_RESPONSE = "{\"error\":true}";
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
        model.addAttribute("firstDayOfThisMonth", ZvvDates.firstDayofThisMonth());
        model.addAttribute("today", new Date());
        return "reports/index";
    }

    @RequestMapping(value = {"/detailed"}, method = RequestMethod.GET)
    public String detailed(@Param("resId") Integer resId, Model model) {
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList", resList);
        return "reports/detailed";
    }

    @CrossOrigin
    @RequestMapping(value = {"/detailed/getResStat"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getResStat(@Param("resId") Integer resId,
                             @Param("lastMonth") Integer lastMonth,
                             HttpServletResponse response) {
        if (lastMonth == null) lastMonth = 3;
        if (resId == null) return ERROR_RESPONSE;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (int i = 0; i < lastMonth; i++) {
            int smsCount = reportService.getSmsCountForPeriodAndResId(
                    ZvvDates.dayOfMonthFromNow(-i, ZvvDates.FIRST_DAY),
                    ZvvDates.dayOfMonthFromNow(-i, ZvvDates.LAST_DAY),
                    resId
            );
            ObjectNode monthStat = mapper.createObjectNode();
            monthStat.put("id", i);
            monthStat.put("monthName", ZvvDates.monthNameFromNow(-i));
            monthStat.put("smsCount", smsCount);
            monthStat.put("spentMoney", (smsCount * Double.parseDouble(smsPrice)));
            monthStat.put("resId", resId);
            monthStat.put("resName", resService.getOne(resId).getName());

            arrayNode.add(monthStat);
        }
        try {
            //json
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return ERROR_RESPONSE;
        }
    }


}
