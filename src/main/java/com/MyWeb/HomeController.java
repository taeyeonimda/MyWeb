package com.MyWeb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model){
        model.getAttribute("currentUser");//            System.out.println("널임다");
//            System.out.println(model.getAttribute("currentUser"));
        return "index";
    }

}
