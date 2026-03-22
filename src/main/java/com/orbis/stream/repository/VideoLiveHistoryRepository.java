package com.orbis.stream.repository;

import com.orbis.stream.model.VideoLiveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLiveHistoryRepository extends JpaRepository<VideoLiveHistory, Long> {
}
