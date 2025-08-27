package com.edukit.common.infra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final String SERVER_ID_FILE_PATH = "/tmp/server-id.txt";

    public ServerInstanceManager() {
        this.serverId = System.getenv(SERVER_ID_ENV_VAR) != null
                ? System.getenv(SERVER_ID_ENV_VAR)
                : generateServerIdFromEC2Metadata();
        
        writeServerIdToFile();
    }

    private String generateServerIdFromEC2Metadata() {
        return SERVER_PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }

    private void writeServerIdToFile() {
        try {
            Path path = Paths.get(SERVER_ID_FILE_PATH);
            Files.createDirectories(path.getParent());
            Files.write(path, serverId.getBytes());
            log.info("Server ID written to file: {}", serverId);
        } catch (IOException e) {
            log.warn("Failed to write server ID to file: {}", e.getMessage());
        }
    }
}
