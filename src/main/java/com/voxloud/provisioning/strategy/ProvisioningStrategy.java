package com.voxloud.provisioning.strategy;

import com.voxloud.provisioning.entity.Device;

public interface ProvisioningStrategy {
    String generateConfig(Device device, String domain, int port, java.util.List<String> codecs);
}
