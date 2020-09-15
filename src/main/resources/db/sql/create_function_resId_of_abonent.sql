CREATE OR REPLACE FUNCTION resId_of_abonent(in aN bigint) returns int AS
		'SELECT res.id from abonent
			inner join fider on abonent.fider_id=fider.id
				inner join tp on tp.id = fider.tp_id
					inner join part on part.id=tp.part_id
						inner join line on line.id=part.line_id
							inner join section on section.id=line.section_id
								inner join substation on substation.id=section.substation_id
									inner join res on res.id=substation.res_id
			WHERE abonent.account_number=$1'
	LANGUAGE SQL;

-- SELECT * FROM resId_of_abonent(14044700191);


	
	
