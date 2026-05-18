SET AUTOCOMMIT TRUE;

INSERT INTO users (id, username, email, password_hash, role)
VALUES
(1, 'admin', 'admin@mail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin'),
(2, 'moderator1', 'mod@mail.com', 'b9fb3d4603180d1968e382f5b3987c1a093496edcd1156e24460ebb14765860c', 'moderator'),
(3, 'ivan', 'ivan@mail.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user'),
(4, 'anna', 'anna@mail.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user');

INSERT INTO categories (id, title, description, user_id)
VALUES
(1, 'Общий раздел', 'Общие обсуждения', 1),
(2, 'Программирование', 'Вопросы по разработке', 2);

INSERT INTO topics (id, category_id, user_id, title, status)
VALUES
(1, 1, 1, 'Добро пожаловать', 'normal'),
(2, 1, 2, 'Важная информация', 'pinned'),
(3, 2, 3, 'Вопрос по SQL', 'locked'),
(4, 2, 4, 'Старое обсуждение', 'archived');

INSERT INTO posts (id, topic_id, user_id, content)
VALUES
(1, 1, 1, 'Привет всем!'),
(2, 1, 3, 'Привет! Рад видеть.'),
(3, 3, 3, 'Как работает JOIN?');

INSERT INTO posts (id, topic_id, user_id, parent_id, content)
VALUES
(4, 1, 2, 1, 'Спасибо!');

INSERT INTO attachment (id, post_id, path_to_file, type)
VALUES
(1, 1, '/files/image1.png', 'image'),
(2, 1, '/files/video1.mp4', 'video'),
(3, 2, '/files/doc1.pdf', 'document'),
(4, 3, '/files/archive.zip', 'other');

INSERT INTO post_views (post_id, user_id)
VALUES
(1, 2),
(1, 3),
(2, 1),
(3, 4);

COMMIT;
