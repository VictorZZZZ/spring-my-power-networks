package net.energo.grodno.pes.smsSender.utils;

import net.energo.grodno.pes.smsSender.Services.AbonentService;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.OrderItem;
import net.energo.grodno.pes.smsSender.entities.Tp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    private List<OrderItem> items;
    private TpService tpService;
    private FiderService fiderService;
    private AbonentService abonentService;

    @Autowired
    public ShoppingCart(TpService tpService, FiderService fiderService, AbonentService abonentService) {
        this.tpService = tpService;
        this.fiderService = fiderService;
        this.abonentService = abonentService;
        this.items=new ArrayList<>();
    }




    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    //добавить в корзину Товар("получателя")
    @Transactional
    public int addAbonent(Long id) {
        OrderItem newItem = new OrderItem();
        try {
            //если абонент существует
            Abonent abonent = abonentService.getOne(id);
            System.out.println(abonent);
            newItem.setAbonent(abonent);
            items.add(newItem);
            return 1;
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
            OrderItem newItem = new OrderItem();
            newItem.setAbonent(abonent);
            items.add(newItem);
            count++;
        }
        return count;
    }

    @Transactional
    public Integer addTp(Integer id) {
        //добавить в корзину Товар("получателей") по ТП
        Tp tp = tpService.getOne(id);
        int count=0;
        for(Fider fider:tp.getFiders()){
            for(Abonent abonent:fider.getAbonents()){
                OrderItem newItem = new OrderItem();
                newItem.setAbonent(abonent);
                items.add(newItem);
                count++;
            }
        }
        return count;
    }
}
