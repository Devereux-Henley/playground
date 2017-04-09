-- migration to be applied
create table TeamMembers(
TeamID int not null,
UserID int not null,
primary key (TeamID, UserID),
foreign key (TeamID) references Teams(ID) on delete cascade on update cascade,
foreign key (UserID) references Users(ID) on delete cascade on update cascade
);
