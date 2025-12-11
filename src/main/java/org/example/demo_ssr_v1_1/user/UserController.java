package org.example.demo_ssr_v1_1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    // 로그인 화면 요청
    // http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    // JWT 토큰 기반 인증 x -> 세션 기반 인증 처리
    // 로그인 기능 요청
    // http://localhost:8080/login
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO) {
        // 1. 인증검사 x - 로그인 요청
        // 2. 유효성 검사
        // 3. db 에 사용자 이름과 비밀번호 확인
        // 4. 로그인 성공 또는 실패 처리
        // 5. 웹 서버는 바보라서 사용자의 정보를 세션 메모리에 저장 시켜야
        //    다음 번 요청이 오더라고 알 수 있어. - 세션 저장 처리

        return "redirect:/";
    }




    // 회원가입 화면 요청
    // http://localhost:8080/join
    @GetMapping("/join")
    public String joinFrom() {
        return "user/join-form";
    }

    // 회원가입 기능 요청
    // http://localhost:8080/join
    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {

        // 1. 인증검사 (x) - 필요 없음 (회원가입임)
        // 2. 유효성 검사 (엉망인 데이터를 저장할 수 없음)
        // 3. 사용자 이름 중복 체크
        // 4. 저장 요청
        joinDTO.validate();
        User existingUser = userRepository.findByusername(joinDTO.getUsername());
        if(existingUser != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다");
        }
        User user = joinDTO.toEntity();
        userRepository.save(user);

        return "redirect:/login";
    }

}
