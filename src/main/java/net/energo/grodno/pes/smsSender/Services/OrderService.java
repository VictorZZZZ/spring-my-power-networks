package net.energo.grodno.pes.smsSender.Services;

import net.energo.grodno.pes.smsSender.entities.Order;
import net.energo.grodno.pes.smsSender.repositories.OrderRepository;
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

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Order> list;
        long ordersCount = orderRepository.count();

        if (ordersCount < startItem) {
            list = Collections.emptyList();
            Page<Order> orderPage
                    = new PageImpl<Order>(list, PageRequest.of(currentPage, pageSize), ordersCount);

            return orderPage;
        } else {
            Page<Order> orderPage
                    = orderRepository.findAll(pageable);
            return orderPage;
        }

    }

    public long getCount() {
        return orderRepository.count();
    }

    public void saveOne(Order order){ orderRepository.save(order);}
}
