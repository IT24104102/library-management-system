-- V3: Seed baseline roles matching stakeholders (RBAC)
INSERT INTO roles (code, name)
VALUES
  ('ADMIN', 'System Administrator'),
  ('CHIEF_LIBRARIAN', 'Chief Librarian'),
  ('LIBRARIAN', 'Librarian'),
  ('ASSISTANT', 'Library Assistant'),
  ('STUDENT', 'University Student'),
  ('IT_SUPPORT', 'IT Support Officer'),
  ('ACADEMIC_COORD', 'Academic Coordinator')
ON DUPLICATE KEY UPDATE name = VALUES(name);

