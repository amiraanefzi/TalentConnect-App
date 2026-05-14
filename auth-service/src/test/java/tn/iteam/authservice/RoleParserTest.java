package tn.iteam.authservice;

import org.junit.jupiter.api.Test;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.RoleParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleParserTest {

    @Test
    void parse_salarieWithAccent_mapsToEmployeRole() {
        assertEquals(Role.EMPLOYE, RoleParser.parse("salarié"));
    }
}
