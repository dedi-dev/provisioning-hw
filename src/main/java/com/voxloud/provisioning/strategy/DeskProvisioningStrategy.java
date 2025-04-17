package com.voxloud.provisioning.strategy;

import com.voxloud.provisioning.entity.Device;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

public class DeskProvisioningStrategy implements ProvisioningStrategy {
    @Override
    public String generateConfig(Device device, String domain, int port, List<String> codecs) {
        Properties props = new Properties();
        props.setProperty("username", device.getUsername());
        props.setProperty("password", device.getPassword());
        props.setProperty("domain", domain);
        props.setProperty("port", String.valueOf(port));
        props.setProperty("codecs", String.join(",", codecs));

        if (device.getOverrideFragment() != null) {
            try {
                props.load(new StringReader(device.getOverrideFragment()));
            } catch (Exception ignored) {}
        }

        try (StringWriter writer = new StringWriter()) {
            props.store(writer, null);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
