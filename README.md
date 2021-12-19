최근 진행하는 토이 프로젝트의 API 서버는 서로 독립된 프로젝트 2개로 이루어져있었다.

-   인증 서버
-   어플리케이션 서버

처음에는 기능 구현 자체에 초점을 맞추고 각자의 프로젝트 크기도 크지 않아서 불편함을 느끼지 못했다. 하지만 프로젝트를 진행하면서 크기가 커지면서 `domain` 을 각각 프로젝트에 선언했기 때문에 동일성을 보장하기 위해 똑같은 작업을 두개의 `Applicaton`에 작업을 해줘야했다. 또한 공통적인 `Repository`도 각 `Application`에서 작성해줘야하는게 여간 번거로운게 아니었고, 똑같은 코드가 생긴다는게 썩 마음에 들지 않았다.

그리고 알아보던중 **멀티 모듈(Multi Module)**에 대해 알게 되었다.

이번 글에서는 **멀티 모듈 개념/예제**에 대해 알아보자.

---

# 멀티 모듈 이란?

**멀티 모듈**이란 서로 독립적인 프로젝트(`인증`, `어플리케이션`)를 하나의 프로젝트로 묶어 **모듈**로서 사용되는 구조를 말한다.

멀티 모듈을 사용하면 공통적인 기능을 모아 하나의 모듈로 만드는 것이 가능하다. 즉, 인증과 어플리케이션에서 공통으로 사용하는 `util`, `domain`, `Repository`등을 모듈로 분리해 사용할 수 있는 것이다.

멀티 모듈에 관련해 더 자세하게 알고 싶은 경우 아래 글을 참고하길 바란다.

