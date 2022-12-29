package com.likelionfinalproject1.Controller;


import com.likelionfinalproject1.Service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SwaggerTestController {

    private final AlgorithmService algorithmService;

    @GetMapping("/hello")
    public String swaggerTest(){
        return "배지원";
    }

    @GetMapping("/{num}")
    public Integer divide(Integer num){
        return algorithmService.sumOfDigit(num);
    }
}
