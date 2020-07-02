package net.energo.grodno.pes.smsSender.utils;

import net.energo.grodno.pes.smsSender.Services.AbonentService;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.Services.OrderService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.*;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepository;
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
    private List<OrderItem> items;
    private TpService tpService;
    private FiderService fiderService;
    private AbonentService abonentService;
    private OrderService orderService;
    private UserRepository userRepository;

    @Autowired
    public ShoppingCart(TpService tpService, FiderService fiderService, AbonentService abonentService, OrderService orderService) {
        this.tpService = tpService;
        this.fiderService = fiderService;
        this.abonentService = abonentService;
        this.orderService = orderService;
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
    public Integer addFider(Integer id) {
        Fider fider=fiderService.getOne(id);
        int count=0;
        for(Abonent abonent:fider.getAbonents()){
            count+=addAbonent(abonent.getAccountNumber());
        }
        return count;
    }

    @Transactional
    public Integer addTp(Integer id) {
        //добавить в корзину Товар("получателей") по ТП
        Tp tp = tpService.getOne(id);
        int count=0;
        for(Fider fider:tp.getFiders()){
            count+=addFider(fider.getId());
        }
        return count;
    }

    public Integer createOrderAndSendSms(Order order, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).get();
        order.setUser(user);
//todo: set orderItems
        orderService.saveOne(order);
        System.out.println(order.getId());
        return 1;
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
