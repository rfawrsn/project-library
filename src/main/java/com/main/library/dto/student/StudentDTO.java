package com.main.library.dto.student;

public class StudentDTO {
    private String id;
    private String name;
    private String nim;
    private String faculty;
    private String major;
    private String password;

    public StudentDTO(String id, String name, String nim, String faculty, String major, String password) {
        this.id = id;
        this.name = name;
        this.nim = nim;
        this.faculty = faculty;
        this.major = major;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
