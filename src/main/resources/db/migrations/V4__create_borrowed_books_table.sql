CREATE TABLE borrowed_books (
  id TEXT PRIMARY KEY,
  book_id INTEGER NOT NULL,
  student_id INTEGER NOT NULL,
  borrowed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  returned_at DATETIME DEFAULT NULL,
  deadline_at DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (book_id) REFERENCES books (id),
  FOREIGN KEY (student_id) REFERENCES students (id)
);