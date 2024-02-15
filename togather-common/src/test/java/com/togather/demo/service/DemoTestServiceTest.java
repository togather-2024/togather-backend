package com.togather.demo.service;

import com.togather.demo.converter.DemoTestDtoConverter;
import com.togather.demo.dto.DemoTestDto;
import com.togather.demo.entity.DemoTestEntity;
import com.togather.demo.repository.DemoTestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemoTestServiceTest {
    @Spy
    private DemoTestDtoConverter demoTestDtoConverter;
    @Mock
    private DemoTestRepository demoTestRepository;
    @InjectMocks
    private DemoTestService demoTestService;

    @Test
    void getById_entityExists() {
        // given:
        when(demoTestRepository.findById(anyLong())).thenReturn(Optional.ofNullable( new DemoTestEntity()));

        // when:
        DemoTestDto result = demoTestService.getById(1L);

        // then:
        assertEquals(0L, result.getId());
        assertEquals(null, result.getFirstName());
        assertEquals(null, result.getLastName());
    }

    @Test
    void getById_entityNotExist() {
        // given
        when(demoTestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        // when + then:
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> {
                    demoTestService.getById(1L);
                }
        );

        // then:
        assertEquals("cannot find entity", exception.getMessage());
    }

}