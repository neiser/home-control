package de.n04r.homecontrol;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "devices")
public class DevicesConfigurationProperties extends ArrayList<DevicesConfigurationProperties.Device> {

    @Value
    @RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
    public static class Device {
        String name;
        String type;
        String host;
        Set<String> tags;
    }
}
