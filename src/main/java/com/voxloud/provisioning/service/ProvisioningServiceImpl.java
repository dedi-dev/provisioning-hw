package com.voxloud.provisioning.service;

import com.voxloud.provisioning.config.ProvisioningProperties;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.strategy.ConferenceProvisioningStrategy;
import com.voxloud.provisioning.strategy.DeskProvisioningStrategy;
import com.voxloud.provisioning.strategy.ProvisioningStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProvisioningServiceImpl implements ProvisioningService {

    private final DeviceRepository repository;
    private final ProvisioningProperties properties;

    private static final Map<Device.DeviceModel, ProvisioningStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put(Device.DeviceModel.DESK, new DeskProvisioningStrategy());
        strategyMap.put(Device.DeviceModel.CONFERENCE, new ConferenceProvisioningStrategy());
    }

    @Override
    public String getProvisioningFile(String macAddress) {
        Device device = repository.findById(macAddress)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        ProvisioningStrategy strategy = strategyMap.get(device.getModel());
        if (strategy == null) {
            throw new RuntimeException("No provisioning strategy for device model");
        }

        return strategy.generateConfig(device, properties.getDomain(), properties.getPort(), properties.getCodecs());
    }
}
