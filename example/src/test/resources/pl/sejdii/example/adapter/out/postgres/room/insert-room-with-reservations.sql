INSERT INTO room (id, identifier, number_of_seats)
values (1000, 'aedd07ed-efd5-4720-99b0-7ae731df43ff', 4);

INSERT INTO reservation (identifier, reservation_owner_identifier, start_time, end_time, number_of_participants, room_id)
values ('3aeb88fb-5d68-4ec2-85b4-977da4e58b6f', 'EMP0001', '2025-06-01 10:00:00', '2025-06-01 11:00:00', 3, 1000);

INSERT INTO room (id, identifier, number_of_seats)
values (2000, 'abfe1353-8795-411b-ae58-7ca611131bac', 12);

INSERT INTO reservation (identifier, reservation_owner_identifier, start_time, end_time, number_of_participants, room_id)
values ('e8d1e6a9-7241-4f1f-bcff-724a41915047', 'EMP0002', '2025-06-22 11:25:00', '2025-06-22 12:50:00', 9, 2000);

INSERT INTO room (id, identifier, number_of_seats)
values (3000, 'd175d0ba-1a96-4e4a-929b-090c62c31eaf', 3);