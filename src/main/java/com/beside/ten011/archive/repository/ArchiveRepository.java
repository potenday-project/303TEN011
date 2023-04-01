package com.beside.ten011.archive.repository;

import com.beside.ten011.archive.entity.Archive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long>, JpaSpecificationExecutor<Archive> {

    Optional<Archive> findByIdAndUserId(Long id, Long userId);

    @Query(value = "select * from archive a where a.user_id = :userId order by rand() limit 1", nativeQuery = true)
    Optional<Archive> findRandomArchive(@Param("userId") Long userId);

    Page<Archive> findAll(Specification<Archive> spec, Pageable pageable);

    // TODO 수정
    @Query(value = "select count(distinct a.title) from Archive a")
    Long countTotalBook();
}
