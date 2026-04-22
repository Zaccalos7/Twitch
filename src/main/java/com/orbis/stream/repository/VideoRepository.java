package com.orbis.stream.repository;

import com.orbis.stream.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    Optional<List<Video>> findByVideoSetting_id(Long id);
}
