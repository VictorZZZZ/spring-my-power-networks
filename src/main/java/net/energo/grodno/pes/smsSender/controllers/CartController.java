package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Order;
import net.energo.grodno.pes.smsSender.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartController {
    private ShoppingCart cart;

    @Autowired
    public CartController(ShoppingCart cart) {
        this.cart = cart;
    }

    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String cart(HttpSession session,Model model){
        session.setAttribute("cartItemsCount",cart.getItems().size());
        Order order = new Order();
        model.addAttribute("order",order);
        model.addAttribute("cartItems",cart.getItems());
        return "cart/index";
    }

    @GetMapping("/add/tp/{id}")
    public String addTpToCart(HttpSession session,HttpServletRequest request,Model model, @PathVariable("id") Integer id, RedirectAttributes redirectAttributes){
        //добавление ТП
        int count = cart.addTp(id);
        redirectAttributes.addFlashAttribute("messageInfo","В список рассылки добавлено " + count + " абонентов");
        session.setAttribute("cartItemsCount",cart.getItems().size());
        return "redirect:"+request.getHeader("Referer");
    }

    //добавление Фидера
    @GetMapping("/add/fider/{id}")
    public String addFiderToCart(HttpSession session,HttpServletRequest request,Model model, @PathVariable("id") Integer id, RedirectAttributes redirectAttributes){
        int count = cart.addFider(id);
        redirectAttributes.addFlashAttribute("messageInfo","В список рассылки добавлено " + count + " абонентов");
        session.setAttribute("cartItemsCount",cart.getItems().size());
        return "redirect:"+request.getHeader("Referer");
    }

    //todo переделать в POST
    @GetMapping("/add/abonent/{id}")
    public String addAbonentToCart(HttpSession session,HttpServletRequest request, Model model, @PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        int count= cart.addAbonent(id);
        if(count>0) {
            redirectAttributes.addFlashAttribute("messageInfo","Абонент добавлен  в список рассылки");
        } else redirectAttributes.addFlashAttribute("messageError","Не существует абонента с id "+id);
        session.setAttribute("cartItemsCount",cart.getItems().size());
        return "redirect:"+request.getHeader("Referer");
    }

    //создание ЗАКАЗА
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    public String createOrderAndSendSms(Order order, Model model, Principal principal,RedirectAttributes redirectAttributes) {
        Integer sentSms = cart.createOrderAndSendSms(order,principal);
        if(sentSms>0)
            redirectAttributes.addAttribute("messageInfo","Отправлено "+ sentSms + " сообщений");
        return "redirect:/orders/";
    }

    @GetMapping("/clear")
    public String clear(RedirectAttributes redirectAttributes){
        cart.clear();
        redirectAttributes.addFlashAttribute("messageInfo","Список рассылки очищен");
        return "redirect:/cart";
    }

    @GetMapping("/removeFromCart/{accountNumber}")
    public String removeFromCart(@PathVariable("accountNumber") Long accountNumber,RedirectAttributes redirectAttributes){
        if(cart.removeFromCart(accountNumber)) {
            redirectAttributes.addFlashAttribute("messageInfo", "Абонент удалён из рассылки");
        } else {
            redirectAttributes.addFlashAttribute("messageError", "Абонент с таким Id не найден");
        }
        return "redirect:/cart";
    }

}
