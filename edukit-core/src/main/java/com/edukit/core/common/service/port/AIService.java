package com.edukit.core.common.service.port;

import com.edukit.core.common.service.response.OpenAIVersionResponse;
import reactor.core.publisher.Flux;

public interface AIService {

    Flux<OpenAIVersionResponse> getVersionedStreamingResponse(String prompt);
}
