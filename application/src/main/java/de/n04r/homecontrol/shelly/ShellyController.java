package de.n04r.homecontrol.shelly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShellyController {
    private final RestTemplateBuilder restTemplateBuilder;

    public void openShutters(List<String> shellyHosts) {
        sendCommandTo(shellyHosts, "open");
    }

    public void closeShutters(List<String> shellyHosts) {
        sendCommandTo(shellyHosts, "close");
    }

    private void sendCommandTo(List<String> shellyHosts, String command) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        log.debug("Sending command {} to hosts: {}", command, shellyHosts);
        shellyHosts.forEach(shellyHost -> {
            URI uri = UriComponentsBuilder.fromPath("/roller/0")
                    .scheme("http")
                    .host(shellyHost)
                    .queryParam("go", command)
                    .queryParam("duration", 0)
                    .build().toUri();
            String response = restTemplate.getForObject(uri, String.class);
            log.debug("Got response: {}", response);
        });
    }
}
