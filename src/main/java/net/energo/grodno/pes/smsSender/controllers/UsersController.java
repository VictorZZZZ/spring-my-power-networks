package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.Services.RoleService;
import net.energo.grodno.pes.smsSender.Services.UserService;
import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.users.Role;
import net.energo.grodno.pes.smsSender.entities.users.User;
import net.energo.grodno.pes.smsSender.repositories.user.UserRepr;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UsersController {
    private UserService userService;
    private RoleService roleService;
    private ResService resService;

    public UsersController(UserService userService, RoleService roleService, ResService resService) {
        this.userService = userService;
        this.roleService = roleService;
        this.resService = resService;
    }

    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public String showAll(Model model){
        List<User> users = userService.findAll();
        model.addAttribute("users",users);
        return "user/index";
    }

    @RequestMapping(value = {"/delete/{id}"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        userService.deleteOne(id);
        redirectAttributes.addFlashAttribute("messageInfo","Пользователь удален");
        return "redirect:/users";
    }
    @RequestMapping(value = {"/edit/{id}"}, method = RequestMethod.GET)
    public String editUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,Model model){
        Optional<User> user = userService.findById(id);
        if(user.isPresent()) {
            UserRepr userRepr = new UserRepr();
            userRepr.setId(user.get().getId());
            userRepr.setUsername(user.get().getUsername());
            userRepr.setRes(user.get().getRes());
            userRepr.setRoles(user.get().getRoles());
            model.addAttribute("user",userRepr);
            System.out.println(user.get());
            List<Res> resList = resService.getAllRes();
            model.addAttribute("resList",resList);
            List<Role> roleList = roleService.findAll();
            model.addAttribute("roleList",roleList);
            return "user/edit";
        } else {
            redirectAttributes.addFlashAttribute("messageError","не существует пользователя с id="+id);
            return "redirect:/users";
        }
    }

    @RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
    public String registerNewUser(@Valid UserRepr userRepr, BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            //todo frontend не выдаёт ошибки красным цветом под полями
            model.addAttribute("user",userRepr);
            List<Res> resList = resService.getAllRes();
            model.addAttribute("resList",resList);
            List<Role> roleList = roleService.findAll();
            model.addAttribute("roleList",roleList);
            return "user/edit";
        }
        if (!userRepr.getPassword().equals(userRepr.getRepeatPassword())) {
            bindingResult.rejectValue("password", "", "Пароли не совпадают");
            model.addAttribute("user",userRepr);
            List<Res> resList = resService.getAllRes();
            model.addAttribute("resList",resList);
            List<Role> roleList = roleService.findAll();
            model.addAttribute("roleList",roleList);
            return "user/edit";
        }

        userService.save(userRepr);
        redirectAttributes.addFlashAttribute("messageInfo","Пользователь отредактирован");
        return "redirect:/users";
    }


}
