SET foreign_key_checks = 0;

TRUNCATE TABLE `announcements`;
TRUNCATE TABLE `comments`;
TRUNCATE TABLE `companies`;
TRUNCATE TABLE `marks`;
TRUNCATE TABLE `real_estates`;
TRUNCATE TABLE `reports`;
TRUNCATE TABLE `users`;

SET foreign_key_checks = 1;

/* Insert new users */
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (1, 0, 'isco@gmail.com', 'Isco', 'Alarcon', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065066665', 'oglašavač', 'isco');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (2, 0, 'sr4@gmail.com', 'Sergio', 'Ramos', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065066665', 'verifikator', 'sr4');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (3, 0, 'david@gmail.com', 'David', 'Beckham', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065066665', 'oglašavač', 'david');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (4, 0, 'bjelica@gmail.com', 'Nemanja', 'Bjelica', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065066765', 'verifikator', 'bjelica');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (5, 0, 'lillard@gmail.com', 'Damian', 'Lillard', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065066635', 'oglašavač', 'damian');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (6, 0, 'lebron@gmail.com', 'LeBron', 'James', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065061665', 'oglašavač', 'james');
INSERT INTO `users` (`u_id`, `u_deleted`, `u_email`, `u_fname`, `u_lname`, `u_password`, `u_telephone`, `u_type`, `u_username`) VALUES (7, 0, 'wiggins@gmail.com', 'Andrew', 'Wiggins', '$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq', '065161665', 'verifikator', 'wiggins');

/* Insert new real estates */
INSERT INTO `real_estates` (`re_id`, `re_area`, `re_deleted`, `re_heating_type`, `re_name`, `re_type`) VALUES ('1', '120', 0, 'coal', 'RealEstate1', 'flat');
INSERT INTO `real_estates` (`re_id`, `re_area`, `re_deleted`, `re_heating_type`, `re_name`, `re_type`) VALUES ('2', '220', 0, 'central_heating', 'RealEstate2', 'house');


/* Insert new announcements */
INSERT INTO `announcements` (`ann_id`, `ann_date_announced`, `ann_date_modified`, `ann_deleted`, `ann_expiration_date`, `ann_price`, `ann_telephone`, `ann_type`, `re_author_id`, `ann_real_estate_id`) VALUES ('1', '1994-01-01', '1994-01-03', 0, '1994-01-20', '50', '0654887612', 'flat', '1', '1');
