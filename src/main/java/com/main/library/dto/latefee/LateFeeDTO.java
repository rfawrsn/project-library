package com.main.library.dto.latefee;

public class LateFeeDTO {
    private String id;
    private String bookTitle;
    private String studentNim;
    private int amount;
    private String status;
    private String note;

    public LateFeeDTO(String id, String bookTitle, String studentNim, int amount, String status, String note) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.studentNim = studentNim;
        this.amount = amount;
        this.status = status;
        this.note = note;
    }

    public String getStudentNim() {
        return studentNim;
    }

    public void setStudentNim(String studentNim) {
        this.studentNim = studentNim;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
