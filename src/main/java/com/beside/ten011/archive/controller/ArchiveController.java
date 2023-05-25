package com.beside.ten011.archive.controller;

import com.beside.ten011.archive.controller.dto.ArchiveRequest;
import com.beside.ten011.archive.controller.dto.ArchiveResponse;
import com.beside.ten011.archive.service.ArchiveService;
import com.beside.ten011.exception.CustomException;
import com.beside.ten011.exception.ErrorCode;
import com.beside.ten011.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    /**
     * 아카이브 목록 조회
     *
     * @param authentication
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getArchivePages(
            Authentication authentication,
            @PageableDefault(sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String search) {

        Map<String, Object> map = new HashMap<>();
        map.put("totalCreationDate", archiveService.getTotalCreationDates((User) authentication.getPrincipal()));
        map.put("archives", archiveService.getArchiveResponsePageCustom((User) authentication.getPrincipal(), pageable, search, year, month));
        return ResponseEntity.ok(map);
    }

    /**
     * 아카이브 조회 (랜덤 단건)
     *
     * @param authentication
     * @return
     */
    @GetMapping("random")
    public ResponseEntity<ArchiveResponse> getRandomArchive(Authentication authentication) {
        return ResponseEntity.ok(
                archiveService.getRandomArchive((User) authentication.getPrincipal())
                        .map(ArchiveResponse::fromEntity)
                        .orElseGet(() -> null)
        );
    }

    /**
     * 아카이브 조회 (단건)
     *
     * @param authentication
     * @param archiveId
     * @return
     */
    @GetMapping("{archiveId}")
    public ResponseEntity<ArchiveResponse> getArchive(Authentication authentication,
                                                      @PathVariable("archiveId") Long archiveId) {
        return ResponseEntity.ok(
                archiveService.getArchive((User) authentication.getPrincipal(), archiveId)
                        .map(ArchiveResponse::fromEntity)
                        .orElseThrow(() -> new CustomException(ErrorCode.ARCHIVE_NOT_FOUND)));
    }

    /**
     * 아카이브 등록
     *
     * @param authentication
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> saveArchive(Authentication authentication,
                                                         @RequestBody ArchiveRequest request) {
        Long saveArchiveId = archiveService.saveArchive((User) authentication.getPrincipal(), request);
        return ResponseEntity.ok(Map.of("archiveId", saveArchiveId));
    }

    /**
     * 아카이브 수정
     *
     * @param authentication
     * @param request
     * @param archiveId
     * @return
     */
    @PatchMapping("{archiveId}")
    public ResponseEntity<Map<String, Long>> modifyArchive(Authentication authentication,
                                                           @RequestBody ArchiveRequest request,
                                                           @PathVariable("archiveId") Long archiveId) {

        Long modifyArchiveId = archiveService.modifyArchive((User) authentication.getPrincipal(), request, archiveId);
        return ResponseEntity.ok(Map.of("archiveId", modifyArchiveId));
    }

    /**
     * 아카이브 삭제
     *
     * @param authentication
     * @param archiveId
     * @return
     */
    @DeleteMapping("{archiveId}")
    public ResponseEntity<Void> deleteArchive(Authentication authentication,
                                              @PathVariable("archiveId") Long archiveId) {
        archiveService.deleteArchive((User) authentication.getPrincipal(), archiveId);
        return ResponseEntity.ok().build();
    }
}
