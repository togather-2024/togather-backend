package com.togather.demo;

import com.togather.demo.dto.DemoTestDto;
import com.togather.demo.service.DemoTestService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Hidden
public class DemoTestController {

    private final DemoTestService demoTestService;


    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello/{id}")
    public String helloUser(@PathVariable("id") long id) {
        try {
            DemoTestDto dto = demoTestService.getById(id);
            return String.format("Hello User: firstName: %s lastName: %s", dto.getFirstName(), dto.getLastName());
        } catch (RuntimeException runtimeException) {
            return "User Not Found";
        }
    }
}
