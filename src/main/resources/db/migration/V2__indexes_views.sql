-- V2: Helpful indexes and reporting views (per docs scope)

-- Indexes (only those specified)
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);

CREATE INDEX idx_loans_user_book ON loans(user_id, book_id);

CREATE INDEX idx_reservations_book_created ON reservations(book_id, created_at);

CREATE INDEX idx_fines_user_status ON fines(user_id, status);

-- Views (placeholders for reporting)
DROP VIEW IF EXISTS v_popular_books;
CREATE VIEW v_popular_books AS
SELECT
  l.book_id,
  COUNT(*) AS borrow_count
FROM loans l
GROUP BY l.book_id;

DROP VIEW IF EXISTS v_overdue_loans;
CREATE VIEW v_overdue_loans AS
SELECT
  l.id AS loan_id,
  l.user_id,
  GREATEST(DATEDIFF(NOW(), l.due_at), 0) AS days_overdue
FROM loans l
WHERE l.returned_at IS NULL
  AND l.due_at < NOW();
