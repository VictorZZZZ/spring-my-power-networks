package net.energo.grodno.pes.smsSender.Cart;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.smsSender.utils.ShoppingCart;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;

public class CartTest extends AbstractClass {
    @Autowired
    ShoppingCart cart;

    @Test
    @Ignore
    public void testAddingAbonentToCart(){
        cart.addAbonent(12094800121L);
        cart.addAbonent(109481L);
        System.out.println(cart.getItems());
        if(cart.getItems().size()==2){
            assert(true);
        } else {
            assert(false);
        }
    }

    @Test
    @Ignore
    public void testAddingFiderToCart(){
        cart.addFider(3821L);
        System.out.println(cart.getItems().size());
    }

    @Test
    @Ignore
    public void testAddingTpToCart(){
        cart.addTp(8543L);
        System.out.println(cart.getItems().size());
    }
}
