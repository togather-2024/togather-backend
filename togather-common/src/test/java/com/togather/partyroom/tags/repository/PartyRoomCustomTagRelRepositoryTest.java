package com.togather.partyroom.tags.repository;

import com.togather.config.jasypt.JasyptConfig;
import com.togather.config.yaml.YamlConfiguration;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.repository.PartyRoomRepository;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaRepositories
@ComponentScan
@EntityScan(basePackages = "com.togather")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {YamlConfiguration.class, JasyptConfig.class})
class PartyRoomCustomTagRelRepositoryTest {
    @Autowired
    private PartyRoomCustomTagRelRepository partyRoomCustomTagRelRepository;

    @Test
    @Disabled
    void findCustomTagsByPartyRoom2() {
        List<PartyRoomCustomTag> customTagsByPartyRoom = partyRoomCustomTagRelRepository.findCustomTagsByPartyRoom(create());
        assertEquals(3, customTagsByPartyRoom.size());
    }

    PartyRoom create() {
        return PartyRoom.builder().partyRoomId(4L).build();
    }

}