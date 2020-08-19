package net.energo.grodno.pes.smsSender.utils;

import net.energo.grodno.pes.smsSender.Services.*;
import net.energo.grodno.pes.smsSender.entities.*;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepository;
import net.energo.grodno.pes.smsSender.utils.smsAPI.ErrorsTable;
import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsAPI;
import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    private static final int PHONE_LENGTH = 9;
    private List<OrderItem> items;
    private TpService tpService;
    private FiderService fiderService;
    private AbonentService abonentService;
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public ShoppingCart(TpService tpService, FiderService fiderService, AbonentService abonentService, OrderService orderService, UserService userService) {
        this.tpService = tpService;
        this.fiderService = fiderService;
        this.abonentService = abonentService;
        this.orderService = orderService;
        this.userService = userService;
        this.items=new ArrayList<>();

    }



    @Transactional
    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    //добавить в корзину Товар("получателя")
    @Transactional
    public int addAbonent(Long id) {

        try {
            //проверка на абонента в корзине
            Optional<OrderItem> optOrderItem = items.stream()
                    .filter(orderItem -> {return orderItem.getAbonent().getAccountNumber().equals(id);})
                    .findFirst();
            if(!optOrderItem.isPresent()){
                //если абонент существует
                Abonent abonent = abonentService.getOne(id);
                OrderItem newItem = new OrderItem();
                newItem.setAbonent(abonent);
                items.add(newItem);
                return 1;
            } else return 0;
        } catch (EntityNotFoundException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    //добавить по Фидеру, или одного Абонента)
    @Transactional
    public Integer addFider(Long id) {
        Fider fider=fiderService.getOne(id);
        int count=0;
        for(Abonent abonent:fider.getAbonents()){
            count+=addAbonent(abonent.getAccountNumber());
        }
        return count;
    }

    @Transactional
    public Integer addTp(Long id) {
        //добавить в корзину Товар("получателей") по ТП
        Tp tp = tpService.getOne(id);
        int count=0;
        for(Fider fider:tp.getFiders()){
            count+=addFider(fider.getId());
        }
        return count;
    }

    public Integer createOrderAndSendSms(Order order, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            order.setUser(user);
            List<SmsResponse> smsResponse = SmsAPI.sendSms(getNumbersForSms(),order.getMessage());
//            List<SmsResponse> smsResponse = new ArrayList<SmsResponse>(Arrays.asList(new SmsResponse[]{new SmsResponse(1l, 2, 3, 4, "321321"),
//                    new SmsResponse(2l, 2, 3, -5, "297819778")}));
            parseSmsResponse(smsResponse);
            orderService.saveOrderWithItems(order,items);
            items.clear();
            return items.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    private void parseSmsResponse(List<SmsResponse> smsResponseList) {
            for(SmsResponse smsResponse:smsResponseList){
                for (int i = 0; i < items.size(); i++) {
                    if(items.get(i).getAbonent().getFirstPhone().equals(smsResponse.getRecipientWithoutPrefix())
                            || items.get(i).getAbonent().getSecondPhone().equals(smsResponse.getRecipientWithoutPrefix())){
                        items.get(i).setSmsId(smsResponse.getSmsId());
                        items.get(i).setSmsCount(smsResponse.getSmsCount());
                        items.get(i).setOperator(smsResponse.getOperator());
                        items.get(i).setRecipient(smsResponse.getRecipientWithoutPrefix());
                        items.get(i).setErrorCode(smsResponse.getErrorCode());
                        items.get(i).setSmsStatus(ErrorsTable.ERRORS.get(smsResponse.getErrorCode()));
                        continue;
                    }
                }
            }
        System.out.println(items);
    }

    private List<String> getNumbersForSms() {
        List<String> result = new ArrayList<>();
        for (OrderItem item:items) {
            String firstPhone=item.getAbonent().getFirstPhone();
            String secondPhone=item.getAbonent().getSecondPhone();
            if(!firstPhone.isEmpty() && !firstPhone.equals("0") && firstPhone.length()==PHONE_LENGTH){
                result.add(firstPhone);
            } else if(!secondPhone.isEmpty() && !secondPhone.equals("0") && secondPhone.length()==PHONE_LENGTH){
                result.add(secondPhone);
            }
        }
        return result;
    }

    public void clear(){
        this.items.clear();
    }

    public boolean removeFromCart(Long accountNumber) {
        Optional<OrderItem> optOrderItem = items.stream()
                                                .filter(orderItem -> {return orderItem.getAbonent().getAccountNumber().equals(accountNumber);})
                                                .findFirst();
        if(optOrderItem.isPresent()){
            items.remove(optOrderItem.get());
            return true;
        } else return false;
    }
}
