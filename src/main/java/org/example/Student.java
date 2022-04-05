package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Student implements Serializable {
    private String firstName;
    private String lastName;

    public Student(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        if (!this.getFirstName().equals(other.getFirstName())) {
            return false;
        }
        if (!this.getLastName().equals(other.getLastName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
