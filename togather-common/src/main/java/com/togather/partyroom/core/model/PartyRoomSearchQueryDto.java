package com.togather.partyroom.core.model;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PartyRoomSearchQueryDto {
    private String sido;
    private String sigungu;
    private LocalDate date;
    private Integer guestCount;
    private List<String> keywords = new ArrayList<>();
    private int pageNum = 1;
    private int pageSize = 10;
}
