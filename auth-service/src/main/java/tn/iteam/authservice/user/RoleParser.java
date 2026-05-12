package tn.iteam.authservice.user;

import java.text.Normalizer;

public final class RoleParser {
    private RoleParser() {
    }

    public static Role parse(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Role is required");
        }
        String normalized = Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('-', '_')
                .toUpperCase();

        if (normalized.equals("EMPLOYEE") || normalized.equals("EMPLOYE") || normalized.equals("SALARIE")) {
            return Role.EMPLOYE;
        }
        if (normalized.equals("HR") || normalized.equals("RH")) {
            return Role.RH;
        }
        if (normalized.equals("ADMIN")) {
            return Role.ADMIN;
        }
        return Role.valueOf(normalized);
    }
}
