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
		// Vérifie que le contexte Spring Boot se charge sans erreur.
		// Aucune assertion nécessaire : un échec de démarrage lèverait une exception.
	}
}
