package com.beside.ten011.archive.service;

import com.beside.ten011.archive.controller.dto.ArchiveRequest;
import com.beside.ten011.archive.entity.Archive;
import com.beside.ten011.archive.repository.ArchiveRepository;
import com.beside.ten011.archive.repository.ArchiveSpec;
import com.beside.ten011.user.controller.dto.MyRitualResponse;
import com.beside.ten011.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArchiveService {

    private final ArchiveRepository archiveRepository;

    @Transactional(readOnly = true)
    public Page<Archive> getArchivePage(User user, Pageable pageable, String title) {
        return archiveRepository.findAll(ArchiveSpec.searchWith(user.getId(), title), pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Archive> getRandomArchive(User user) {
        return archiveRepository.findRandomArchive(user.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Archive> getArchive(User user, Long archiveId) {
        return archiveRepository.findByIdAndUserId(archiveId, user.getId());
    }

    @Transactional
    public void saveArchive(User user, ArchiveRequest request) {
        Archive archive = Archive.builder()
                .user(user)
                .title(request.getTitle())
                .author(request.getAuthor())
                .content(request.getContent())
                .imageSize(request.getImageSize())
                .backgroundColor(request.getBackgroundColor())
                .fontStyle(request.getFontStyle())
                .fontColor(request.getFontColor())
                .build();
        archiveRepository.save(archive);
    }

    @Transactional
    public void modifyArchive(User user, ArchiveRequest request, Long archiveId) {
        getArchive(user, archiveId)
                .ifPresent(archive -> archive.modify(request.getTitle(), request.getAuthor(), request.getContent(),
                        request.getImageSize(), request.getBackgroundColor(),
                        request.getFontStyle(), request.getFontColor()
                ));
    }

    @Transactional
    public void deleteArchive(User user, Long archiveId) {
        getArchive(user, archiveId)
                .ifPresent(archiveRepository::delete);
    }

    @Transactional(readOnly = true)
    public MyRitualResponse getMyRitual(Authentication authentication) {
        long totalArchiveCount = archiveRepository.count();
        long totalBookCount = archiveRepository.countTotalBook();
        return MyRitualResponse.builder()
                .totalArchiveCount(totalArchiveCount)
                .totalBookCount(totalBookCount)
                .build();
    }
}
