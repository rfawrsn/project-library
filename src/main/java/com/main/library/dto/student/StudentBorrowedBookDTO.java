package com.main.library.dto.student;

public class StudentBorrowedBookDTO {
    private String id;
    private String bookId;
    private String studentNim;
    private String title;
    private String borrowedDate;
    private String deadlineDate;
    private boolean isReturned;
    private String status;

    public StudentBorrowedBookDTO(String id, String bookId, String studentNim, String title, String borrowedDate, String deadlineDate, boolean isReturned, String status) {
        this.id = id;
        this.bookId = bookId;
        this.studentNim = studentNim;
        this.title = title;
        this.borrowedDate = borrowedDate;
        this.deadlineDate = deadlineDate;
        this.isReturned = isReturned;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStudentNim() {
        return studentNim;
    }

    public void setStudentNim(String studentNim) {
        this.studentNim = studentNim;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(String borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
