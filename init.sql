SET AUTOCOMMIT TRUE;

INSERT INTO users (username, email, password_hash, role)
VALUES 
('admin', 'admin@mail.com', 'hash1', 'admin'),
('moderator1', 'mod@mail.com', 'hash2', 'moderator'),
('ivan', 'ivan@mail.com', 'hash3', 'user'),
('anna', 'anna@mail.com', 'hash4', 'user');

INSERT INTO categories (title, description, user_id)
VALUES 
('Общий раздел', 'Общие обсуждения', 1),
('Программирование', 'Вопросы по разработке', 1);

INSERT INTO topics (category_id, user_id, title, status)
VALUES 
(0, 0, 'Добро пожаловать', 'normal'),
(0, 1, 'Важная информация', 'pinned'),
(1, 2, 'Вопрос по SQL', 'locked'),
(1, 3, 'Старое обсуждение', 'archived');

INSERT INTO posts (topic_id, user_id, content)
VALUES 
(0, 0, 'Привет всем!'),
(0, 2, 'Привет! Рад видеть.'),
(2, 2, 'Как работает JOIN?');

INSERT INTO posts (topic_id, user_id, parent_id, content)
VALUES
(0, 1, 0, 'Спасибо!');

INSERT INTO attachment (post_id, path_to_file, type)
VALUES
(0, '/files/image1.png', 'image'),
(0, '/files/video1.mp4', 'video'),
(1, '/files/doc1.pdf', 'document'),
(2, '/files/archive.zip', 'other');

INSERT INTO post_views (post_id, user_id)
VALUES
(0, 1),
(0, 2),
(1, 0),
(2, 3);

COMMIT;