package com.codestates.coffee;

// TODO CoffeeController 에 Spring WebFlux 를 적용해 주세요. Spring MVC 방식 아닙니다!!

import com.codestates.utils.UriCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Validated
@RestController
@RequestMapping("/v12/coffees")
public class CoffeeController {
    private final CoffeeService coffeeService;
    private final CoffeeMapper coffeeMapper;

    public CoffeeController(CoffeeService coffeeService, CoffeeMapper coffeeMapper) {
        this.coffeeService = coffeeService;
        this.coffeeMapper = coffeeMapper;
    }

    @PostMapping
    public Mono<ResponseEntity> postCoffee(@Valid @RequestBody Mono<CoffeeDto.Post> requestBody) {
        return requestBody
                .flatMap(post -> coffeeService.createCoffee(coffeeMapper.coffeePostDtoToCoffee(post)))
                .map(createdCoffee -> {
                    URI location = UriCreator.createUri("/v12/coffees/", createdCoffee.getCoffeeId());
                    return ResponseEntity.created(location).build();
                });
    }
}
