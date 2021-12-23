create table "user"
(
	id serial
		constraint user_id_pk
			primary key,
	login varchar not null,
	password varchar not null
);

create unique index user_login_uindex
	on "user" (login);

create table category
(
	id serial
		constraint id_category_pk
			primary key,
	name varchar not null
);

create unique index category_name_uindex
	on category (name);

create table item
(
	id serial
		constraint item_id_pk
			primary key,
	barcode varchar not null,
	name varchar not null,
	price decimal not null,
	id_category int not null
		constraint item_category_id_fk
			references category
);

create unique index item_barcode_uindex
	on item (barcode);

create table cart (
	id      serial
		constraint cart_id_pk
			primary key,
	id_user integer not null
		constraint cart_user_id_fk
			references "user"
);

alter table cart
	owner to postgres;

create unique index cart_id_user_uindex
	on cart(id_user);

create table cart_item (
	id_cart integer not null
		constraint cart_item_cart_id_fk
			references cart,
	id_item integer not null
		constraint cart_item_item_id_fk
			references item
);

alter table cart_item
	owner to postgres;

create table "order"
(
	id serial
		constraint order_id_pk
			primary key,
	id_user int not null
		constraint order_user_id_fk
			references "user",
	date timestamp default now() not null,
	amount int not null
);

create table order_item (
	id_order integer not null
		constraint order_item_cart_id_fk
			references "order",
	id_item integer not null
		constraint order_item_item_id_fk
			references item
);

alter table order_item
	owner to postgres;


