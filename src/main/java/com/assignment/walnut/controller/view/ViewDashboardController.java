package com.assignment.walnut.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "bluewalnut/dashboard";
    }
}
