-- migration to be applied
create table TeamProjects(
TeamID int not null,
ProjectID int not null,
primary key (TeamID, ProjectID),
foreign key (TeamID) references Teams(ID) on delete cascade on update cascade,
foreign key (ProjectID) references Projects(ID) on delete cascade on update cascade
);
