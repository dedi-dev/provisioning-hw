package com.voxloud.provisioning.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConferenceProvisioningStrategy implements ProvisioningStrategy {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String generateConfig(Device device, String domain, int port, List<String> codecs) {
        Map<String, Object> config = new HashMap<>();
        config.put("username", device.getUsername());
        config.put("password", device.getPassword());
        config.put("domain", domain);
        config.put("port", String.valueOf(port));
        config.put("codecs", codecs);

        if (device.getOverrideFragment() != null) {
            try {
                JsonNode override = mapper.readTree(device.getOverrideFragment());
                override.fields().forEachRemaining(e -> config.put(e.getKey(), e.getValue()));
            } catch (Exception ignored) {}
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
