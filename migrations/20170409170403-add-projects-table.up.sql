-- migration to be applied
create table Projects(
ID int not null auto_increment,
Name varchar(100) not null,
Description varchar(1000) not null,
primary key (ID)
);
