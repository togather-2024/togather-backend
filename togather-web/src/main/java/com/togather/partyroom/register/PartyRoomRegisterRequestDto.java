package com.togather.partyroom.register;

import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.model.PartyRoomImageType;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PartyRoomRegisterRequestDto {
    private final PartyRoomDto partyRoomDto;
    private final List<PartyRoomCustomTagDto> customTags;
    private final PartyRoomImageDto partyRoomImageDto;
    private final PartyRoomLocationDto partyRoomLocationDto;
    private final List<PartyRoomOperationDayDto> operationDays;
}
