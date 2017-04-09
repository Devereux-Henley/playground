-- migration to be applied
create table Teams(
ID int not null auto_increment,
OrganizationID int not null,
Name varchar(100) not null,
primary key (ID),
foreign key (OrganizationID) references Organizations(ID) on delete cascade on update cascade
);
