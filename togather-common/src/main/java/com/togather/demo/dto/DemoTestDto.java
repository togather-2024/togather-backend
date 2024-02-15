package com.togather.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DemoTestDto {
    private long id;
    private String firstName;
    private String lastName;
}
