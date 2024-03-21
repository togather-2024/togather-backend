package com.togather.partyroom.payment;

import com.togather.common.AddJsonFilters;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.payment.model.PaymentDto;
import com.togather.partyroom.payment.model.PaymentSuccessDto;
import com.togather.partyroom.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.togather.common.ResponseFilter.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Toss Payments")
public class PaymentController {

    private final MemberService memberService;
    private final PaymentService paymentService;

    @PostMapping("/toss")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Saving Information for Toss Payments", description = "토스 결제를 위한 정보 저장 API")
    @AddJsonFilters(filters = {PAYMENT_INFO})
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    public MappingJacksonValue saveRequiredData(@Valid @RequestBody PaymentDto.Request paymentDto) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        PaymentDto responsePaymentDto = paymentService.saveRequiredData(paymentDto, loginUser);

        return new MappingJacksonValue(responsePaymentDto);
    }

    @PostMapping("/toss/success")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Approval and Verification After Successful Payment for Toss Payments", description = "결제 완료 후 토스 결제 승인 및 결제 정보 확인 API")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    public ResponseEntity<PaymentSuccessDto> verifySuccessfulTossPayment(@RequestParam String paymentKey,
                                                          @RequestParam String orderId,
                                                          @RequestParam long amount) {
        PaymentSuccessDto paymentSuccessDto = paymentService.verifySuccessfulTossPayment(paymentKey, orderId, amount);

        return ResponseEntity.ok(paymentSuccessDto);
    }

}
