package se.vgregion.ifeedpoc.model;

public class User {

    private long userId;
    private String screenName;
    private String firstName;
    private String lastName;
    private String emailAddress;

    public User() {
    }

    public User(long userId, String screenName, String firstName, String lastName, String emailAddress) {
        this.userId = userId;
        this.screenName = screenName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    public long getUserId() {
      return userId;
    }

    public void setUserId(long userId) {
      this.userId = userId;
    }

    public String getScreenName() {
          return screenName;
      }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
