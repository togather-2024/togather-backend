package com.togather.demo.converter;

import com.togather.demo.dto.DemoTestDto;
import com.togather.demo.entity.DemoTestEntity;
import org.springframework.stereotype.Component;

@Component
public class DemoTestDtoConverter {

    public DemoTestDto convertDtoByEntity(DemoTestEntity demoTestEntity) {
        if (demoTestEntity == null) {
            return null;
        }

        return DemoTestDto.builder()
                .id(demoTestEntity.getId())
                .firstName(demoTestEntity.getFirstName())
                .lastName(demoTestEntity.getLastName())
                .build();
    }

    public DemoTestEntity convertEntityByDto(DemoTestDto dto) {
        // NOTE: did not implement because no usage
        return new DemoTestEntity();
    }
}