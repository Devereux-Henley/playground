create table Organizations(
	ID int not null auto_increment,
	Name varchar(100) unique not null,
	Description varchar(1000) not null,
	primary key (ID)
);

create table Projects(
	ID int not null auto_increment,
	Name varchar(100) not null,
	Description varchar(1000) not null,
	primary key (ID)
);

create table Teams(
	ID int not null auto_increment,
	OrganizationID int not null,
	Name varchar(100) not null,
	primary key (ID),
	foreign key (OrganizationID) references Organizations(ID) on delete cascade on update cascade
);

create table TeamProjects(
	TeamID int not null,
	ProjectID int not null,
	primary key (TeamID, ProjectID),
	foreign key (TeamID) references Teams(ID) on delete cascade on update cascade,
	foreign key (ProjectID) references Projects(ID) on delete cascade on update cascade
);

create table Users(
	ID int not null auto_increment,
	FirstName varchar(100) not null,
	LastName varchar(100) not null,
	UserName varchar(100) not null,
	PasswordHash char(64) not null,
	primary key (ID, UserName)
);

create table TeamMembers(
	TeamID int not null,
	UserID int not null,
	primary key (TeamID, UserID),
	foreign key (TeamID) references Teams(ID) on delete cascade on update cascade,
	foreign key (UserID) references Users(ID) on delete cascade on update cascade
);

create table CustomerRequirements(
	ID int not null auto_increment,
	Name varchar(100) not null,
	Description varchar(1000) not null,
	ProjectID int not null,
	primary key (ID),
	foreign key (ProjectID) references Projects(ID) on delete cascade on update cascade
);

create table HighLevelRequirements(
	ID int not null auto_increment,
	Name varchar(100) not null,
	Description varchar(1000) not null,
	CustomerRequirementID int not null,
	primary key (ID),
	foreign key (CustomerRequirementID) references CustomerRequirements(ID) on delete cascade on update cascade
);

create table LowLevelRequirements(
	ID int not null auto_increment,
	Name varchar(100) not null,
	Description varchar(1000) not null,
	HighLevelRequirementID int not null,
	primary key (ID),
	foreign key (HighLevelrequirementID) references HighLevelRequirements(ID) on delete cascade on update cascade
);

create table TestCases(
	ID int not null auto_increment,
	Name varchar(100) not null,
	Description varchar(1000) not null,
	LowLevelRequirementID int not null,
	primary key (ID),
	foreign key (LowLevelrequirementID) references LowLevelRequirements(ID) on delete cascade on update cascade
);
