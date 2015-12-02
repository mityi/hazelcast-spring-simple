package rda.simpleapp.repositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthHelper {

    public static List<String> getRole(String tag) {
        switch (tag) {
            case "user":
                return Arrays.asList("USER");
            case "admin":
                return Arrays.asList("ADMIN", "USER");
            default:
                return Collections.emptyList();
        }
    }

    public static Map<String, String> devUsers() {
        return Arrays.asList("admin", "user").stream().collect(Collectors.toMap(u -> u, u -> u));
    }
}
