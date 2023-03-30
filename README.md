[블로그 포스팅 주소](https://velog.io/@wish17/%EC%BD%94%EB%93%9C%EC%8A%A4%ED%85%8C%EC%9D%B4%EC%B8%A0-%EB%B0%B1%EC%97%94%EB%93%9C-%EB%B6%80%ED%8A%B8%EC%BA%A0%ED%94%84-71%EC%9D%BC%EC%B0%A8-Spring-WebFlux)

# Spring WebFlux

- Spring WebFlux는 Spring 5부터 지원하는 리액티브 웹 애플리케이션을 위한 웹 프레임워크다.

- Spring WebFlux는 Spring MVC 방식의 ``@Controller``, ``@RestController``, ``@RequestMapping`` 등과 같은 애너테이션을 동일하게 지원한다.

- Spring WebFlux는 1차로 요청을 수신한 애플리케이션에서 외부 애플리케이션에 요청을 추가적으로 전달할 때 1차로 요청을 수신한 애플리케이션의 요청 처리 쓰레드가 Blocking 되지 않는다.


> WebFlux
- Reactor의 타입인 Flux가 Web에서 사용되는 것

### Spring WebFlux 애플리케이션 vs Spring MVC 애플리케이션

#### 기술 스택 비교

<table><thead><tr><th>Reactive Stack</th><th>Servlet Stack</th></tr></thead><tbody><tr><td> Netty, Servlet 3.1+ Containers</td><td>Servlet Containers</td></tr><tr><td> Reactive Streams Adapters</td><td>Servlet API</td></tr><tr><td> Spring Security Reactive</td><td>Spring Security</td></tr><tr><td> Spring WebFlux</td><td>Spring MVC</td></tr><tr><td>Spring Data Reactive Repositories - Mongo, Cassandra, Redis, Couchbase, R2DBC</td><td>Spring Data Repositories - JDBC, JPA,NoSQL</td></tr></tbody></table>

1. Non-Blocking

	- Spring WebFlux의 경우 **Non-Blocking 통신**을 지원
	- Spring MVC의 경우 Non-Blocking이 아닌 Blocking 통신 방식을 사용

2. 유연함
	- Spring WebFlux의 경우 Reactive Adapter를 사용해서 Reactor 뿐만 아니라 RxJava 등의 다른 리액티브 라이브러리를 사용할 수 있는 **유연함**을 제공 
	- Spring MVC의 경우 **Servlet API의 스펙에 의존적**이다.

3. Spring Security
	- Spring WebFlux의 경우 서블릿 필터 방식이 아닌 WebFilter를 사용해 리액티브 특성에 맞게 인증과 권한 등의 보안을 적용한다.
	- Spring WebFlux와 Spring MVC 모두 보안을 적용하기 위해서 Spring Security를 사용한다.

4. 웹 스택
	- Reactive Stack의 경우, 웹 계층(프리젠테이션 계층, API 계층)에서는 Spring WebFlux를 사용
	-  Servlet Stack의 경우, Spring MVC를 사용한다.

5. Spring WebFlux의 경우 완전한 Non-Blocking 통신을 위해 리액티브 스택을 데이터 액세스 계층까지 확장한다.

> R2DBC(Reactive Relation Database Connectivity)
- 관계형 데이터베이스에 Non-Blocking 통신을 적용하기 위한 표준 사양(Specification)이다.
- MySQL, Oracle 등의 데이터베이스 벤더에서는 R2DBC 사양에 맞는 드라이버를 구현해서 공급한다.

#### 벤 다이어그램을 통한 비교

[![](https://velog.velcdn.com/images/wish17/post/a2389603-83ed-41ad-9c3a-d9bf38ba95b7/image.png)](https://docs.spring.io/spring-framework/docs/5.2.5.RELEASE/spring-framework-reference/web-reactive.html#webflux-framework-choice)

***

## 리액티브한 샘플 애플리케이션 구현

[풀코드 Github 주소](https://github.com/wish9/Practice-webflux/commit/34d8b2f083cb584f272e0e24aff0b24b39db73ca)


- Spring WebFlux에서는 모든 데이터가 Mono나 Flux로 래핑되어 전달된다.

- R2DBC는 Spring Data JDBC나 Spring Data JPA 처럼 애너테이션이나 컬렉션 등을 이용한 연관 관계 매핑은 지원하지 않는다.
    - 연관 관계 매핑이 적용되는 순간 내부적으로 Blocking 요소가 포함될 가능성이 있기 때문


``error()`` Operator
- Exception을 throw하는데 사용하는 Operator

``then()`` Operator
- 이 전에 동작하고 있던 Sequence를 종료하고 새로운 Sequence를 시작하게 해주는 Operator
    
``switchIfEmpty()`` Operator
- emit되는 데이터가 없다면 switchIfEmpty() Operator의 파라미터로 전달되는 Publisher가 대체 동작을 수행할 수 있게 해주는 Operator

```java
private Mono<Member> findVerifiedMember(long memberId) {
    return memberRepository
            .findById(memberId)
            .switchIfEmpty(Mono.error(new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)));
    // switchIfEmpty() Operator는 emit되는 데이터가 없다면 switchIfEmpty() Operator의 파라미터로 전달되는 Publisher가 대체 동작을 수행할 수 있게 해주는 Operator다.
}
```

``zipWith()`` Operator
- ``zipWith()``를 호출하는 Mono와 ``zipWith()``의 파라미터로 주어지는 Mono에서 emit하는 두 개의 데이터를 ``Tuple2`` 객체로 결합한 후, Downstream에 전달하는 Operator

```java
@Transactional(readOnly = true)
public Mono<Page<Member>> findMembers(PageRequest pageRequest) {
    return memberRepository.findAllBy(pageRequest)
            .collectList()     // List 객체로 변환
            .zipWith(memberRepository.count())
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));
}
```

### 핵심 포인트

- Spring 리액티브 스택의 경우, H2 웹 콘솔을 정상적으로 지원하지 않는다.
    - [Spring WebFlux 환경에서 H2 웹 콘솔을 설정하는 방법](https://itvillage.tistory.com/53)

- Spring Data R2DBC는 Auto DDL 기능을 제공하지 않기 때문에 직접 SQL 스크립트 설정을 추가해야 한다.

- R2DBC의 Reposiroty를 사용하기 위해서는 Configuration 클래스에 ``@EnableR2dbcRepositories`` 애너테이션을 추가해 주어야 한다.

- Auditing 기능을 사용하기 위해서는 Configuration 클래스에 ``@EnableR2dbcAuditing`` 애너테이션을 추가해 주어야한다.
    - Auditing 기능 = 데이터베이스에 엔티티가 저장 및 수정 될 때, 생성 날짜와 수정 날짜를 자동으로 저장해주는 기능 등을 지원하는 기능

- request body를 Mono 타입으로 전달 받을 경우, Blocking 요소가 포함되지 않도록 request body를 전달 받는 순간부터 Non-Blocking으로 동
작하도록 Operator 체인을 바로 연결해서 다음 처리를 시작할 수 있다.

- **Spring WebFlux 기반 클래스는 Mono와 같이 Mono로 래핑한 값을 리턴한다.**

- **Spring WebFlux에서는 모든 데이터가 Mono나 Flux로 래핑되어 전달된다.**

***

## Spring WebFlux 적용 실습

[실습 풀코드 Github 주소](https://github.com/wish9/Practice-webflux2/commit/dcf8c906a6314073e5d9ae91cd193b081b8b89ca)

- CoffeeController의 postCoffee() 핸들러 메서드에 Spring WebFlux를 적용

- CoffeeService의 createCoffee() 메서드에 Spring WebFlux를 적용

- 데이터베이스에 커피 정보를 등록
    - ``R2dbcEntityTemplate``을 사용해서 커피 정보를 데이터베이스에 등록
