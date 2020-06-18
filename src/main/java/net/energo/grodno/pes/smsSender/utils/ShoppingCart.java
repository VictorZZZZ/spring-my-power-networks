package net.energo.grodno.pes.smsSender.utils;

import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    private List<OrderItem> items;
    private TpService tpService;

    @Autowired
    public ShoppingCart(TpService tpService) {
        this.tpService = tpService;
        this.items=new ArrayList<>();
    }

    public void addTp(Integer id) {
        //добавить в корзину Товар("получателей") по ТП

    }
    //добавить в корзину Товар("получателя") , по Фидеру, или одного Абонента)


    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
