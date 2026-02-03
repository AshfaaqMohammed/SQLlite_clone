package model;

public class Row {

    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int ROW_SIZE = Integer.BYTES + MAX_USERNAME_LENGTH + MAX_EMAIL_LENGTH;

    private int id;
    private String username;
    private String email;

    public Row(int id, String username, String email){
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "(" + id + "," + username + "," + email + ")";
    }
}
