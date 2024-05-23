package com.yc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {


    @RequestMapping(value = "/")
    public String GoToIndex(){
        System.out.println("访问首页地址:");
        return "redirect:/index.html";
    }
}
