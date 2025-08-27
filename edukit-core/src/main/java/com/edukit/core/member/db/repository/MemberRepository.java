package com.edukit.core.member.db.repository;

import com.edukit.core.member.db.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberUuidAndIsDeleted(String memberUuid, boolean isDeleted);

    Optional<Member> findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<Member> findByIdAndIsDeleted(long id, boolean isDeleted);

    boolean existsByEmailAndIsDeleted(String email, boolean isDeleted);

    boolean existsByNicknameIgnoreCase(String nickname);

    @Query("SELECT m FROM Member m JOIN FETCH m.subject WHERE m.id = :id AND m.isDeleted = :isDeleted")
    Optional<Member> findByIdAndIsDeletedFetchJoinSubject(@Param("id") long id, @Param("isDeleted") boolean isDeleted);
}
