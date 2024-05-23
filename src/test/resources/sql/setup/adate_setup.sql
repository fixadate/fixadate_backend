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
        'MEMBER');

INSERT INTO color_type(color, name, is_default)
VALUES ('black', '검정', false),
       ('red', '빨강', false),
       ('white', '하양', false),
       ('blue', '파랑', false),
       ('violet', '바이올렛', false);

INSERT INTO adate (title, notes, location, alert_when, repeat_freq, color, adate_name, if_all_day, starts_when,
                   ends_when, calendar_id, reminders, status)
VALUES ('Meeting1', 'Discuss project status', 'Conference Room A', '2024-04-17T10:00:00', '2024-04-17T11:00:00',
        'black', 'Team Meeting', 1, '2024-05-01T10:00:00', '2024-05-01T11:00:00', 'abc123', 101, 'Scheduled'),
       ('Meeting2', 'Discuss project status', 'Conference Room B', '2024-04-18T10:00:00', '2024-04-18T11:00:00', 'red',
        'Client Meeting', 0, '2024-06-01T10:00:00', '2024-06-01T11:00:00', 'def456', 102, 'Scheduled'),
       ('Meeting3', 'Discuss project status', 'Conference Room C', '2024-04-19T10:00:00', '2024-04-19T11:00:00',
        'white', 'Board Meeting', 1, '2024-07-04T10:00:00', '2024-07-04T11:00:00', 'ghi789', 103, 'Scheduled'),
       ('Meeting4', 'Discuss project status', 'Conference Room D', '2024-04-20T10:00:00', '2024-04-20T11:00:00', 'blue',
        'Team Building', 0, '2024-04-20T10:00:00', '2024-04-20T11:00:00', 'jkl012', 104, 'Scheduled'),
       ('Meeting5', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-21T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-21T11:00:00', 'mno345', 105, 'Scheduled'),
       ('Meeting6', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-22T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-22T11:00:00', 'ads234', 105, 'Scheduled'),
       ('Meeting7', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-23T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-23T11:00:00', 'qew267', 105, 'Scheduled'),

       ('Meeting8', 'Discuss marketing strategy', 'Conference Room F', '2024-04-24T10:00:00', '2024-04-24T11:00:00',
        'green', 'Marketing Meeting', 1, '2024-03-24T10:00:00', '2024-08-24T11:00:00', 'ert567', 106, 'Scheduled'),
       ('Meeting9', 'Discuss financial report', 'Conference Room G', '2024-04-25T10:00:00', '2024-04-25T11:00:00',
        'yellow', 'Financial Meeting', 0, '2024-03-25T10:00:00', '2024-08-25T11:00:00', 'fgh890', 107, 'Scheduled'),
       ('Meeting10', 'Discuss HR policies', 'Conference Room H', '2024-04-26T10:00:00', '2024-04-26T11:00:00', 'orange',
        'HR Meeting', 1, '2024-05-26T10:00:00', '2024-08-26T11:00:00', 'hjk123', 108, 'Scheduled'),
       ('Meeting11', 'Discuss sales targets', 'Conference Room I', '2024-04-27T10:00:00', '2024-04-27T11:00:00', 'pink',
        'Sales Meeting', 0, '2024-05-27T10:00:00', '2024-08-27T11:00:00', 'iop456', 109, 'Scheduled'),
       ('Meeting12', 'Discuss IT infrastructure', 'Conference Room J', '2024-04-28T10:00:00', '2024-04-28T11:00:00',
        'cyan', 'IT Meeting', 1, '2024-06-28T10:00:00', '2024-08-28T11:00:00', 'klm789', 110, 'Scheduled'),
       ('Meeting13', 'Discuss customer feedback', 'Conference Room K', '2024-04-29T10:00:00', '2024-04-29T11:00:00',
        'brown', 'Customer Meeting', 0, '2024-02-28T10:00:00', '2024-02-29T11:00:00', 'nop012', 111, 'Scheduled'),
       ('Meeting14', 'Discuss product roadmap', 'Conference Room L', '2024-04-30T10:00:00', '2024-04-30T11:00:00',
        'gray', 'Product Meeting', 1, '2024-02-29T10:00:00', '2024-02-29T11:00:00', 'opq345', 112, 'Scheduled');



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
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting8';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting9';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting10';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting11';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting12';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting13';

UPDATE adate
SET member_id = (SELECT id FROM member WHERE name = 'down')
WHERE title = 'Meeting14';


UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'black';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'red';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'white';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'blue';

UPDATE color_type
SET member_id = (SELECT id FROM member WHERE name = 'hong')
WHERE color = 'violet';
