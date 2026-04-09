package model;

import java.io.Serializable;

public class Criminal implements Serializable {

    private static final long serialVersionUID = 1L;

    private int criminalId;
    private String name;
    private int age;
    private String crimeType;

    public Criminal(int criminalId, String name, int age, String crimeType) {
        this.criminalId = criminalId;
        this.name = name;
        this.age = age;
        this.crimeType = crimeType;
    }

    public int getCriminalId() { return criminalId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCrimeType() { return crimeType; }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public static class Builder {

        private int criminalId;
        private String name;
        private int age;
        private String crimeType;

        public Builder criminalId(int criminalId) {
            this.criminalId = criminalId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder crimeType(String crimeType) {
            this.crimeType = crimeType;
            return this;
        }

        public Criminal build() {
            return new Criminal(criminalId, name, age, crimeType);
        }
    }
}