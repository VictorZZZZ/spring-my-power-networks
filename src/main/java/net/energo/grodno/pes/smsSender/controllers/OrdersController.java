package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.OrderService;
import net.energo.grodno.pes.smsSender.Services.UserService;
import net.energo.grodno.pes.smsSender.entities.Order;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.security.AuthDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/orders")
public class OrdersController {
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public OrdersController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @RequestMapping(value = {"", "/", "index", "/listOrders"}, method = RequestMethod.GET)
    public String showAllOrdersPaginated(Model model,
                                         @RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size") Optional<Integer> size,
                                         Authentication authentication) throws Exception {
        //todo изменить в сессии количество списка рассылки после отправки СМС
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(50);
        Page<Order> orderPage;
        if (AuthDetails.listRoles(authentication).contains("ROLE_ADMIN")) {
            //Если Admin, то показывать заказы всех пользователей
            orderPage = orderService.findAllPaginated(PageRequest.of(currentPage - 1, pageSize));
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //System.out.println("User has authorities: " + userDetails.getAuthorities());
            User user = userService.findByUsername(userDetails.getUsername());
            orderPage = orderService.findPaginatedByUserId(user, PageRequest.of(currentPage - 1, pageSize));
        }

        model.addAttribute("ordersPaginated", orderPage);

        int totalPages = orderPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        long totalOrders = orderService.getCount();
        model.addAttribute("totalOrders", totalOrders);

        return "orders/index_paginated";
    }

    @RequestMapping(value = {"/view/{id}"}, method = RequestMethod.GET)
    public String viewOrder(Model model, @PathVariable("id") Long id) {
        Order order = orderService.getOne(id);
        model.addAttribute("order", order);
        return "orders/view";
    }

    @RequestMapping(value = {"/byUser/{id}"}, method = RequestMethod.GET)
    public String showOrdersOfUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        int currentPage = 1;
        int pageSize = 50;
        Page<Order> orderPage;
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            orderPage = orderService.findPaginatedByUserId(user.get(), PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("ordersPaginated", orderPage);

            int totalPages = orderPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            long totalOrders = orderService.getCount();
            model.addAttribute("totalOrders", totalOrders);

            return "orders/index_paginated";
        } else {
            redirectAttributes.addFlashAttribute("messageError", "Нет пользователя с id=" + id);
            return "redirect:/";
        }
    }
}
