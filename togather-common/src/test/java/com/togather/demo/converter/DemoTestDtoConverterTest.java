package com.togather.demo.converter;

import com.togather.demo.dto.DemoTestDto;
import com.togather.demo.entity.DemoTestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DemoTestDtoConverterTest {

    @InjectMocks
    private DemoTestDtoConverter demoTestDtoConverter;

    @Test
    void convertDtoByEntity_entityIsNull() {
        DemoTestDto result = demoTestDtoConverter.convertDtoByEntity(null);
        assertEquals(result, null);
    }

    @Test
    void convertDtoByEntity_entityIsNotNull() {
        DemoTestDto result = demoTestDtoConverter.convertDtoByEntity(new DemoTestEntity());
        assertEquals(0, result.getId());
        assertEquals(null, result.getFirstName());
        assertEquals(null, result.getLastName());
    }

}