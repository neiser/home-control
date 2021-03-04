package de.n04r.homecontrol.shelly;

import de.n04r.homecontrol.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@Slf4j
public class ShellyDeviceHandler {
    private final RestTemplate restTemplate;

    public ShellyDeviceHandler(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void turnRelayOff(Device device) {
        sendRelayCommand(device, "off");
    }

    public void turnRelayOn(Device device) {
        sendRelayCommand(device, "on");
    }

    public void openShutter(Device device) {
        sendShutterCommand(device, "open");
    }

    public void closeShutter(Device device) {
        sendShutterCommand(device, "close");
    }

    public void stopShutter(Device device) {
        sendShutterCommand(device, "close");
    }

    private void sendRelayCommand(Device device, String command) {
        String shellyHost = device.getHost();
        log.debug("Sending relay command {} to host: {}", command, shellyHost);
        URI uri = UriComponentsBuilder.fromPath("/relay/0")
                .scheme("http")
                .host(shellyHost)
                .queryParam("turn", command)
                .build().toUri();
        String response = restTemplate.getForObject(uri, String.class);
        log.debug("Got response: {}", response);
    }

    private void sendShutterCommand(Device device, String command) {
        String shellyHost = device.getHost();
        log.debug("Sending shutter command {} to host: {}", command, shellyHost);
        URI uri = UriComponentsBuilder.fromPath("/roller/0")
                .scheme("http")
                .host(shellyHost)
                .queryParam("go", command)
                .queryParam("duration", 0)
                .build().toUri();
        String response = restTemplate.getForObject(uri, String.class);
        log.debug("Got response: {}", response);
    }
}
