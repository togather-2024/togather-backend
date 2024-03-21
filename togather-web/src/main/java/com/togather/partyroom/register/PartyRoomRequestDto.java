package com.togather.partyroom.register;

import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.model.PartyRoomImageType;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
public class PartyRoomRequestDto {
    private static int MAX_NUMBER_OF_TAG_INPUT_PER_PARTY_ROOM = 10;

    // PARTY ROOM FIELDS
    @NotBlank
    @Schema(description = "name of partyRoom")
    private String partyRoomName;

    @NotBlank
    @Schema(description = "description of partyRoom")
    private String partyRoomDesc;

    @Min(0)
    @Max(24)
    @Schema(description = "opening hour of partyRoom. Should be an integer between 0 -24")
    private int openingHour;

    @Min(0)
    @Max(24)
    @Schema(description = "closing hour of partyRoom. Should be an integer between 0 -24")
    private int closingHour;

    @Positive
    @Schema(description = "price of partyRoom per hour")
    private long price;

    @Positive
    @Schema(description = "the number of partyRoom guest capacity")
    private int guestCapacity;

    // TAG FIELDS
    @Schema(description = "list of tags of party room")
    private List<String> customTags = new ArrayList<>();

    // IMAGE FIELDS
    private String imageFileName;

    // LOCATION FIELDS
    private String sido;
    private String sigungu;
    private String roadName;
    private String roadAddress;
    private String jibunAddress;

    // OPERATION DAY FIELDS
    @NotEmpty
    @DayList
    @Schema(description = "list of operation days of party room. Each string should be one of MONDAY,TUESDAY,... ,SUNDAY")
    private List<String> operationDays;


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

    public PartyRoomLocationDto extractPartyRoomLocationDto() {
        return PartyRoomLocationDto.builder()
                .sido(this.sido)
                .sigungu(this.sigungu)
                .roadName(this.roadName)
                .roadAddress(this.roadAddress)
                .jibunAddress(this.jibunAddress)
                .build();
    }

    public PartyRoomImageDto extractPartyRoomImageDto() {
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