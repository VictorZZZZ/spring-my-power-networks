package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.OrderService;
import net.energo.grodno.pes.smsSender.entities.Order;
import net.energo.grodno.pes.smsSender.entities.OrderItem;
import net.energo.grodno.pes.smsSender.utils.smsAPI.ErrorsTable;
import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsAPI;
import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsSenderErrorException;
import net.energo.grodno.pes.smsSender.utils.smsAPI.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sms")
public class SmsController {
    private final OrderService orderService;
    private final SmsAPI smsAPI;

    @Autowired
    public SmsController(OrderService orderService, SmsAPI smsAPI) {
        this.orderService = orderService;
        this.smsAPI = smsAPI;
    }

    @GetMapping(value = {"/checkBalance"})
    public String checkBalance(HttpServletRequest request,RedirectAttributes redirectAttributes){
        try {
            String balance = smsAPI.checkBalance();
            redirectAttributes.addFlashAttribute("messageInfo","Текущий баланс: "+balance+"руб.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Ошибка связи с сервером (IOException | InterruptedException) проверьте интернет соединение и попробуйте ещё раз.");
        }

        return "redirect:"+request.getHeader("Referer");
    }

    @GetMapping(value = {"/checkStatuses/order/{id}"})
    public String checkStatuses(HttpServletRequest request, RedirectAttributes redirectAttributes,
                                @PathVariable("id") Long id){
        try {
            Order order = orderService.getOne(id);
            List<OrderItem> orderItems = order.getItems();
            List<Long> smsIds = new ArrayList<>();
            for (OrderItem item:orderItems) {
                smsIds.add(item.getSmsId());
            }
            List<StatusResponse> statusResponses = smsAPI.checkStatuses(smsIds);
            int count = parseStatusResponses(orderItems, statusResponses);
            orderService.saveOrderWithItems(order,orderItems);
            redirectAttributes.addFlashAttribute("messageInfo","Обновлено " + count+ " статусов смс.");
        } catch(EntityNotFoundException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Нет рассылки с таким Id");
        } catch (IOException| InterruptedException | SmsSenderErrorException | NullPointerException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError","Ошибка: " + e.getMessage());
        }

        return "redirect:/orders/view/"+id;

    }

    private int parseStatusResponses(List<OrderItem> orderItems, List<StatusResponse> statusResponses) {
        int counter=0;
        for(StatusResponse statusResponse:statusResponses){
            for(OrderItem orderItem:orderItems){
                if((orderItem.getSmsId() instanceof Long) && (orderItem.getSmsId().equals(statusResponse.getSmsId()))){
                    //"sms_id":886055815,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375297819778"
                    orderItem.setSmsStatus(ErrorsTable.STATUS.get(statusResponse.getSmsStatus()));
                    counter++;
                    break;
                }
            }
        }
        return counter;
    }

}
