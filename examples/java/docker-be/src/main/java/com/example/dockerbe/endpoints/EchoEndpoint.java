package com.example.dockerbe.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("echo")
public class EchoEndpoint {
    
    @GetMapping("/uppercase")
    public String getUppercase(@RequestParam(value = "text", required = true) String text) {
        System.out.println("Echo endpoint was called");
        return text.toUpperCase();
    }
}
