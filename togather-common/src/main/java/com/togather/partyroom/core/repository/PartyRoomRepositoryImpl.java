package com.togather.partyroom.core.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomSearchQueryDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.togather.partyroom.core.model.QPartyRoom.partyRoom;
import static com.togather.partyroom.location.model.QPartyRoomLocation.partyRoomLocation;
import static com.togather.partyroom.core.model.QPartyRoomOperationDay.partyRoomOperationDay;
import static com.togather.partyroom.tags.model.QPartyRoomCustomTag.partyRoomCustomTag;
import static com.togather.partyroom.tags.model.QPartyRoomCustomTagRel.partyRoomCustomTagRel;

public class PartyRoomRepositoryImpl implements PartyRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public PartyRoomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<PartyRoom> search(PartyRoomSearchQueryDto partyRoomSearchQueryDto) {
        Pageable page = PageRequest.of(partyRoomSearchQueryDto.getPageNum() - 1, partyRoomSearchQueryDto.getPageSize());

        List<PartyRoom> result = jpaQueryFactory
                .select(partyRoom)
                .from(partyRoom)
                .leftJoin(partyRoomLocation).on(partyRoom.eq(partyRoomLocation.partyRoom))
                .leftJoin(partyRoomOperationDay).on(partyRoom.eq(partyRoomOperationDay.partyRoom))
                .leftJoin(partyRoomCustomTagRel).on(partyRoom.eq(partyRoomCustomTagRel.partyRoom))
                .leftJoin(partyRoomCustomTag).on(partyRoomCustomTag.eq(partyRoomCustomTagRel.partyRoomCustomTag))

                .where(sidoEquals(partyRoomSearchQueryDto.getSido()),
                        sigunguLike(partyRoomSearchQueryDto.getSigungu()),
                        opensAt(partyRoomSearchQueryDto.getDate()),
                        guestCapacityGoe(partyRoomSearchQueryDto.getGuestCount()),
                        containsKeyword(partyRoomSearchQueryDto.getKeywords()))
                .distinct()
                .orderBy(partyRoom.partyRoomId.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        return result;
    }

    /**
     * Returns if sido of party room is equal to the "sido" field of user input
     * If user input field of "sido" is empty(or null), then the user did not enter this field
     * Therefore, if this field is null, we should serve all partyRooms REGARDLESS of sido
     *
     * 파티룸의 '시/도' 정보가 유저가 검색 필드로 입력한 '시/도' 정보와 일치하는지를 확인하는 메서드입니다.
     * 만약 이 필드가 유효하다면(사용자가 값을 입력했다면), 파티룸의 위치와 사용자의 입력값이 일치하는 파티룸만 찾습니다.
     * 만약 이 필드가 비어있다면, 사용자가 값을 입력하지 않았다는 의미입니다. 그렇다면 '시/도' 조건은 확인하지 않고 다른 모든
     * 조건을 만족하는 파티룸을 반환해야 합니다. ('시/도' 는 판별하지 않습니다)
     *
     * 실제로 사용자가 '시/도' 필드를 입력하지 않아서 반환값(BooleanExpression)이 null 이면 위 쿼리에서 where(null, ...) 이 실행되게 되고,
     * 시/도 조건은 검색하지 않습니다.
     */
    private BooleanExpression sidoEquals(String sido) {
        if (StringUtils.hasText(sido)) {
            return partyRoomLocation.sido.eq(sido);
        }
        return null;
    }

    private BooleanExpression sigunguLike(String sigungu) {
        if (StringUtils.hasText(sigungu)) {
            return partyRoomLocation.sigungu.like(sigungu + "%");
        }
        return null;
    }

    /**
     * Returns if the partyRoom is opened at the date.
     * if 'date' field has a valid value, we compare the 'operationDays' of the party room and return only valid party rooms.
     * if 'date' field has an invalid value, we skip the "check if the party room opens at a specific date" logic.
     *
     * 파티룸이 해당 날짜에 여는지 확인합니다.
     * (위의 시/도 필드와 동일하게) 사용자가 유효한 값을 입력했다면, operationDays를 찾아서 해당 날짜에 영업하는 파티룸만 반환합니다.
     * 사용자가 값을 입력하지 않았다면, 날짜는 검색하지 않겠다는 의미이므로 날짜와 관계없이 모든 파티룸을 반환합니다.
     */

    private BooleanExpression opensAt(LocalDate date) {
        if (date != null) {
            return partyRoomOperationDay.operationDay.eq(date.getDayOfWeek());
        }
        return null;
    }

    /**
     * 사용자가 입력한 사용 인원과 파티룸이 최대로 수용할 수 있는 인원을 비교합니다.
     * 사용자가 이용 인원을 입력했다면 파티룸의 최대 수용 인원(guestCapacity) 를 비교해서 capacity >= guestCount (GOE: Greater or equal) 한 파티룸만 반환합니다.
     * 마찬가지로 이용인원을 입력하지 않았다면 파티룸의 최대 수용 인원 조건을 확인하지 않습니다.
     */

    private BooleanExpression guestCapacityGoe(Integer guestCount) {
        if (guestCount != null) {
            return partyRoom.guestCapacity.goe(guestCount);
        }
        return null;
    }

    /**
     * 키워드 판별 로직입니다. 사용자가 아무 키워드도 입력하지 않았다면, 파티룸의 키워드를 판별하지 않습니다.
     * 1개 이상의 키워드를 입력했다면, 그 중 하나라도 태그로 등록한 파티룸은 검색의 대상이 됩니다.
     */
    private BooleanExpression containsKeyword(List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return null;
        }
        return partyRoomCustomTag.tagContent.in(keywords);
    }
}
