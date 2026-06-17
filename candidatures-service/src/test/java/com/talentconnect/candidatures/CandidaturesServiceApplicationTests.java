package com.talentconnect.candidatures;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.talentconnect.candidatures.infrastructure.kafka.CandidateAppliedKafkaProducer;

@SpringBootTest
class CandidaturesServiceApplicationTests {

	@MockitoBean
	CandidateAppliedKafkaProducer candidateAppliedKafkaProducer;

	@Test
	void contextLoads() {
	}
}
