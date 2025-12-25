create extension if not exists btree_gist;

alter table bookings
    add constraint booking_no_overlap
        exclude using gist (
        room_id with =,
        tstzrange(start_at, end_at) with &&
        );