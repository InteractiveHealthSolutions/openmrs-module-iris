DROP PROCEDURE iris_inputcsv_query;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `iris_inputcsv_query`()
BEGIN
	DECLARE done INT DEFAULT FALSE;
	
	DECLARE forivm_variable VARCHAR(100);
	DECLARE forivm_concept_id INT;
	DECLARE forivm_concept_type VARCHAR(30);
	DECLARE forivm_index INT;
	DECLARE forivm_accept_condition VARCHAR(5000);
	DECLARE forivm_is_observation INT;
	
-- CSV and OBS Concept mapping are defined in table iris_input_mapping with order required
	DECLARE cObs CURSOR FOR 
			SELECT iris_variable, is_observation, c.concept_id, cdt.name, iris_index, accept_condition FROM iris_input_mapping i 
			LEFT JOIN concept c ON c.concept_id=i.concept_id LEFT JOIN concept_datatype cdt ON c.datatype_id=cdt.concept_datatype_id 
			ORDER BY iris_index;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
-- There is a limit of 61 joins in query, CSV requires 246 observations/data values.
-- To generate CSV for 246-approx OBS we would break query 
-- to create multiple temporary tables with name starting with starting below and index post fixed.
SET @iris_temp_table = 'iris_temp_table';
-- CSV cloumns in start requires data from person table
set @qstaticFields=" SELECT P.patient_id AS ID , E.encounter_id AS EID ";
-- Joins with tables other than OBS, these define unique patients and their unique encounters 
-- IRIS is used to find COD on Verbal Autopsy forms ONLY 
SET @qNonObsJoin = " FROM patient P JOIN encounter E ON P.patient_id = E.patient_id 
				JOIN person PR ON P.patient_id = PR.person_id 
				JOIN encounter_type ET ON E.encounter_type = ET.encounter_type_id AND ET.name IN ('VERBAL AUTOPSY') ";

SET @tempTables = 0;
-- Columns for non-static data (one which would be evaluated from accept condition) only that would be in FINAL generated table for CSV
SET @finalColumns = '';
-- Concept ID of answer YES to compare if answer for OBS was a YES.
SET @YES_CONCEPT_ID = 1065;
-- JOIN parts of query for temp table for OBS of specified concept (from iris_input_mapping table)
SET @qJoinsObs = '';
-- Column NAMES for OBS/data for temp table
SET @qFieldsNonStatic = '';

OPEN cObs;

SET @totalRows = FOUND_ROWS();
-- Current JOIN of OBS in loop for temp table (this is represented by each row of iris_input_mapping for which observation=true)
SET @currentRow = 1;
read_loop: LOOP
FETCH cObs INTO forivm_variable, forivm_is_observation, forivm_concept_id, forivm_concept_type, forivm_index, forivm_accept_condition;
IF done THEN
  LEAVE read_loop;
ELSE 
-- FINAL columns in CSV would be represented by variable_name in each row of iris_input_mapping table
		SET @finalColumns = CONCAT(@finalColumns,IF(@finalColumns IS NULL OR @finalColumns = '','',','),forivm_variable);

		IF forivm_accept_condition IS NOT NULL AND forivm_accept_condition <> '' THEN 
		
		set @evaluationOrAcceptCondition = forivm_accept_condition;
		
		IF forivm_is_observation THEN 
-- Each OBS of iris_input_mapping would be JOINed as o_iris_MAPPING_VARIABLE_NAME, 
-- so wherever it finds OBS in iris accept condition it would replace it with the table alias in query being created
-- EX: OBS.value_numeric < 3 in accept condition for iris mapping ACUTE would be converted to 
-- IF(o_ACUTE.value_numeric <= 3, 'Y', '') AS 'ACUTE'	,
		set @obsJoinAlias = CONCAT("o_",forivm_variable);
		set @replaceObsConditionVarnameWith = CONCAT(@obsJoinAlias, ".");
		set @evaluationOrAcceptCondition = REPLACE(IFNULL(@evaluationOrAcceptCondition,'1<1'), "OBS.", @replaceObsConditionVarnameWith);
		
