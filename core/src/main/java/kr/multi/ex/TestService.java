package kr.multi.ex;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String test() {
        return "core의 Bean Class 테스트";
    }
}
