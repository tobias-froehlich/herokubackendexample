package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String id;
    private String version;
    private String name;
    private String password;

    public User(
            @JsonProperty("id") String id,
            @JsonProperty("version") String version,
            @JsonProperty("name") String name,
            @JsonProperty("password") String password) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        if (!this.getVersion().equals(other.getVersion())) {
            return false;
        }
        if (!this.getName().equals(other.getName())) {
            return false;
        }
        if (!this.getPassword().equals(other.getPassword())) {
            return false;
        }
        return true;
    }
}
