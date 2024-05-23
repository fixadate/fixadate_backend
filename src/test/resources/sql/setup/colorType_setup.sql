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



INSERT INTO color_type (color, name, is_default)
VALUES ('yellow', 'ex1', false),
       ('violet', 'ex2', false),
       ('white', 'ex3', false),
       ('orange', 'ex4', false),
       ('green', 'ex5', false),
       ('brown', 'ex6', false),
       ('silver', 'ex7', false),
       ('cyan', 'ex8', false),
       ('magenta', 'ex9', false),
       ('maroon', 'ex10', false),
       ('default1', 'ex8', true),
       ('default2', 'ex9', true),
       ('default3', 'ex10', true);


INSERT INTO adate (title, notes, location, alert_when, repeat_freq, color, adate_name, if_all_day, starts_when,
                   ends_when, calendar_id, reminders, status)
VALUES ('Meeting1', 'Discuss project status', 'Conference Room A', '2024-04-17T10:00:00', '2024-04-17T11:00:00',
        'yellow', 'Team Meeting', 1, '2024-05-01T10:00:00', '2024-05-01T11:00:00', 'abc123', 101, 'Scheduled'),
       ('Meeting2', 'Discuss project status', 'Conference Room B', '2024-04-18T10:00:00', '2024-04-18T11:00:00',
        'violet',
        'Client Meeting', 0, '2024-06-01T10:00:00', '2024-06-01T11:00:00', 'def456', 102, 'Scheduled'),
       ('Meeting3', 'Discuss project status', 'Conference Room C', '2024-04-19T10:00:00', '2024-04-19T11:00:00',
        'white', 'Board Meeting', 1, '2024-07-04T10:00:00', '2024-07-04T11:00:00', 'ghi789', 103, 'Scheduled'),
       ('Meeting4', 'Discuss project status', 'Conference Room D', '2024-04-20T10:00:00', '2024-04-20T11:00:00',
        'orange',
        'Team Building', 0, '2024-04-20T10:00:00', '2024-04-20T11:00:00', 'jkl012', 104, 'Scheduled'),
       ('Meeting5', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-21T11:00:00',
        'brown', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-21T11:00:00', 'mno345', 105, 'Scheduled'),
       ('Meeting6', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-22T11:00:00',
        'silver', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-22T11:00:00', 'ads234', 105, 'Scheduled'),
       ('Meeting7', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-23T11:00:00',
        'cyan', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-23T11:00:00', 'qew267', 105, 'Scheduled'),

       ('Meeting8', 'Discuss marketing strategy', 'Conference Room F', '2024-04-24T10:00:00', '2024-04-24T11:00:00',
        'magenta', 'Marketing Meeting', 1, '2024-03-24T10:00:00', '2024-08-24T11:00:00', 'ert567', 106, 'Scheduled'),
       ('Meeting9', 'Discuss financial report', 'Conference Room G', '2024-04-25T10:00:00', '2024-04-25T11:00:00',
        'maroon', 'Financial Meeting', 0, '2024-03-25T10:00:00', '2024-08-25T11:00:00', 'fgh890', 107, 'Scheduled');



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

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'default1';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'default2';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'default3';



UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE title = 'Meeting1';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE title = 'Meeting2';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE title = 'Meeting3';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE title = 'Meeting4';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE title = 'Meeting5';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'muny')
WHERE title = 'Meeting6';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'kim')
WHERE title = 'Meeting7';


UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'violet')
WHERE title = 'Meeting1';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'white')
WHERE title = 'Meeting2';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'yellow')
WHERE title = 'Meeting3';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'green')
WHERE title = 'Meeting4';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'white')
WHERE title = 'Meeting5';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'green')
WHERE title = 'Meeting6';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'yellow')
WHERE title = 'Meeting7';

UPDATE adate
SET color_type_id = (SELECT id FROM color_type WHERE color = 'orange')
WHERE title = 'Meeting8';
