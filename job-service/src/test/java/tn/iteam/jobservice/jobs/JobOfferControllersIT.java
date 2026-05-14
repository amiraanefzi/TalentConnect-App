package tn.iteam.jobservice.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.jobservice.jobs.api.dto.JobOfferCreateRequest;
import tn.iteam.jobservice.jobs.domain.EmploymentType;
import tn.iteam.jobservice.jobs.domain.ExperienceLevel;
import tn.iteam.jobservice.jobs.domain.JobOffer;
import tn.iteam.jobservice.jobs.repo.JobOfferRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobOfferControllersIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired JobOfferRepository repo;

    @BeforeEach
    void cleanup() {
        repo.deleteAll();
    }

    @Test
    void listForEmploye_onlyReturnsPublishedOffers() throws Exception {
        repo.save(JobOffer.builder()
                .title("Backend Engineer")
                .companyName("Acme")
                .location("Tunis")
                .employmentType(EmploymentType.FULL_TIME)
                .experienceLevel(ExperienceLevel.MID)
                .remote(true)
                .description("Desc 1")
                .published(true)
                .build());

        repo.save(JobOffer.builder()
                .title("QA Engineer")
                .companyName("Beta")
                .location("Sfax")
                .employmentType(EmploymentType.CONTRACT)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .remote(false)
                .description("Desc 2")
                .published(false)
                .build());

        mvc.perform(get("/api/jobs")
                        .header("Authorization", "Bearer " + jwt("emp@talen.local", List.of("ROLE_EMPLOYE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Backend Engineer"));
    }

    @Test
    void getForEmploye_unpublishedOfferIsHidden() throws Exception {
        var unpublished = repo.save(JobOffer.builder()
                .title("Hidden Offer")
                .companyName("Secret")
                .location("Remote")
                .employmentType(EmploymentType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .remote(true)
                .description("Nope")
                .published(false)
                .build());

        mvc.perform(get("/api/jobs/" + unpublished.getId())
                        .header("Authorization", "Bearer " + jwt("emp@talen.local", List.of("ROLE_EMPLOYE"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCreate_returns201AndLocation() throws Exception {
        var req = new JobOfferCreateRequest(
                "DevOps Engineer",
                "Gamma",
                "Lagos",
                EmploymentType.FULL_TIME,
                ExperienceLevel.MID,
                true,
                "We need a DevOps Engineer",
                1000,
                2000,
                "USD",
                true
        );

        mvc.perform(post("/api/admin/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt("rh@talen.local", List.of("ROLE_RH")))
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/admin/jobs/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void adminCreate_withAdminToken_returns201() throws Exception {
        var req = new JobOfferCreateRequest(
                "Platform Engineer",
                "Delta",
                "Tunis",
                EmploymentType.FULL_TIME,
                ExperienceLevel.SENIOR,
                true,
                "Need a platform engineer",
                1500,
                2500,
                "USD",
                true
        );

        mvc.perform(post("/api/admin/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt("admin@talen.local", List.of("ROLE_ADMIN")))
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void adminCreate_withoutToken_isForbidden() throws Exception {
        var req = new JobOfferCreateRequest(
                "No Auth",
                "Gamma",
                "Lagos",
                EmploymentType.FULL_TIME,
                ExperienceLevel.MID,
                true,
                "We need a DevOps Engineer",
                1000,
                2000,
                "USD",
                true
        );

        mvc.perform(post("/api/admin/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreate_withEmployeToken_isForbidden() throws Exception {
        var req = new JobOfferCreateRequest(
                "No Privilege",
                "Gamma",
                "Lagos",
                EmploymentType.FULL_TIME,
                ExperienceLevel.MID,
                true,
                "Unauthorized create",
                1000,
                2000,
                "USD",
                true
        );

        mvc.perform(post("/api/admin/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt("emp@talen.local", List.of("ROLE_EMPLOYE")))
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    private static String jwt(String subject, List<String> roles) {
        // Must match app.jwt.secret in application-test.properties
        String secret = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .claim("roles", roles)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
