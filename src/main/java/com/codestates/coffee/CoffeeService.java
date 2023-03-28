package com.codestates.coffee;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

// TODO CoffeeService 에 Spring WebFlux 를 적용해 주세요. Spring MVC 방식 아닙니다!!

@Transactional
@Service
public class CoffeeService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public CoffeeService(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<Coffee> createCoffee(Coffee coffee) {
        return r2dbcEntityTemplate.insert(coffee)
                .map(savedCoffee -> {
                    return savedCoffee;
                });
    }
}
