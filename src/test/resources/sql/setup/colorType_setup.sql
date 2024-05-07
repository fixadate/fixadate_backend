INSERT INTO member (id, oauth_id, oauth_platform, name, profile_img, nickname, birth, gender, profession,
                    signature_color, email, role)
VALUES ('101', '123', 'GOOGLE', 'hong', '123123', 'kevin', '0928', 'male', 'student', 'red', 'hong@example.com',
        'MEMBER'),
       ('102', '2', 'GOOGLE', 'muny', '123123', 'anna', '0929', 'female', 'developer', 'blue', 'muny@example.com',
        'MEMBER'),
       ('103', '3', 'GOOGLE', 'kim', '123123', 'jenny', '0930', 'female', 'professor', 'black', 'kim@example.com',
        'MEMBER'),
       ('104', '4', 'GOOGLE', 'karina', '123123', 'katarina', '1000', 'female', 'developer', 'pink',
        'karina@example.com', 'MEMBER'),
       ('105', '5', 'GOOGLE', 'down', '123123', 'mark', '1001', 'female', 'teacher', 'skyblue', 'down@example.com',
        'MEMBER'),

       ('106', '6', 'GOOGLE', 'choi', '123123', 'james', '1002', 'male', 'student', 'yellow', 'choi@example.com',
        'MEMBER'),
       ('107', '7', 'GOOGLE', 'lee', '123123', 'sara', '1003', 'female', 'designer', 'orange', 'lee@example.com',
        'MEMBER'),
       ('108', '8', 'GOOGLE', 'park', '123123', 'peter', '1004', 'male', 'engineer', 'green', 'park@example.com',
        'MEMBER'),
       ('109', '9', 'GOOGLE', 'cho', '123123', 'lisa', '1005', 'female', 'manager', 'purple', 'cho@example.com',
        'MEMBER'),
       ('110', '10', 'GOOGLE', 'han', '123123', 'alex', '1006', 'male', 'developer', 'white', 'han@example.com',
        'MEMBER');



INSERT INTO color_type (color, name)
VALUES ('yellow', 'ex1'),
       ('violet', 'ex2'),
       ('white', 'ex3'),
       ('orange', 'ex4'),
       ('green', 'ex5'),
       ('brown', 'ex6'),
       ('silver', 'ex7'),
       ('cyan', 'ex8'),
       ('magenta', 'ex9'),
       ('maroon', 'ex10');

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'yellow';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'violet';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'white';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'orange';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'green';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'muny')
WHERE color = 'brown';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'kim')
WHERE color = 'silver';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'karina')
WHERE color = 'cyan';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE color = 'magenta';