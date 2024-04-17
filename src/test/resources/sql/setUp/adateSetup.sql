INSERT INTO member (oauth_id, oauth_platform, name, profile_img, nickname, birth, gender, profession, signature_color)
VALUES ('123', 'GOOGLE', 'hong', '123123', 'kevin', '0928', 'male', 'student', 'red'),
       ('2', 'GOOGLE', 'muny', '123123', 'anna', '0929', 'female', 'developer', 'blue'),
       ('3', 'GOOGLE', 'kim', '123123', 'jenny', '0930', 'female', 'professor', 'black'),
       ('4', 'GOOGLE', 'karina', '123123', 'katarina', '1000', 'female', 'developer', 'pink'),
       ('5', 'GOOGLE', 'down', '123123', 'mark', '1001', 'female', 'teacher', 'skyblue');

INSERT INTO color_type(color, name)
VALUES ('black', '검정'),
       ('red', '빨강'),
       ('white', '하양'),
       ('blue', '파랑'),
       ('violet', '바이올렛');

INSERT INTO adate (title, notes, location, alert_when, repeat_freq, color, adate_name, if_all_day, starts_when,
                   ends_when, calendar_id, reminders, status)
VALUES ('Meeting1', 'Discuss project status', 'Conference Room A', '2024-04-17T10:00:00', '2024-04-17T11:00:00',
        'black', 'Team Meeting', 1, '2024-04-17T10:00:00', '2024-04-17T11:00:00', 'abc123', 101, 'Scheduled'),
       ('Meeting2', 'Discuss project status', 'Conference Room B', '2024-04-18T10:00:00', '2024-04-18T11:00:00', 'red',
        'Client Meeting', 0, '2024-04-18T10:00:00', '2024-04-18T11:00:00', 'def456', 102, 'Scheduled'),
       ('Meeting3', 'Discuss project status', 'Conference Room C', '2024-04-19T10:00:00', '2024-04-19T11:00:00',
        'white', 'Board Meeting', 1, '2024-04-19T10:00:00', '2024-04-19T11:00:00', 'ghi789', 103, 'Scheduled'),
       ('Meeting4', 'Discuss project status', 'Conference Room D', '2024-04-20T10:00:00', '2024-04-20T11:00:00', 'blue',
        'Team Building', 0, '2024-04-20T10:00:00', '2024-04-20T11:00:00', 'jkl012', 104, 'Scheduled'),
       ('Meeting5', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-21T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-21T11:00:00', 'mno345', 105, 'Scheduled'),
       ('Meeting6', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-22T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-22T11:00:00', 'ads234', 105, 'Scheduled'),
       ('Meeting7', 'Discuss project status', 'Conference Room E', '2024-04-21T10:00:00', '2024-04-23T11:00:00',
        'violet', 'Product Launch', 1, '2024-04-21T10:00:00', '2024-04-23T11:00:00', 'qew267', 105, 'Scheduled');




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