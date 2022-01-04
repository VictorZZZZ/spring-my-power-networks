package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.sms.Services.OrderService;
import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.sms.entities.Order;
import net.energo.grodno.pes.sms.entities.OrderItem;
import net.energo.grodno.pes.sms.repositories.OrderItemRepository;
import net.energo.grodno.pes.sms.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class OrderServiceTest extends AbstractClass {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Order order;
    private List<OrderItem> items = new ArrayList<>();

    @BeforeEach
    void setUp() {
        order = new Order();
        BigDecimal amount = new BigDecimal(22);
        order.setAmount(1.2);
        order.setPrice(2.6);
        order.setMessage("Some message");
        order.setItems(items);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setSmsId("123");
        orderItem.setRecipient("291234567");
        items.add(orderItem);
    }

    @Test
    void saveOrderWithItems() {
        System.out.println(order);
        orderService.saveOrderWithItems(order,items);
    }

    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAll(items);
        orderRepository.delete(order);
    }
}