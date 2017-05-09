-- migration to be applied
INSERT INTO users (first_name, last_name, user_name, password_hash)
VALUES ('dev', 'henley', 'devo',
'bcrypt+sha512$54a7181bea5a720450a2dd4371097dd6$12$f40d595894603b380bbade92a15610938db622671f923e6b'),
('dude', 'guy', 'dannik',
'bcrypt+sha512$54a7181bea5a720450a2dd4371097dd6$12$f40d595894603b380bbade92a15610938db622671f923e6b'),
('jimmy', 'billy', 'bob',
'bcrypt+sha512$54a7181bea5a720450a2dd4371097dd6$12$f40d595894603b380bbade92a15610938db622671f923e6b');
