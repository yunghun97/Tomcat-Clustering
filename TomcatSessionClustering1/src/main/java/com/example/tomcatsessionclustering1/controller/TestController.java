package com.example.tomcatsessionclustering1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("")
    public String test(){
        return "톰캣 클러스터1";
    }
    @GetMapping("/session")
    public String session(@RequestParam(name = "name") String name, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("이름 목록", name);
        return "여긴 세션 테스트"+ session.getAttribute("이름 목록").toString();
    }
}
