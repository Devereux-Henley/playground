-- migration to be applied
create table RequirementsPaths(
Ancestor int not null,
Descendant int not null,
primary key (Ancestor, Descendant),
foreign key (Ancestor) references Requirements(ID),
foreign key (Descendant) references Requirements(ID)
);
