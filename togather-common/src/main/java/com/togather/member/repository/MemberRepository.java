package com.togather.member.repository;

import com.togather.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.email = :email")
    Optional<Member> findByEmail(String email);

    @Query("select m from Member m where m.email = :email and m.password = :password")
    Optional<Member> findByEmailAndPassword(String email, String password);

}
