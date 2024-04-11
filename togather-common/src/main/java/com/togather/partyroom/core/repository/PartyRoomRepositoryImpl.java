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
                        sigunguEquals(partyRoomSearchQueryDto.getSigungu()),
                        opensAt(partyRoomSearchQueryDto.getDate()),
                        guestCapacityGoe(partyRoomSearchQueryDto.getGuestCount()),
                        containsKeyword(partyRoomSearchQueryDto.getKeywords()))
                .distinct()
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        return result;
    }

    private BooleanExpression sidoEquals(String sido) {
        if (StringUtils.hasText(sido)) {
            return partyRoomLocation.sido.eq(sido);
        }
        return null;
    }

    private BooleanExpression sigunguEquals(String sigungu) {
        if (StringUtils.hasText(sigungu)) {
            return partyRoomLocation.sigungu.eq(sigungu);
        }
        return null;
    }

    private BooleanExpression opensAt(LocalDate date) {
        if (date != null) {
            return partyRoomOperationDay.operationDay.eq(date.getDayOfWeek());
        }
        return null;
    }

    private BooleanExpression guestCapacityGoe(Integer guestCount) {
        if (guestCount != null) {
            return partyRoom.guestCapacity.goe(guestCount);
        }
        return null;
    }

    private BooleanExpression containsKeyword(List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return null;
        }
        return partyRoomCustomTag.tagContent.in(keywords);
    }
}
