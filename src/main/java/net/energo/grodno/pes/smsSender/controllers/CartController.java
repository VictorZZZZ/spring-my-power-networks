package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    private ShoppingCart cart;

    @Autowired
    public CartController(ShoppingCart cart) {
        this.cart = cart;
    }

    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String cart(HttpSession session){
        session.setAttribute("cartItemsCount",cart.getItems().size());
        return "cart/index";
    }

    @GetMapping("/add/tp/{id}")
    public String addTpToCart(Model model, @PathVariable("id") Integer id, RedirectAttributes redirectAttributes){
        //добавление ТП
        cart.addTp(id);
        return "redirect:/";
    }
    //добавление Фидера или абонента в Корзину

    //создание ЗАКАЗА
}
