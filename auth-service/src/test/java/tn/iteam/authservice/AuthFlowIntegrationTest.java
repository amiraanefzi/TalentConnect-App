package tn.iteam.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.UserService;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthFlowIntegrationTest {
    final ObjectMapper objectMapper = new ObjectMapper();

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    void registerReturnsRolesWithoutRolePrefix_andCannotAccessAdmin() throws Exception {
        String body = """
                {"email":"emp1@test.tn","password":"Password@123"}
                """;

        String token = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasItem("EMPLOYE")))
                .andExpect(jsonPath("$.roles", not(hasItem("ROLE_EMPLOYE"))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(token).get("accessToken").asText();

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateRoles_acceptsAccentedEmployee() throws Exception {
        userService.register("admin2@test.tn", "AdminPass@123", Set.of(Role.ADMIN));
        userService.register("user2@test.tn", "Password@123", Set.of(Role.EMPLOYE));

        String login = """
                {"email":"admin2@test.tn","password":"AdminPass@123"}
                """;

        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(loginResp).get("accessToken").asText();

        String listResp = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long idToUpdate = -1L;
        for (var node : objectMapper.readTree(listResp)) {
            if ("user2@test.tn".equalsIgnoreCase(node.get("email").asText())) {
                idToUpdate = node.get("id").asLong();
                break;
            }
        }
        if (idToUpdate < 0) {
            throw new IllegalStateException("Test user not found in admin list response");
        }

        String patchBody = """
                {"roles":["RH","EMPLOYÉ"]}
                """;

        mockMvc.perform(patch("/api/admin/users/" + idToUpdate + "/roles")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasItem("RH")))
                .andExpect(jsonPath("$.roles", hasItem("EMPLOYE")));
    }
}
