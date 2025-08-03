package com.edukit.core.member.repository;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.enums.MemberRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberUuidAndIsDeleted(String memberUuid, boolean isDeleted);

    Optional<Member> findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<Member> findByIdAndIsDeleted(long id, boolean isDeleted);

    boolean existsByEmailAndIsDeleted(String email, boolean isDeleted);

    boolean existsByIdNotAndNicknameIgnoreCaseAndIsDeleted(long id, String nickname, boolean isDeleted);

    @Query("SELECT m FROM Member m JOIN FETCH m.subject WHERE m.id = :id AND m.isDeleted = :isDeleted")
    Optional<Member> findByIdAndIsDeletedFetchJoinSubject(@Param("id") long id, @Param("isDeleted") boolean isDeleted);

    @Query("""
            SELECT m FROM Member m 
            WHERE m.isDeleted = false 
            AND m.role = :role
            AND m.verifiedAt < :lastVerificationCutoff
            ORDER BY m.id
            """)
    List<Member> findTeachersForVerificationReset(@Param("role") MemberRole role,
                                                  @Param("lastVerificationCutoff") LocalDateTime cutOffDate);

    List<Member> findMembersByRoleAndIsDeleted(MemberRole role, boolean isDeleted);
}
