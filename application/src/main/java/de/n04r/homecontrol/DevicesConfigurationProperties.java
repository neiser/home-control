package de.n04r.homecontrol;

import de.n04r.homecontrol.model.Device;
import de.n04r.homecontrol.model.DeviceType;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "devices")
public class DevicesConfigurationProperties extends ArrayList<DevicesConfigurationProperties.DeviceConfigItem> {

    @Value
    @RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
    public static class DeviceConfigItem implements Device {
        String name;
        DeviceType type;
        String host;
        Set<String> tags;
    }
}
