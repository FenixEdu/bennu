package pt.ist.bennu.json.tests;

public class User extends Person {
    private String password;
    private String number;

    public User(String name, String password, String number) {
        super(name);
        this.password = password;
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", User.class.getSimpleName(), name, password, number);
    }

}