[https://techblog.woowahan.com/2637/](https://techblog.woowahan.com/2637/)

---

# 멀티 모듈 간단 예제

이제 간단하게 Gradle을 사용해 멀티 모듈 프로젝트를 만들어보자.

먼저 모듈들을 모을 Gradle 프로젝트를 하나 만들어주자.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/0ca7b999-3a1b-45cc-ab08-55d9d986ab46/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T122903Z&X-Amz-Expires=86400&X-Amz-Signature=cbc1a621c18eb1396392810ec0748aea2d97dee6887b9c1c3fecf1f214b7b5e8&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

MacOS IntelliJ 기준 File→New→Project 경로에 위의 사진처럼 Gradle 프로젝트를 하나 생성한다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/a23b44ef-d75a-4c57-8e00-6972deda3ef0/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T122919Z&X-Amz-Expires=86400&X-Amz-Signature=0f0bac475c7828f7430840403ad34ca0b99c475ef37dc6b48ed723d1a49ae0bd&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

그럼 위와 같은 구조의 프로젝트가 생성된다. 일단 모듈을 담을 프로젝트이기때문에 src폴더가 필요없기 때문에 **src폴더는 삭제해주자** 그리고 `build.gradle` 아래와 같이 입력한다.

```
buildscript {
    ext {
        springBootVersion = '2.4.3'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath "io.spring.gradle:dependency-management-plugin:1.0.11.RELEASE"
    }
}

allprojects {}

subprojects {
    apply plugin: 'java'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

    group = 'kr.multi.ex'
    version = '1.0'
    sourceCompatibility = '11'

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly 'org.projectlombok:lombok:1.18.16'
        annotationProcessor 'org.projectlombok:lombok:1.18.16'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testImplementation('org.springframework.boot:spring-boot-starter-test') {
            exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
        }
    }
}
```

**위의 큰 3개의 Closure를 간단하게 설명하면 다음과 같다**

`buildscript` : `Gradle` 이 빌드되기전 실행되는 설정

`allprojects` : 현재의 `root` 프로젝트와 앞으로 추가될 `서브 모듈`에 대한 설정

`subprojects` : 전체 `서브 모듈` 에 해당되는 설정

이제 모듈들을 생성해보자. 모듈은 `core`, `api` 를 생성해보자.

`core`는 `spring-boot-starter` 의존성을 가지고 있고 이것을 `api`가 사용할 수 있게 해보자.

`root` 폴더를 우클릭 → New → Module을 클릭해서 Gradle Module을 생성해주자

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/96d31597-b58a-4e3a-8d9d-39b4c1c7ac89/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123009Z&X-Amz-Expires=86400&X-Amz-Signature=f9edb59a1dfa6649a0d05a3a4f82decbe422d7ca22ff68f640e9ea8fc5e69e0f&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/be82c7cc-864c-4bec-b978-1326cbb0cfed/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123027Z&X-Amz-Expires=86400&X-Amz-Signature=cc2fee3f45980c766de00a8f614ca293eb31d006cff8b2f2a17a22e7621d0385&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/cd625ba1-10c9-4780-8a4b-08863812304e/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123037Z&X-Amz-Expires=86400&X-Amz-Signature=99a7e337348b104e4b17526dc4fd829f7f72497e574e2240c2c6b8c3515e7c01&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

생성하면 다음가 같은 폴더 구조가 될 것이다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/41148b58-f16c-4f43-91bb-475a06543585/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123049Z&X-Amz-Expires=86400&X-Amz-Signature=c6f7ac46dfae8afd3d5dd9c230a2b11f01a74a3926bbcff9eda599e72a17aef8&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

이제 `settings.gradle`을 보면 다음과 같이 방금 생성한 `core`가 `include` 된것을 확인할 수 있다. 이것은 하위 모듈로 선언한다는 의미이다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/086fcc83-48a0-4d94-88cc-6e0f5eab197a/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123100Z&X-Amz-Expires=86400&X-Amz-Signature=e9aa129539ea8d8d0939eab30030a7171564b8ad75a8a196175825e386d67c6e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

IntelliJ의 Gradle Tab에서도 보면 다음과 같이 `core`가 `root` 하위에 존재하는 것을 확인할 수 있다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/fd3b327a-d4d0-4ac4-a2e5-ec09bf2c4fc6/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123114Z&X-Amz-Expires=86400&X-Amz-Signature=c5bd2d7f68f8e5561bc4526eab13b3f69567de0ee4440020cc96c2890b521037&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

`core`의 `build.gradle` 에 아래처럼 의존성만 추가하자

```
//core build.gradle
dependencies {
    compile 'org.springframework.boot:spring-boot-starter-web:2.6.1'
}
bootJar {
    enabled = false
}

jar {
    enabled = true
}
```

여기서 중요한 것은 `bootJar`와 `jar`다 `bootJar`은 실행가능한 `jar`를 만들려 하기 때문에 `main()`이 필요하다 그렇기 때문에 `main()`이 없는 `core`는 `enabled`를 `false`로 해줘야하고 결론적으로 저걸 넣지 않으면 추후 `core`에 있는 `Bean Class`를 다른 모듈에서 사용할 때 에러가 발생할 수 있다.

그리고 의존성을 추가할 때 `implementation` 대신 `compile`을 사용했는데 그 이유에 대해서는 밑에서 설명하겠다

이제 `gradle`을 갱신하면 `root`의 `build.gradle subprojects`에 선언한 `lombok`과 `spring-boot-starter`가 추가된 것을 확인할 수 있다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/cd791515-cb77-4d13-b3df-550cd9afd5ba/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123129Z&X-Amz-Expires=86400&X-Amz-Signature=1128130512bb8689dc85a971576cb941ad6fbac8e0cede93db675d94365a7b2e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

바로 `api` 모듈도 `core`가 똑같은 과정을 통해 만들면 다음과 같은 구조가 될 것이다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/eafd8061-1636-49a2-9d34-ce6bfe5b56fc/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123140Z&X-Amz-Expires=86400&X-Amz-Signature=87f5af5a6b831d7c6fb255071a4d635cfeb250fd0011406b11929e8e8cec08af&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

`api`의 `build.gradle`은 다음과 같이 **아무런 의존성을 넣지 않았다.**

```
//api build.gradle
dependencies {}
```

이제 `root`의 `build.gradle` 맨 아래에 다음을 추가해보자.

```
project(':api') {
    dependencies {
        implementation project(':core')
    }
}
```

`api` 모듈에 `core`의 의존성을 추가하라는 의미이다.

갱신해보면 아래처럼 `api`에 `spring-boot-starter` 추가된 것을 확인할 수 있다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/7999d5ce-3208-491f-be9b-f28b4bcb0817/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123155Z&X-Amz-Expires=86400&X-Amz-Signature=072f4a69e8300ae959422aa2ee58dd748dc7d64c64c9ed51dd3d411116db1a81&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

이제 `api`에서 `@SpringBootApplication`을 선언했을 때 정상적으로 사용할 수 있다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/1a7faefa-6fb8-4ba4-87c3-b1f97c23697f/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123206Z&X-Amz-Expires=86400&X-Amz-Signature=60b441df4d10ba2713a27d9934f49ec4d5440d76d9168c53cb5820e188e1a165&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

`core`에 `Bean Class`를 만들고 `api`에서 호출하는 예제는 다음과 같다. 간단하게 `@Service` `Bean을` 만들어 테스트한 코드다.

```
//core 모듈
@Service
public class TestService {
    public String test() {
        return "core의 Bean Class 테스트";
    }
}

//api 모듈
@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    @GetMapping("/test")
    public String test() {
        return testService.test();
    }
}
```

구조로 보면 다음과 같다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/abc43317-277b-4729-a0d1-2a521f38c633/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123224Z&X-Amz-Expires=86400&X-Amz-Signature=96979082258b55521d7547f0db4c294210554bcbcf28f5bf1e7f13495fb063dd&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

이제 `api` 를 실행후 `/test`를 호출하면 아래의 결과를 볼 수 있다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/65df4083-20ed-4879-8479-ef0118f824b9/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123234Z&X-Amz-Expires=86400&X-Amz-Signature=1553563d109580f0c199a05c112db94810909148de00d9ddb67bd2a685480f9b&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

여기서 주의할 점은 `core`와 `api`의 패키지 구조다. 현재 상위 패키지가 `kr.multi.ex`로 통일 된 것을 볼 수있는데 이렇게 통일하지 않으면 api에서 core의 bean을 읽어오지 못해 에러가 발생한다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/2b0016b1-daab-484b-81fe-03cd14f1b31f/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123245Z&X-Amz-Expires=86400&X-Amz-Signature=e395b61b0e73d3838318f9c3c87c9381b5e9a488e80021f99a0780ed7897e079&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

만약 위처럼 `api`의 패키지를 `kr.multi.ex2`로 변경하면 패키지가 공통이 되지 않기 때문에 아래와 같은 에러를 호출한다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/71b591b7-d615-4e8c-a953-d8fe060d1535/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123327Z&X-Amz-Expires=86400&X-Amz-Signature=6f26ce68570d6196dcc1b102da645dba0fd07881da7ae3107680fa351c980ec6&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

좀 더 정확히 말하면 `Application` 클래스의 위치한 곳의 패키지가 공통으로 맞춰져 있어야한다. 만약 패키지를 못맞춘다면 아래와 같이 `scanBasePackages` 옵션을 통해 `Bean`을 스캔할 수 있다.

```
@SpringBootApplication(scanBasePackages = "kr.multi")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }
}
```

> **core의 의존성을 compile로 선언한 이유**

위에서 `core` 의존성을 추가할 때 `compile`을 사용했는데 그 이유는 `implementation`은 직접 의존하는 모듈(`core`)외에서는 사용할 수 없기 때문이다.

즉 `core`의 `spring-boot-starter`를 `implementation`으로 선언할 경우 `api`에서는 `spring-boot-starter`를 가져오지 않는다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/6e2d3564-fc23-462e-b1db-6644673f8b5a/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20211219%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211219T123349Z&X-Amz-Expires=86400&X-Amz-Signature=f9b1b09499d4ecc5061ac62e65672dca99d19bbceed73d53e5fb2247a2a64973&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

위처럼 `core`의 의존성을 `implementation`으로 할 경우 `api`에서는 못가져오는 것을 볼 수 있다.

하지만 `compile`은 `gradle`에서 권장되지 않는 방식이다. 심지어 현재는 예제의 `gradle`은 `6.x`버전이라 `compile`을 사용할 수 있지만 `7.0` 버전 이상부터는 `compile`을 사용할 수 없다.

### **그럼 어떻게 해야하나?**

`7.0`버전 이상부터는 `api` 키워드를 사용할 수 있다. `api`는 `compile`이랑 비슷한 키워드라 생각하면 된다.

만약 `gradle` 버전을 올리고 싶다면 `root` 프로젝트 위치에서 해당 명령어를 통해 `gradle` 버전을 변경할 수 있다.

```
$ ./gradlew wrapper --gradle-version=7.x
```

`api`를 사용하기 위해서는 `root` `build.gradle`의 `subprojects`의 다음을 수정해준다.  
`apply plugin:’java’` → `apply plugin:’java-library’`

그리고 다음처럼 의존성을 추가하면 된다.

```
//core build.gradle
dependencies {
    api 'org.springframework.boot:spring-boot-starter-web:2.6.1'
}
bootJar {
    enabled = false
}

jar {
    enabled = true
}
```

---

> 비고

[https://kwonnam.pe.kr/wiki/gradle/multiproject](https://kwonnam.pe.kr/wiki/gradle/multiproject)

[https://velog.io/@sangwoo0727/Gradle을-이용한-멀티-모듈](https://velog.io/@sangwoo0727/Gradle%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88)

[https://wellbell.tistory.com/253#comment16473032](https://wellbell.tistory.com/253#comment16473032)