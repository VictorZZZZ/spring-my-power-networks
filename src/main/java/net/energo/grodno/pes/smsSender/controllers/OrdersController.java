package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.OrderService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/orders")
public class OrdersController {
    private OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String showAllOrdersPaginated( Model model,
                                 @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(50);
        Page<Order> orderPage = orderService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("ordersPaginated", orderPage);

        int totalPages = orderPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        long totalOrders = orderService.getCount();
        model.addAttribute("totalOrders",totalOrders);

        return "orders/index_paginated";
    }
}
