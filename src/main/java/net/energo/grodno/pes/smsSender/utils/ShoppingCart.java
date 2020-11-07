package net.energo.grodno.pes.smsSender.utils;

import net.energo.grodno.pes.smsSender.Services.*;
import net.energo.grodno.pes.smsSender.entities.*;
import net.energo.grodno.pes.smsSender.entities.users.User;
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
    private PartService partService;
    private LineService lineService;
    private SectionService sectionService;
    private SubstationService substationService;

    @Autowired
    public ShoppingCart(TpService tpService, FiderService fiderService, AbonentService abonentService, OrderService orderService, UserService userService, PartService partService, LineService lineService, SectionService sectionService, SubstationService substationService) {
        this.tpService = tpService;
        this.fiderService = fiderService;
        this.abonentService = abonentService;
        this.orderService = orderService;
        this.userService = userService;
        this.partService = partService;
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.substationService = substationService;
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
            count+=this.addAbonent(abonent.getAccountNumber());
        }
        return count;
    }

    @Transactional
    public Integer addTp(Long id) {
        //добавить в корзину Товар("получателей") по ТП
        Tp tp = tpService.getOne(id);
        int count=0;
        for(Fider fider:tp.getFiders()){
            count+=this.addFider(fider.getId());
        }
        return count;
    }

    public int addPart(Long id) {
        //добавить в корзину получателей по Участку
        Part part = partService.getOne(id);
        int count=0;
        for(Tp tp:part.getTps()){
            count+=this.addTp(tp.getId());
        }
        return count;
    }

    public int addLine(Long id) {
        Line line = lineService.getOne(id);
        int count=0;
        for(Part part:line.getParts()){
            count+=this.addPart(part.getId());
        }
        return count;
    }

    public int addSection(Integer id) {
        Section section = sectionService.getOne(id);
        int count=0;
        for(Line line:section.getLines()){
            count+=this.addLine(line.getId());
        }
        return count;
    }

    public int addSubstation(Integer id) {
        Substation substation = substationService.getOne(id);
        int count=0;
        for(Section section:substation.getSections()){
            count+=this.addSection(section.getId());
        }
        return count;
    }

    public Integer createOrderAndSendSms(Order order, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            order.setUser(user);
            List<SmsResponse> smsResponse = SmsAPI.sendSms(getNumbersForSms(),order.getMessage());
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
        List<OrderItem> newItemList = new ArrayList<>();
            for(SmsResponse smsResponse:smsResponseList){
                for (OrderItem item : items) {
                    if (item.getAbonent().getFirstPhone().equals(smsResponse.getRecipientWithoutPrefix())
                            || item.getAbonent().getSecondPhone().equals(smsResponse.getRecipientWithoutPrefix())) {
                        item.setSmsId(smsResponse.getSmsId());
                        item.setSmsCount(smsResponse.getSmsCount());
                        item.setOperator(smsResponse.getOperator());
                        item.setRecipient(smsResponse.getRecipientWithoutPrefix());
                        item.setErrorCode(smsResponse.getErrorCode());
                        item.setSmsStatus(ErrorsTable.ERRORS.get(smsResponse.getErrorCode()));
                        newItemList.add(item);
                        break;
                    }
                }
            }
        items = newItemList;
    }

    private List<String> getNumbersForSms() {
        List<String> result = new ArrayList<>();
        for (OrderItem item:items) {
            String firstPhone=item.getAbonent().getFirstPhone();
            String secondPhone=item.getAbonent().getSecondPhone();
            if(!firstPhone.isEmpty() && !firstPhone.equals("0") && firstPhone.length()>=PHONE_LENGTH){
                result.add(firstPhone);
            } else if(!secondPhone.isEmpty() && !secondPhone.equals("0") && secondPhone.length()>=PHONE_LENGTH){
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
