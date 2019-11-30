package com.xuecheng.manage_cms.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class helloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }
}
