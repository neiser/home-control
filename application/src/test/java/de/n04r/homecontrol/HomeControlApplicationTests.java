package de.n04r.homecontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.websocket.messages.AbstractWsMessage;
import de.n04r.homecontrol.websocket.messages.TagsSelectedWsMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HomeControlApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		// just context loading
	}

	@Test
	void deserializedJson() throws Exception {
		AbstractWsMessage abstractWsMessage = objectMapper.readValue("{\"type\":\"tags-selected\",\"tags\":[\"Hinten\"]}", AbstractWsMessage.class);
		assertThat(abstractWsMessage).isInstanceOf(TagsSelectedWsMessage.class);
	}
}
