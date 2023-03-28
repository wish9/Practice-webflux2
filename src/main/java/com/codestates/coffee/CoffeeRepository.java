package com.codestates.coffee;

import com.codestates.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//public class CoffeeRepository extends R2dbcRepository<Coffee, Long> {
//    Mono<Member> findByEmail(String email);
//    Flux<Member> findAllBy(Pageable pageable);
//}
