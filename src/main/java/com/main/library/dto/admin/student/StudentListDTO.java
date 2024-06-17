package com.main.library.dto.admin.student;

public class StudentListDTO {
    private String name;
    private String nim;
    private String faculty;
    private String major;

    public StudentListDTO(String name, String nim, String faculty, String major) {
        this.name = name;
        this.nim = nim;
        this.faculty = faculty;
        this.major = major;
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
}
