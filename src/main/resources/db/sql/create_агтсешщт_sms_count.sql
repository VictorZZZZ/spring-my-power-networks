/**
  Количество СМС за период
  */

create or replace function sms_count(date_from timestamptz, date_to timestamptz, resId int)
    returns bigint
as
$body$
SELECT COUNT(*) From order_items oi
                         join orders on orders.id=oi.order_id
where
        oi.order_id IN (
        SELECT id from orders o where created between date_from AND date_to
    )
  AND orders.user_id IN (SELECT users.id from users where users.res_id=resId);
$body$
    language sql;
