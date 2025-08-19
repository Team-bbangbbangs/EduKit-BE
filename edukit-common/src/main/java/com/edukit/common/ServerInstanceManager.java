package com.edukit.common;

import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class ServerInstanceManager {

    private final String serverId;

    private static final String SERVER_PREFIX = "server-";
    private static final String SERVER_ID_ENV_VAR = "SERVER_ID";

    public ServerInstanceManager() {
        this.serverId = System.getenv(SERVER_ID_ENV_VAR) != null
                ? System.getenv(SERVER_ID_ENV_VAR)
                : generateServerIdFromEC2Metadata();
    }

    private String generateServerIdFromEC2Metadata() {
        return SERVER_PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }
}
