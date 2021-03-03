package de.n04r.homecontrol;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "devices")
public class DevicesConfigurationProperties extends ArrayList<DevicesConfigurationProperties.Device> {

    @Getter
    @Setter
    public static class Device {
        private String name;
        private String type;
        private String host;
        private Set<String> tags;
    }
}
