package com.d209.childmade.video.repository;

import com.d209.childmade.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findAllByMemberId(Integer memberId, Pageable pageable);

    void deleteByIdAndMemberId(Long Id, Integer memberId);

    String findVideoUrlById(Long Id);
}
