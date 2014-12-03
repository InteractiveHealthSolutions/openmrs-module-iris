package org.openmrs.module.iris.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleMustStartException;
import org.openmrs.module.iris.IrisOutputMapping;
import org.openmrs.module.iris.api.IrisMappingService;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

import com.mysql.jdbc.StringUtils;

public class IrisManager {
	public static String pushDataToIris() throws Exception{
		String irisMedcodConcepts = Context.getAdministrationService().getGlobalProperty("iris.input.medcodConceptIdName");
		if(StringUtils.isEmptyOrWhitespaceOnly(irisMedcodConcepts)){
			throw new IllegalStateException("Concept Names for IRIS COD medcod table not specified in global settings 'iris.input.medcodConceptIdName'");
		}
		
		String sql = "SELECT iris_variable, is_observation, concept_id, iris_index, column_selector_condition FROM iris_input_mapping i WHERE column_selector_condition IS NOT NULL ORDER BY iris_index";
		List<List<Object>> iiml = Context.getAdministrationService().executeSQL(sql , true);
		
		List<List<Object>> datal = Context.getAdministrationService().executeSQL("CALL iris_input_query()" , true);

		String colnames = "";
		for (List<Object> cl : iiml) {//ARR of COL NAMES
			colnames += cl.get(0)+",";
		}
		colnames = colnames.substring(0, colnames.lastIndexOf(","));
		
		for (List<Object> row : datal) {
			String insqr = "INSERT INTO ecodirsIdent ("+colnames+") VALUES " ;

			String rowd = "";
			for (Object object : row) {
				try{
					rowd += Double.valueOf(object.toString()).intValue()+",";
				}
				catch(Exception e){
					rowd += (object==null?object:"'"+object+"'")+",";
				}
			}
			rowd = rowd.substring(0, rowd.lastIndexOf(","));
			
			insqr += " ("+rowd+") ";
			
			IrisUtils.getDbConnection().modifyData(insqr, null);
			
			String[] medcodConcepts = irisMedcodConcepts.split(",");
			String in2sqr = "INSERT INTO ecodirsMedCod (CertificateKey, LineNb, CodeLine, CodeOnly) VALUES " ;
			
			int i=0;
			for (String cncpt : medcodConcepts) {
				Concept c = Context.getConceptService().getConcept(cncpt);
				if(c != null){
					String val = "";
					List<Obs> eos = Context.getObsService().getObservationsByPersonAndConcept(Context.getEncounterService().getEncounter(Integer.parseInt(row.get(0).toString())).getPatient(), c);
					for (Obs obs : eos) {
						if(obs.getConcept().getConceptId()==c.getConceptId() && obs.getEncounter().getEncounterId()==Integer.parseInt(row.get(0).toString())){
							if(obs.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.TEXT)){
								val = obs.getValueText();
							}
							else if(obs.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.CODED)
									&& obs.getValueCoded() != null){
								val = Context.getConceptService().getConcept(obs.getValueCoded().getConceptId()).getShortNameInLocale(Locale.ENGLISH).getName();
							}
						}
					}

					if(val != ""){
						in2sqr += "("+row.get(0)+","+(i++)+", '"+val+"',1),";
					}
				}
			}
			
