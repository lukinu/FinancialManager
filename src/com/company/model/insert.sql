PRAGMA foreign_keys = ON;
insert OR REPLACE into accounts (descr, user_name) values ("white", "test");
insert OR REPLACE into accounts (descr, user_name) values ("gray", "test");

insert OR REPLACE into categories (name) values ("Food and Drinks"), ("Cafe"), ("Transport"), ("Health"), ("Other"), ("Income");

insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("Bonus", 200000, "Income", 1, 1442172965);
insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("A new car", -10000, "Transport", 1, 1441172965);
insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("A cup of tea", -20, "Cafe", 1, 1442172965);
insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("A metro ticket", -35, "Transport", 1 , 1443172965);

insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("Bonus", 2000000, "Income", 2, 1442172965);
insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("A new jet", -1000000, "Transport", 2, 1443172965);
insert OR REPLACE into records (descr, amount, category, account_id, recdate) values ("A cup of coffee", -200, "Cafe", 2, 1442142965);
