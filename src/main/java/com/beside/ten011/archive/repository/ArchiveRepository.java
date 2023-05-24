package com.beside.ten011.archive.repository;

import com.beside.ten011.archive.entity.Archive;
import com.beside.ten011.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long>, JpaSpecificationExecutor<Archive> {

    void deleteByUserId(Long userId);

    Optional<Archive> findByIdAndUserId(Long id, Long userId);

    @Query(value = "select * from archive a where a.user_id = :userId order by rand() limit 1", nativeQuery = true)
    Optional<Archive> findRandomArchive(@Param("userId") Long userId);

    Page<Archive> findAll(Specification<Archive> spec, Pageable pageable);

    // TODO 수정
    @Query(value = "select count(distinct a.title) from Archive a where a.user = :user")
    Long countTotalBookByUser(@Param("user") User user);

    Long countByUserId(@Param("userId") Long userId);

    // TODO 쿼리문 수정 필요
    @Query(value = """
                select continuity from (
                    select max(created_dt) as '종료일'
                    , count(*) as continuity
                    from(
                        select ((@row_number \\:= @row_number + 1) + datediff(created_dt, CURDATE())) as 'consecutive_day', created_dt
                        FROM (select distinct date_format(created_dt,'%y-%m-%d') as created_dt from archive where user_id = :userId) a,
                        (SELECT @row_number \\:= 0) AS x
                        ORDER BY created_dt DESC
                     ) b
                     group by consecutive_day
                 ) c
                 where 종료일 = curdate() or 종료일 = subdate(curdate(), 1)
            """
            , nativeQuery = true)
    Long countContinuityPostDay(@Param("userId") Long userId);

    @Query(value = "select distinct function('date_format', createdDt, '%Y-%m') as date" +
            " from Archive a where a.user = :user")
    List<String> getTotalCreationDates(@Param(value = "user") User user);
}
