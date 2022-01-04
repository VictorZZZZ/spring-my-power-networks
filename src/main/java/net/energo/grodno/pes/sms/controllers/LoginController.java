package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.repositories.user.UserRepr;
import net.energo.grodno.pes.sms.Services.ResService;
import net.energo.grodno.pes.sms.Services.RoleService;
import net.energo.grodno.pes.sms.Services.UserService;
import net.energo.grodno.pes.sms.entities.users.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class LoginController {
    private final UserService userService;
    private final ResService resService;
    private final RoleService roleService;

    @Autowired
    public LoginController(UserService userService, ResService resService, RoleService roleService) {
        this.userService = userService;
        this.resService = resService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request,Model model) {
        if(request.getHeader("x-requested-with")!=null){
            //Проверка на асинхронность запроса. Если запрос асинхронный, то есть header c именем "x-requested-with"
            model.addAttribute("messageError","В доступе отказано, попробуйте зайти под своей учетной записью!");
            return "flashMessages";
        }

        return "user/login";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roleList",roleList);
        model.addAttribute("user", new UserRepr());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("user") UserRepr userRepr, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }
        if (!userRepr.getPassword().equals(userRepr.getRepeatPassword())) {
            bindingResult.rejectValue("password", "", "Пароли не совпадают");
            return "user/register";
        }

        userService.create(userRepr);
        return "redirect:/login";
    }
}
