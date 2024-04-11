package com.togather.partyroom.payment;

import com.togather.common.AddJsonFilters;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.payment.model.PaymentCancelDto;
import com.togather.partyroom.payment.model.PaymentDto;
import com.togather.partyroom.payment.model.PaymentFailDto;
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
import org.springframework.web.bind.annotation.*;

import static com.togather.common.ResponseFilter.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Toss Payments")
@RequestMapping("/toss")
public class PaymentController {

    private final MemberService memberService;
    private final PaymentService paymentService;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Saving Information for Toss Payments", description = "토스 결제를 위한 정보 저장 API")
    @AddJsonFilters(filters = {PAYMENT_INFO})
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    public MappingJacksonValue saveRequiredData(@Valid @RequestBody PaymentDto.Request paymentDto) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        PaymentDto responsePaymentDto = paymentService.saveRequiredData(paymentDto, loginUser);

        return new MappingJacksonValue(responsePaymentDto);
    }

    @GetMapping("/success")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Approval and Verification After Successful Payment for Toss Payments", description = "결제 완료 후 토스 결제 승인 및 결제 정보 확인 API")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentSuccessDto.class)))
    @AddJsonFilters(filters = {PAYMENT_INFO_EXCLUDE_HANDLING_URL})
    public MappingJacksonValue approvePayment(@RequestParam String paymentKey,
                                                            @RequestParam String orderId,
                                                            @RequestParam long amount) {
        PaymentSuccessDto paymentSuccessDto = paymentService.handleSuccessTossPayment(paymentKey, orderId, amount);

        return new MappingJacksonValue(paymentSuccessDto);
    }

    @GetMapping("/fail")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Toss Payments Failure Information", description = "결제 실패 정보 리턴 API")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentFailDto.class)))
    public ResponseEntity<PaymentFailDto> failPayment(@RequestParam String code,
                                                      @RequestParam String message,
                                                      @RequestParam String orderId) {

        PaymentFailDto paymentFailDto = paymentService.handleFailedTossPayment(code, message, orderId);

        return ResponseEntity.ok(paymentFailDto);
    }

    @GetMapping("/my/{paymentKey}")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Payment Inquiry", description = "결제 조회 API")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @AddJsonFilters(filters = {PAYMENT_INFO_EXCLUDE_HANDLING_URL})
    public MappingJacksonValue inquiryPayment(@PathVariable(name = "paymentKey") String paymentKey) {
        PaymentDto paymentDto = paymentService.inquiryPayment(paymentKey);

        return new MappingJacksonValue(paymentDto);
    }

    @PostMapping("/cancel/{paymentKey}")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Payment Cancel", description = "결제 취소 API")
    public ResponseEntity<String> cancelPayment(@PathVariable(name = "paymentKey") String paymentKey,
                                                    @RequestBody PaymentCancelDto.Request paymentCancelDto) {
        String cancelReason = paymentService.cancelPayment(paymentKey, paymentCancelDto);

        return ResponseEntity.ok(cancelReason);
    }
}
