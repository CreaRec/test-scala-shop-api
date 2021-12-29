alter table order_item drop constraint order_item_cart_id_fk;

alter table order_item
	add constraint order_item_cart_id_fk
		foreign key (id_order) references orders
			on update cascade on delete cascade;

