package com.talentconnect.candidatures.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentconnect.candidatures.infrastructure.kafka.CandidateAppliedKafkaProducer;

@SpringBootTest
@AutoConfigureMockMvc
class CandidatureControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	CandidateAppliedKafkaProducer candidateAppliedKafkaProducer;

	@Test
	void employeeCreatesAndReadsOwnCandidature() throws Exception {
		String payload = """
				{
				  "offerId": 500,
				  "type": "INTERNE"
				}
				""";

		String createdJson = mockMvc.perform(post("/api/candidatures")
						.header("X-User-Id", "42")
						.header("X-Role", "EMPLOYEE")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.offerId").value(500))
				.andExpect(jsonPath("$.applicantUserId").value(42))
				.andExpect(jsonPath("$.status").value("SOUMISE"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode created = objectMapper.readTree(createdJson);
		assertThat(created.get("id").asLong()).isPositive();

		mockMvc.perform(get("/api/candidatures/{id}", created.get("id").asLong())
						.header("X-User-Id", "42")
						.header("X-Role", "EMPLOYEE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.candidature.id").value(created.get("id").asLong()));
	}
}
