package com.pillreminder.dto;


public class UserDto {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private int totalMedications;

    private int totalReminders;

    /*
        DEFAULT CONSTRUCTOR
    */

    public UserDto() {
    }

    /*
        ID
    */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
        FULL NAME
    */

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /*
        EMAIL
    */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*
        ROLE
    */

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /*
        TOTAL MEDICATIONS
    */

    public int getTotalMedications() {
        return totalMedications;
    }

    public void setTotalMedications(
            int totalMedications
    ) {
        this.totalMedications =
                totalMedications;
    }

    /*
        TOTAL REMINDERS
    */

    public int getTotalReminders() {
        return totalReminders;
    }

    public void setTotalReminders(
            int totalReminders
    ) {
        this.totalReminders =
                totalReminders;
    }
}