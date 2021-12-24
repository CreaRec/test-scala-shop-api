drop table cart_item;
drop table cart;

create table user_item
(
	id_user int not null
		constraint user_item_users_id_fk
			references users,
	id_item int not null
		constraint user_item_item_id_fk
			references item
);

