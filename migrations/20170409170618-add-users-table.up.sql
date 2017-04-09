-- migration to be applied
create table Users(
ID int not null auto_increment,
FirstName varchar(100) not null,
LastName varchar(100) not null,
UserName varchar(100) not null,
PasswordHash char(64) not null,
primary key (ID, UserName)
);
