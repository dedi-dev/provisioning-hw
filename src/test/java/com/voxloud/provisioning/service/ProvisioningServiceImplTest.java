package com.voxloud.provisioning.service;

import com.voxloud.provisioning.config.ProvisioningProperties;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.entity.Device.DeviceModel;
import com.voxloud.provisioning.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProvisioningServiceImplTest {

    private DeviceRepository deviceRepository;
    private ProvisioningProperties provisioningProperties;
    private ProvisioningServiceImpl provisioningService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        provisioningProperties = new ProvisioningProperties();
        provisioningProperties.setDomain("sip.voxloud.com");
        provisioningProperties.setPort(5060);
        provisioningProperties.setCodecs(Arrays.asList("G711", "G729", "OPUS"));

        provisioningService = new ProvisioningServiceImpl(deviceRepository, provisioningProperties);
    }

    @Test
    void testGenerateDeskConfigWithoutOverride() {
        Device device = new Device();
        device.setMacAddress("aa-bb-cc-dd-ee-ff");
        device.setModel(DeviceModel.DESK);
        device.setUsername("john");
        device.setPassword("doe");

        when(deviceRepository.findById("aa-bb-cc-dd-ee-ff")).thenReturn(Optional.of(device));

        String result = provisioningService.getProvisioningFile("aa-bb-cc-dd-ee-ff");

        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=doe"));
        assertTrue(result.contains("domain=sip.voxloud.com"));
        assertTrue(result.contains("port=5060"));
        assertTrue(result.contains("codecs=G711,G729,OPUS"));
    }

    @Test
    void testGenerateConferenceConfigWithOverride() {
        Device device = new Device();
        device.setMacAddress("1a-2b-3c-4d-5e-6f");
        device.setModel(DeviceModel.CONFERENCE);
        device.setUsername("eric");
        device.setPassword("blue");
        device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}");

        when(deviceRepository.findById("1a-2b-3c-4d-5e-6f")).thenReturn(Optional.of(device));

        String result = provisioningService.getProvisioningFile("1a-2b-3c-4d-5e-6f");

        assertTrue(result.contains("\"username\" : \"eric\""));
        assertTrue(result.contains("\"password\" : \"blue\""));
        assertTrue(result.contains("\"domain\" : \"sip.anotherdomain.com\""));
        assertTrue(result.contains("\"port\" : \"5161\""));
        assertTrue(result.contains("\"timeout\" : 10"));
        assertTrue(result.contains("\"codecs\" : [ \"G711\", \"G729\", \"OPUS\" ]"));
    }

    @Test
    void testDeviceNotFound() {
        when(deviceRepository.findById("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                provisioningService.getProvisioningFile("unknown"));

        assertEquals("Device not found", ex.getMessage());
    }

    @Test
    void testUnsupportedDeviceModel() {
        Device device = new Device();
        device.setMacAddress("unsupported-mac");
        device.setUsername("abc");
        device.setPassword("xyz");

        // Fake model that is not in the map
        device.setModel(null);

        when(deviceRepository.findById("unsupported-mac")).thenReturn(Optional.of(device));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                provisioningService.getProvisioningFile("unsupported-mac"));

        assertEquals("No provisioning strategy for device model", ex.getMessage());
    }
}
