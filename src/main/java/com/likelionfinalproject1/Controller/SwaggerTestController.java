package com.likelionfinalproject1.Controller;


import com.likelionfinalproject1.Service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
@Slf4j
public class SwaggerTestController {

    private final AlgorithmService algorithmService;

    @GetMapping
    public String swaggerTest(){
        return "배지원";
    }

    @GetMapping("/{num}")
    public Integer divide(@PathVariable(name = "num") int num){
        log.info("num:"+num);
        return algorithmService.sumOfDigit(num);
    }


//    @GetMapping("/{num}")
//    public Integer divide(@PathVariable(name = "num") Integer num){
//        log.info("num:"+num);
//        return algorithmService.sumOfDigit(num);
//    }
}
