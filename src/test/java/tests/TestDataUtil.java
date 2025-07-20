package tests;

import org.jetbrains.annotations.NotNull;

public class TestDataUtil {
    @NotNull
    static String getValidEmail() {
        return "user" + System.currentTimeMillis() + "@example.com";
    }

    @NotNull
    static String generateValidPhoneNumber() {
        return "+" + System.currentTimeMillis();
    }
}
