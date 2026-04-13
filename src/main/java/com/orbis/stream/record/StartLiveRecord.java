package com.orbis.stream.record;

import jakarta.validation.constraints.NotNull;

public record StartLiveRecord(
        @NotNull(message = "not.valid.input") String streamUrl,
        @NotNull(message = "not.valid.input") String streamKey,
        @NotNull(message = "not.valid.input") String platformStreamName,
        VideoRecord videoRecord
) {
}
