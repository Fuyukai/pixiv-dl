-- https://wiki.postgresql.org/wiki/Aggregate_Random

CREATE OR REPLACE FUNCTION _final_random(anyarray)
 RETURNS anyelement AS
$BODY$
 SELECT $1[array_lower($1,1) + FLOOR((1 + array_upper($1, 1) - array_lower($1, 1))*random())];
$BODY$
LANGUAGE 'sql' IMMUTABLE;

DROP AGGREGATE IF EXISTS random(anyelement);

CREATE AGGREGATE random(anyelement) (
  SFUNC=array_append,
  STYPE=anyarray,
  FINALFUNC=_final_random,
  INITCOND='{}'
);
