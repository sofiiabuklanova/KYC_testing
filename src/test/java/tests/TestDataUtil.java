package tests;

public class TestDataUtil {
    static String getValidEmail() {
        return "user" + System.currentTimeMillis() + "@example.com";
    }

    static String generateValidPhoneNumber() {
        return "+" + System.currentTimeMillis();
    }
}
