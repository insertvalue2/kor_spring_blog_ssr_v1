package org.example.demo_ssr_v1_1.payment;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.user.User;
import org.example.demo_ssr_v1_1.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;


    // 1. 사전 결제 요청
    // 프론트엔트가 결제창을 띄우기 전에, 서버에서 먼저 고유한 '주문번호(merchantUid) 를
    // 생성해서 내려 주기 위힘 (중복 결제 방지, 금액 위변조 방지)
    @Transactional
    public PaymentResponse.PrepareDTO 결제요청생성(Long userId, Integer amount) {
        if(!userRepository.existsById(userId)) {
            throw new Exception404("사용자를 찾을 수 없습니다");
        }

        // 주문 번호 생성 (UUID 사용, 중복 시 재 생성 로직 추가)
        String merchantUid = generateMerchantUid(userId);
        while (paymentRepository.existsByMerchantUid(merchantUid)) {
            merchantUid = generateMerchantUid(userId);
        }

        return new PaymentResponse.PrepareDTO(merchantUid, amount, impKey);
    }

    // 주문번호 생성 유틸리티
    // 형식 : point_{userId}_{timestamp}_{uuid}
    private String generateMerchantUid(Long userId) {
        return "point_" + userId + "_"
                + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public void 결제검증및충전(Long userId, String impUid, String merchantUid) {

        // 실제 UserId 로 사용자가 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        // 중복 지급 방지
        if (paymentRepository.findByImpUid(impUid).isPresent()) {
            throw new Exception400("이미 처리된 결제 입니다");
        }

        // 위변조 방지 때문에 검증 (500원 결제 --> 500만원 포인트 충전을 막아야 한다)
        // 외부 통신 시작 ( 인증서버 --> JWT --> 포트원 자원 서버에 조회 요청)

        // 임시 1 -
        포트원액세스토큰발급();

    }

    private String 포트원액세스토큰발급() {
        try {
            // https://api.iamport.kr/users/getToken
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 메세지 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 메세지 바디 생성
            Map<String, String> body = new HashMap<>();
            // 포트원에서 발급 받았던 REST API KEY
            body.put("imp_key", impKey);
            body.put("imp_secret", impSecret);

            // 헤더 + body 결합
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 통신 요청
            ResponseEntity<PaymentResponse.PortOneTokenResponse> response = restTemplate.exchange(
                    "https://api.iamport.kr/users/getToken",
                    HttpMethod.POST,
                    request,
                    PaymentResponse.PortOneTokenResponse.class
            );
            System.out.println("액세스 토큰 확인 ");
            System.out.println(response.getBody().getResponse().getAccessToken());
            System.out.println("response : " + response);

        } catch (Exception e) {
            throw new Exception400("포트원 인증실패: 관리자 설정을 확인하세요");
        }

        // TODO - 반드시 응답 값 수정
        return  null;
    }

}


