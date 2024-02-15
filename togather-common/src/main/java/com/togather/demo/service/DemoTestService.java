package com.togather.demo.service;


import com.togather.demo.converter.DemoTestDtoConverter;
import com.togather.demo.dto.DemoTestDto;
import com.togather.demo.repository.DemoTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoTestService {

    private final DemoTestDtoConverter demoTestDtoConverter;
    private final DemoTestRepository demoTestRepository;

    public DemoTestDto getById(Long id) {
        return demoTestDtoConverter.convertDtoByEntity(demoTestRepository.findById(id).orElseThrow(() -> new RuntimeException("cannot find entity")));
    }
}
