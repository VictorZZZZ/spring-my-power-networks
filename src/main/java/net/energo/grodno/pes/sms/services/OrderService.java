package net.energo.grodno.pes.sms.services;

import net.energo.grodno.pes.sms.entities.OrderItem;
import net.energo.grodno.pes.sms.repositories.OrderItemRepository;
import net.energo.grodno.pes.sms.repositories.OrderRepository;
import net.energo.grodno.pes.sms.entities.Order;
import net.energo.grodno.pes.sms.entities.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order getOne(Long id){
        return orderRepository.getOne(id);
    }

    public Page<Order> findPaginatedByUserId(User user, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Order> list;
        long ordersCount = orderRepository.count();

        if (ordersCount < startItem) {
            list = Collections.emptyList();
            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), ordersCount);
        } else {
            return orderRepository.findPageByUserId(user,pageable);
        }

    }

    public Page<Order> findAllPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Order> list;
        long ordersCount = orderRepository.count();

        if (ordersCount < startItem) {
            list = Collections.emptyList();
            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), ordersCount);
        } else {
           return orderRepository.findAllByOrderByCreatedDesc(pageable);
        }

    }

    public long getCount() {
        return orderRepository.count();
    }

    public void saveOne(Order order){ orderRepository.save(order);}

    public void saveOrderWithItems(Order order, List<OrderItem> orderItems){
        orderRepository.save(order);
        for (OrderItem item:orderItems) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);
    }
}
