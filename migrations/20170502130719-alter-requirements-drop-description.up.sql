-- migration to be applied
ALTER TABLE requirements
      DROP COLUMN description,
      DROP COLUMN name;
