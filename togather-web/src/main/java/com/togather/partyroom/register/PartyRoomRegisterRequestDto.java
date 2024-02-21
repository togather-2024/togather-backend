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
    private static int MAX_NUMBER_OF_TAG_INPUT_PER_PARTY_ROOM = 10;

    // PARTY ROOM FIELDS
    @NotBlank
    private final String partyRoomName;

    @NotBlank
    private final String partyRoomDesc;

    @Min(0) @Max(24)
    private final int openingHour;

    @Min(0) @Max(24)
    private final int closingHour;

    @Positive
    private final long price;

    @Positive
    private final int guestCapacity;

    // TAG FIELDS
    private final List<String> customTags = new ArrayList<>();

    // IMAGE FIELDS
    private final String imageFileName;

    // LOCATION FIELDS
    private final String sido;
    private final String sigungu;
    private final String roadName;
    private final String roadAddress;
    private final String jibunAddress;

    // OPERATION DAY FIELDS
    @NotEmpty @DayList
    private final List<String> operationDays;

    public PartyRoomDto extractPartyRoomDto() {
        return PartyRoomDto.builder()
                .partyRoomName(this.partyRoomName)
                .partyRoomDesc(this.partyRoomDesc)
                .partyRoomViewCount(0L)
                .openingHour(this.openingHour)
                .closingHour(this.closingHour)
                .price(this.price)
                .guestCapacity(this.guestCapacity)
                .build();
    }

    public List<PartyRoomCustomTagDto> extractCustomTags() {
        return this.customTags.stream().map(
                customTag -> PartyRoomCustomTagDto.builder()
                        .tagContent(customTag)
                        .tagCount(1L)
                        .build()
                )
                .limit(MAX_NUMBER_OF_TAG_INPUT_PER_PARTY_ROOM)
                .collect(Collectors.toList());
    }

    public PartyRoomLocationDto extractLocationDto() {
        return PartyRoomLocationDto.builder()
                .sido(this.sido)
                .sigungu(this.sigungu)
                .roadName(this.roadName)
                .roadAddress(this.roadAddress)
                .jibunAddress(this.jibunAddress)
                .build();
    }

    public PartyRoomImageDto extractMainImageDto() {
        return PartyRoomImageDto.builder()
                .imageFileName(this.imageFileName)
                .partyRoomImageType(PartyRoomImageType.MAIN)
                .build();
    }

    public List<PartyRoomOperationDayDto> extractOperationDays() {
        return this.operationDays.stream()
                .map(DayOfWeek::valueOf)
                .map(
                        dayOfWeek -> PartyRoomOperationDayDto.builder()
                                .operationDay(dayOfWeek)
                                .build()
                ).collect(Collectors.toList());
    }
}
