package com.edukit.core.member.repository;

import com.edukit.core.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberUuidAndIsDeleted(String memberUuid, boolean isDeleted);

    Optional<Member> findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<Member> findByIdAndIsDeleted(long id, boolean isDeleted);

    boolean existsByEmailAndIsDeleted(String email, boolean isDeleted);
}
