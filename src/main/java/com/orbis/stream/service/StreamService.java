package com.orbis.stream.service;

import com.orbis.stream.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final ResponseHandler responseHandler;

    public ResponseEntity<Map<String, String>> startLive() {
        return null;
    }
}
