package domain.model;

public class UserRequest {
    public String email;
    public String password;
    public String phone;

    public UserRequest(String email, String password, String phone) {
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
