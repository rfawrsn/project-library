CREATE TABLE late_fees (
  id TEXT PRIMARY KEY,
  borrowed_book_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  status TEXT NOT NULL DEFAULT 'Not Yet Paid',
  note TEXT DEFAULT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (borrowed_book_id) REFERENCES borrowed_books (id)
);