			in2sqr = in2sqr.substring(0, in2sqr.lastIndexOf(","));
			if(i > 0){
				IrisUtils.getDbConnection().modifyData(in2sqr, null);
			}
		}

		return "Done! "+datal.size()+" records processed.";
	}

	public static List<String> pullDataFromIris() throws Exception {
		User authuser = Context.getAuthenticatedUser();
		String encounterCreator = Context.getAdministrationService().getGlobalProperty("iris.output.encounterCreatorUsername");
		User creator = authuser!=null?authuser:Context.getUserService().getUserByUsername(encounterCreator);
		if(creator == null){
			throw new IllegalStateException("Default username for IRIS COD encounters not specified in global settings 'iris.output.encounterCreatorUsername'");
		}
		
		String encounterTypeName = Context.getAdministrationService().getGlobalProperty("iris.output.encounterType");
		if(StringUtils.isEmptyOrWhitespaceOnly(encounterTypeName)){
			throw new IllegalStateException("Encounter type for IRIS COD encounters not specified in global settings 'iris.output.encounterType'");
		}
		EncounterType encounterType = Context.getEncounterService().getEncounterType(encounterTypeName);
		if(encounterType == null){
			EncounterType enct = new EncounterType(encounterTypeName, "Encounter type for IRIS COD");
			encounterType = Context.getEncounterService().saveEncounterType(enct );
		}
		
		String irisCodObsGroupConcept = Context.getAdministrationService().getGlobalProperty("iris.output.causeOfDeathGroupConceptIdName");
		if(StringUtils.isEmptyOrWhitespaceOnly(irisCodObsGroupConcept)){
			throw new IllegalStateException("Concept Name for IRIS COD obs group not specified in global settings 'iris.output.causeOfDeathGroupConceptIdName'");
		}
		Concept codObsGroupConcept = Context.getConceptService().getConcept(irisCodObsGroupConcept);
		if(codObsGroupConcept == null){
			throw new IllegalStateException("Concept not found for ID/Name specified in global settings 'iris.output.causeOfDeathGroupConceptIdName' for IRIS COD");
		}
		
		String irisPkCol = Context.getAdministrationService().getGlobalProperty("iris.certificateTable.primaryKeyColumn");
		if(StringUtils.isEmptyOrWhitespaceOnly(irisPkCol)){
			throw new IllegalStateException("Primary Key column name for IRIS certifiate table not specified in global settings 'iris.certificateTable.primaryKeyColumn'");
		}
		
		DbConnection con = IrisUtils.getDbConnection();
		List<Map<String, Object>> data = con.getData("SELECT * FROM ecodirsIdent", new Object[]{});
		
		List<IrisOutputMapping> oml = Context.getService(IrisMappingService.class).getIrisOutputMappings(false, 0, 100, new String[]{"concept"});

		List<String> errors = new ArrayList<String>();
		
		for (Map<String, Object> map : data) {
			// if it has not calculated any COD note the row to return as ERROR message. 
			// BUT DELETE IT to be calculated again in next bunch
			
			if(map.get(irisPkCol) == null){
				errors.add(irisPkCol+" column value not found in rows returned by IRIS");
				continue;
			}
			
			Encounter enc = Context.getEncounterService().getEncounter(Integer.parseInt((String) map.get(irisPkCol)));
			if(enc == null){
				errors.add("ERROR : Unidentified data found in rows returned by IRIS against ID "+irisPkCol+" : "+map.get(irisPkCol));
				continue;
			}
			for (IrisOutputMapping om : oml) {
				if(om.getRequired() && !map.containsKey(om.getIrisOutputVariable())){
					errors.add("EVENT : [ID:Type:PersonName]:["+enc.getId()+":"+enc.getEncounterType()+":"+enc.getPatient().getGivenName()+" "+enc.getPatient().getFamilyName()+"] : Required column value not found in data return by IRIS");
				}
			}
			
			Encounter encounter = new Encounter();
			encounter.setEncounterType(encounterType);
			encounter.setPatient(enc.getPatient());
			encounter.setEncounterDatetime(new Date());
			encounter.setCreator(creator);
			encounter.setDateCreated(new Date());
			encounter.setLocation(enc.getLocation());
			encounter.setVoided(false);

			Obs codObs = new Obs();
			codObs.setConcept(codObsGroupConcept);
			codObs.setCreator(creator);
			codObs.setPerson(enc.getPatient());
			codObs.setEncounter(encounter);
			codObs.setObsDatetime(new Date());

			Set<Obs> obs = new HashSet<Obs>();
			obs.add(codObs);

			for (IrisOutputMapping om : oml) {
				if(om.getObservation()!= null && om.getObservation()){
					Obs o = new Obs();
					o.setObsGroup(codObs);
					o.setConcept(om.getConcept());
					o.setPerson(enc.getPatient());
					o.setCreator(creator);
					o.setDateCreated(new Date());
					o.setEncounter(encounter);
					o.setObsDatetime(new Date());
					
					String val = (String) map.get(om.getIrisOutputVariable());
					if(!StringUtils.isEmptyOrWhitespaceOnly(val)){
						if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.TEXT)){
							o.setValueText(val);
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.NUMERIC)){
							o.setValueNumeric(Double.parseDouble(val));
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.CODED)){
							Concept dc = Context.getConceptService().getConcept(val);
							o.setValueCoded(dc);
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.BOOLEAN)){
							o.setValueBoolean(Boolean.valueOf(val));
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.DATE)){
							o.setValueDate(new SimpleDateFormat("yyyy-MM-dd").parse(val));
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.BOOLEAN)){
							o.setValueDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(val));
						}
						else if(om.getConcept().getDatatype().getHl7Abbreviation().equalsIgnoreCase(ConceptDatatype.TIME)){
							o.setValueTime(new SimpleDateFormat("HH:mm:ss").parse(val));
						}
					}
					obs.add(o);
				}
			}
			
			encounter.setObs(obs );
			System.out.println(obs);
			Context.getEncounterService().saveEncounter(encounter);

			con.modifyData("DELETE FROM ecodirsMedCod WHERE "+irisPkCol+" = '"+enc.getEncounterId()+"'", null);
			con.modifyData("DELETE FROM ecodirsIdent WHERE "+irisPkCol+" = '"+enc.getEncounterId()+"'", null);
		}
		
		errors.add(data.size()+" rows processed");
		return errors;
	}
	
	public static void main(String[] args) throws ModuleMustStartException, DatabaseUpdateException, InputRequiredException {
		Properties props = OpenmrsUtil.getRuntimeProperties("openmrs");
		
		boolean usetest = false;
		
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
			Context.getService(IrisMappingService.class).getIrisInputMappings(true, 0, 100, null);
			//createRequiredConcepts();
			//pullDataFromIris();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.closeSession();
		}
	}
}
