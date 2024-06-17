package com.main.library.session;

import com.main.library.dto.student.StudentDTO;

public class StudentSession {
    private static StudentSession instance;
    private StudentDTO student;

    private StudentSession() {
        // private constructor to prevent instantiation
    }

    public static StudentSession getInstance() {
        if (instance == null) {
            instance = new StudentSession();
        }
        return instance;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void purgeSession() {
        student = null;
    }
}
