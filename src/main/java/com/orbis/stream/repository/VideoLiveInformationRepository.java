package com.orbis.stream.repository;

import com.orbis.stream.model.VideoLiveInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLiveInformationRepository extends JpaRepository<VideoLiveInformation, Integer> {
}
