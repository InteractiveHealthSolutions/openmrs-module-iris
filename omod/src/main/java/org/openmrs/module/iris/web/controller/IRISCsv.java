package org.openmrs.module.iris.web.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleMustStartException;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

import com.mysql.jdbc.StringUtils;

public class IRISCsv {
	public static void createCSV() throws IOException{
		
		String sqlllll = "SELECT iris_output_variable, iris_output_index FROM iris_output_mapping WHERE is_observation = true ORDER BY iris_output_index";
		List<List<Object>> listw = Context.getAdministrationService().executeSQL(sqlllll , true);

		
		String sqlhead = "SELECT iris_variable FROM iris_mapping ORDER BY iris_index";
		List<List<Object>> list = Context.getAdministrationService().executeSQL(sqlhead , true);
		

		String sql = "CALL iris_inputcsv_query()";
		
		List<List<Object>> listdata = Context.getAdministrationService().executeSQL(sql , true);
		
		//csv.populateCsvFromList(listdata, false);

		//System.out.println(csv);
		
		File file = new File("h:\\irisip_20140113.csv");
		FileOutputStream fop = new FileOutputStream(file);
		//fop.write(csv.getCsv(true));
		fop.flush();
		fop.close();
	}

	public static void parseOutput() throws FileNotFoundException{
		File file = new File("h:\\valog.txt"/*"h:\\irisop_20140113.csv"*/);
		Scanner snr = new Scanner(file);
		
		while(snr.hasNextLine()){
			String record = snr.nextLine();
			String[] data = record.split(",", -1);

			String ID = data[0];
			String MALPREV = data[1];
			String HIVPREV = data[2];
			String PREGSTAT = data[3]; 
			String PREGLIK = data[4];
			String PRMAT = data[5];
			String INDET = data[6];
			String CAUSE1 = data[7];
			String LIK1 = data[8];
			String CAUSE2 = data[9];
			String LIK2 = data[10];
			String CAUSE3 = data[11];
			String LIK3 = data[12];
			
			Patient patient = Context.getPatientService().getPatient(Integer.parseInt(ID.trim()));

			String encounterCreatorId = Context.getAdministrationService().getGlobalProperty("iris.output.encounterCreatorId");
			User creator = new User(Integer.parseInt(encounterCreatorId));
			
			Encounter encounter = new Encounter();
			String encounterTypeName = Context.getAdministrationService().getGlobalProperty("iris.output.encounterType");
			EncounterType encounterType = Context.getEncounterService().getEncounterType(encounterTypeName);
			encounter.setEncounterType(encounterType);
			encounter.setPatient(patient);
			encounter.setEncounterDatetime(new Date());
			encounter.setCreator(creator);
			encounter.setDateCreated(new Date());
			encounter.setVoided(false);

			String irisCodObsGroupId = Context.getAdministrationService().getGlobalProperty("iris.output.causeOfDeathGroupConceptId");
			Obs codObs = new Obs();
			codObs.setConcept(new Concept(Integer.parseInt(irisCodObsGroupId)));
			codObs.setCreator(creator);
			codObs.setPerson(new Person(patient.getPatientId()));
			codObs.setEncounter(encounter);
			codObs.setObsDatetime(new Date());

			Set<Obs> obs = new HashSet<Obs>();
			obs.add(codObs);

			String sql = "SELECT iris_output_variable, iris_output_index, c.concept_id, cdt.name FROM iris_output_mapping i LEFT JOIN concept c ON c.concept_id=i.concept_id LEFT JOIN concept_datatype cdt ON c.datatype_id=cdt.concept_datatype_id WHERE is_observation = true ORDER BY iris_output_index";
			List<List<Object>> list = Context.getAdministrationService().executeSQL(sql , true);
			
			for (List<Object> list2 : list) {
				Obs o = new Obs();
				o.setObsGroup(codObs);
				o.setConcept(new Concept((Integer) list2.get(2)));
				o.setPerson(new Person(patient.getPatientId()));
				o.setCreator(creator);
				o.setDateCreated(new Date());
				o.setEncounter(encounter);
				o.setObsDatetime(new Date());
				
				String val = data[Integer.parseInt(list2.get(1).toString())];
				if(!StringUtils.isEmptyOrWhitespaceOnly(val)){
					if(list2.get(3).toString().toLowerCase().contains("text")){
						o.setValueText(val);
					}
					else if(list2.get(3).toString().toLowerCase().contains("numeric")){
						o.setValueNumeric(Double.parseDouble(val));
					}
				}
				obs.add(o);
			}
			
			encounter.setObs(obs );
			
			Context.getEncounterService().saveEncounter(encounter );
		}
		
	}
	
	public static void main(String[] args) throws ModuleMustStartException, DatabaseUpdateException, InputRequiredException {
		Properties props = OpenmrsUtil.getRuntimeProperties("openmrs");
		
		boolean usetest = true;
		
		if (usetest) {
			props.put("connection.username", "root");
			props.put("connection.password", "codbr");
			Context.startup("jdbc:mysql://125.209.94.150:2103/openmrs?autoReconnect=true", "root", "codbr", props);
		} else {
			props.put("connection.username", "root");
			props.put("connection.password", "VA1913wm");
			Context.startup("jdbc:mysql://localhost:3306/openmrs?autoReconnect=true", "root", "VA1913wm", props);
		}
		
		try {
			Context.openSession();
			Context.authenticate("admin", "Admin123");
			createCSV();
			parseOutput();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.closeSession();
		}
	}
}
