package com.example.tomcatsessionclustering2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("")
    public String test(){
        return "톰캣 클러스터2";
    }
    @GetMapping("name")
    public String sessionName(HttpServletRequest request){
        HttpSession session = request.getSession();
        return "name : "+ session.getAttribute("이름 목록").toString();
    }
}
