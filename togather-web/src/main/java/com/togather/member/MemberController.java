package com.togather.member;

import com.togather.common.response.AddJsonFilters;
import com.togather.common.response.ResponseFilter;
import com.togather.member.dto.LoginDto;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.security.jwt.JwtFilter;
import com.togather.security.service.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member CRUD")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    @PostMapping("/join")
    @Operation(summary = "Member Registration API", description = "회원 등록 API")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    public ResponseEntity<MemberDto> join(@RequestBody MemberDto memberDto) {
        memberService.register(memberDto);
        return ResponseEntity.ok(memberDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Member Login API", description = "로그인 API")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        // When invalid email/password is input, authenticate method throws exception, and is handled in filter
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Login success, so add authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        // Response token in body and header to client
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/getUserInfo")
    @Operation(summary = "get info of user logged in", description = "로그인한 사용자 정보 조회 API")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @AddJsonFilters(filters = ResponseFilter.MEMBER_DTO_EXCLUDE_PII_WITH_NAME)
    public MappingJacksonValue getUserInfo() {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return new MappingJacksonValue(loginUser);
    }

    @PatchMapping("/update/name")
    @Operation(summary = "Member Name Update API", description = "회원 이름 변경 API")
    @ApiResponse(responseCode = "200", description = "success")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    public ResponseEntity<String> updateName(@RequestParam(name = "name") String name) {
        memberService.updateName(name);
        return ResponseEntity.ok("ok");
    }

    @PatchMapping("/update/password")
    @Operation(summary = "Member Password Update API", description = "회원 비밀번호 변경 API")
    @ApiResponse(responseCode = "200", description = "success")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    public ResponseEntity<String> updatePassword(@RequestBody MemberDto.UpdatePassword updatePassword) {
        memberService.updatePassword(updatePassword);
        return ResponseEntity.ok("ok");
    }
}
