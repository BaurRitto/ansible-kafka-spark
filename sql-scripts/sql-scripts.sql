create table comments_final(
	postid INT not null,
	id int not null,
	name varchar not null,
	email varchar not null,
	body varchar not null,
	idFromFile int not null,
	"current_date" date not null
)