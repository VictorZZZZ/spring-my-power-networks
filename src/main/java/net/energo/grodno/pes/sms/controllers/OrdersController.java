package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.services.OrderService;
import net.energo.grodno.pes.sms.services.UserService;
import net.energo.grodno.pes.sms.entities.Abonent;
import net.energo.grodno.pes.sms.entities.Order;
import net.energo.grodno.pes.sms.entities.OrderItem;
import net.energo.grodno.pes.sms.entities.users.User;
import net.energo.grodno.pes.sms.security.AuthDetails;
import net.energo.grodno.pes.sms.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/orders")
public class OrdersController {
    private final OrderService orderService;
    private final UserService userService;
    private final ShoppingCart cart;

    @Autowired
    public OrdersController(OrderService orderService, UserService userService, ShoppingCart cart) {
        this.orderService = orderService;
        this.userService = userService;
        this.cart = cart;
    }

    @GetMapping(value = {"", "/", "index", "/listOrders"})
    public String showAllOrdersPaginated(Model model,
                                         @RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size") Optional<Integer> size,
                                         Authentication authentication) throws Exception {
        //todo изменить в сессии количество списка рассылки после отправки СМС
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(50);
        Page<Order> orderPage;
        if (AuthDetails.listRoles(authentication).contains("ROLE_ADMIN")
                || AuthDetails.listRoles(authentication).contains("ROLE_SHOWALLSMS")) {
            //Если Admin или ROLE_SHOWALLSMS, то показывать заказы всех пользователей
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

    @GetMapping(value = {"/view/{id}"})
    public String viewOrder(Model model, @PathVariable("id") Long id) {
        Order order = orderService.getOne(id);
        model.addAttribute("order", order);
        return "orders/view";
    }

    @GetMapping(value = {"/byUser/{id}"})
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

    @GetMapping(value = "/{id}/addConsumersToNewList")
    public String addConsumersToNewList(@PathVariable("id") Long orderId, HttpServletRequest request, RedirectAttributes resdirectAttributes){
        Order order = orderService.getOne(orderId);
        if(order == null){
            resdirectAttributes.addFlashAttribute("messageError", String.format("Заказа с идентификатором %d не существует", orderId));
            return "redirect:"+request.getHeader("Referer");
        }
        List<OrderItem> smsList = order.getItems();
        if(!smsList.isEmpty()){
            List<Abonent> abonents = smsList.stream()
                    .map(OrderItem::getAbonent)
                    .collect(Collectors.toList());
            abonents.forEach(abonent -> cart.addAbonent(abonent.getAccountNumber()));
            resdirectAttributes.addFlashAttribute("messageInfo", String.format("В список рассылки добавлено %d абонентов",abonents.size()));
        }
        return "redirect:"+request.getHeader("Referer");
    }
}
