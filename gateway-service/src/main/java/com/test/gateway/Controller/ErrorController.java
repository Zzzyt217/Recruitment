package com.test.gateway.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorController {

    @GetMapping("/error/401")
    public String unauthorized() {
        return "error/401";
    }

    @GetMapping("/error/404")
    public String notFound() {
        return "error/404";
    }
}
