-- migration to be applied
create table Organizations(
ID int not null auto_increment,
Name varchar(100) unique not null,
Description varchar(1000) not null,
primary key (ID)
);
