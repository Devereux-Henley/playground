-- migration to be applied
create table Requirements(
ID int not null auto_increment,
Name varchar(100) not null,
Description varchar(1000) not null,
ProjectID int not null,
primary key (ID),
foreign key (ProjectID) references Projects(ID) on delete cascade on update cascade
);