-- Append JOIN part of query for temp table i.e.
-- LEFT JOIN obs o_ACUTE ON e.encounter_id=o_ACUTE.encounter_id AND P.patient_id=o_ACUTE.person_id 
-- AND o_ACUTE.concept_id=iris_input_mapping.MAPPED_CONCEPT_FOR_VARABLE_IN_CSV 
-- AND o_ACUTE.obs_id =(MAX OBS_ID FOR SELECTED CRITERIA to ensure smoothness of process in case of duplications)
		set @qJoinsObs = CONCAT(@qJoinsObs, " 
			LEFT JOIN obs ", @obsJoinAlias, " ON E.encounter_id=",@obsJoinAlias, ".encounter_id  AND P.patient_id=",@obsJoinAlias, ".person_id AND ", @obsJoinAlias, ".concept_id=", IFNULL(forivm_concept_id,'-1'),
					" AND ", @obsJoinAlias, ".obs_id = (SELECT MAX(obs_id) FROM obs WHERE encounter_id=E.encounter_id AND person_id=P.patient_id AND concept_id=",IFNULL(forivm_concept_id,'-1'),")");
					
		-- End if observation check
		END IF;
-- EX: P.gender = F in accept condition for iris mapping GENDER would be converted to 
-- IF(P.gender = F , 'Y', '') AS 'FEMALE'
		set @qFieldsNonStatic = CONCAT(@qFieldsNonStatic, ',',"
			IF(",IFNULL(@evaluationOrAcceptCondition, ''), ",'Y','') AS '", forivm_variable,"'");

		-- End if condition for accept condition check
		END IF;

-- if joins/columns for obs/data are greater than 50 or temp table was static record only or all rows have been processed 
-- then create new temp table with dynamic name temp[0-x]
		IF @currentRow%50=0 OR @tempTables <= 0 OR @currentRow>=@totalRows THEN 

-- FINAL query for current temp table would be 
-- if 0th table concat person biographic info 
-- if 1-nth append person_id and encounter_id in temptable so that final great table could be joined with different set of obs
			SET @finalQueryString = IF(@tempTables <= 0, CONCAT(@qstaticFields, @qNonObsJoin), CONCAT("SELECT P.patient_id AS ID , e.encounter_id AS EID ",@qFieldsNonStatic, @qNonObsJoin, @qJoinsObs));
			SET @tmpTableName=CONCAT(@iris_temp_table, @tempTables);
			SET @tempTableCreateStmt = CONCAT("CREATE TEMPORARY TABLE ", @tmpTableName, " ", @finalQueryString);
			SET @tempTableDropStmt = CONCAT("DROP TEMPORARY TABLE IF EXISTS ", @tmpTableName);

			PREPARE stmt1 FROM @tempTableDropStmt;
			EXECUTE stmt1; 
			DEALLOCATE PREPARE stmt1;

			PREPARE stmt1 FROM @tempTableCreateStmt;
			EXECUTE stmt1; 
			DEALLOCATE PREPARE stmt1;

-- RESET queryString for next temp table
			SET @finalQueryString = '';
-- if still unprocessed variables/rows in iris_input_mapping increment temp table for next
-- this max temp table would be required to loop over all tables to combine them into a single tableat the end		
			IF @currentRow<@totalRows THEN	
				SET @tempTables = @tempTables +1; 
			END IF;
-- if rows has reached max join / column limit for our temp tables reset join and fields variable container 
-- to restart process for next table
			IF @currentRow%50=0 THEN 
				SET @qJoinsObs = '';
				SET @qFieldsNonStatic = '';
			END IF;
		END IF;
-- increment current row		
		set @currentRow=@currentRow+1;
-- End loop`s IF condition
END IF;
END LOOP;
CLOSE cObs;

-- now loop over all temptables to combine data for csv
SET @loopindex=0;
-- final query string would be like accessible 
-- select temp0.* {table that would have biographic info}, list of columns for OBS from temp0 
-- then for all other temp table join on person_id [ID] and encounter_id EID in loop below
SET @finalQueryString = CONCAT("SELECT ", @finalColumns, " FROM ", @iris_temp_table,@loopindex);

read_loop: LOOP
-- until loopindex is <= #temp tables
    IF @loopindex >= @tempTables THEN
      LEAVE read_loop;
    ELSE
		SET @loopindex = @loopindex +1;

-- append JOIN next temp table on ID and EID
		SET @finalQueryString = CONCAT(@finalQueryString, " JOIN ", @iris_temp_table,@loopindex, " USING(ID, EID) ");
	END IF;
  END LOOP;

PREPARE stmt1 FROM @finalQueryString;
EXECUTE stmt1; 
DEALLOCATE PREPARE stmt1;

END$$
DELIMITER ;
