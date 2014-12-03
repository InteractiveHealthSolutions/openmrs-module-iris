package org.openmrs.module.iris.web.controller;

import static org.openmrs.module.iris.constants.IrisConstants.*;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.User;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.iris.IrisOutputMapping;
import org.openmrs.module.iris.api.IrisMappingService;

import com.mysql.jdbc.StringUtils;

public class IrisUtils {

	public static DbConnection getDbConnection() {
		String url = Context.getAdministrationService().getGlobalProperty(URL_PROPERTY);
		String user = Context.getAdministrationService().getGlobalProperty(USERNAME_PROPERTY);
		String pwd = Context.getAdministrationService().getGlobalProperty(PASSWORD_PROPERTY);
		String driver = Context.getAdministrationService().getGlobalProperty(DRIVER_PROPERTY);
        
        if(StringUtils.isEmptyOrWhitespaceOnly(url) || StringUtils.isEmptyOrWhitespaceOnly(user) 
        		|| StringUtils.isEmptyOrWhitespaceOnly(pwd) || StringUtils.isEmptyOrWhitespaceOnly(driver)){
        	throw new NoSuchElementException("IRIS DB server configuration should be completed before accessing the service");
        }
        
		return new DbConnection(user, pwd, url, driver);
	}

	public static Concept getOrCreateConcept(String name, ConceptClass cls, ConceptDatatype datatype, User creator){
		Concept cpt = new Concept();
		cpt.setConceptClass(cls);
		cpt.setCreator(creator);
		cpt.setDatatype(datatype);
		cpt.setDateCreated(new Date());
		ConceptName fullySpecifiedName = new ConceptName(name, Locale.ENGLISH);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED );
		fullySpecifiedName.setCreator(creator);
		fullySpecifiedName.setDateCreated(new Date());
		cpt.setFullySpecifiedName(fullySpecifiedName);
		cpt.setPreferredName(fullySpecifiedName);

		System.out.println(cpt+":"+name);
		Concept savedCpt = Context.getConceptService().getConcept(name);
		if(savedCpt == null) {
			savedCpt = Context.getConceptService().saveConcept(cpt);
		}
		System.out.println(cpt+":SAVED");
		return savedCpt;				
	}
	
	public static void createRequiredConcepts() {
		ConceptClass ccls = Context.getConceptService().getConceptClassByName("Finding");
		ConceptDatatype cdt = Context.getConceptService().getConceptDatatypeByName("Text");

		List<IrisOutputMapping> oml = Context.getService(IrisMappingService.class).getIrisOutputMappings(false, 0, 100, new String[]{"concept"});
		for (IrisOutputMapping iom : oml) {
			if(iom.getObservation() != null && iom.getObservation()){// create concepts for Output mapping
				String name = null;
				if(iom.getIrisOutputVariable().equalsIgnoreCase("UCCode")){
					name = "CAUSE OF DEATH FROM ALGORITHM";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("MainInjury")){
					name = "Main Injury that Lead to Death";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("Status")){
					name = "COD Status";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("Reject")){
					name = "Record Data Rejected in Processing";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("Coding")){
					name = "Coding Type";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("CodingVersion")){
					name = "Coding Version";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("CodingFlags")){
					name = "Coding Flags";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("SelectedCodes")){
					name = "Selected Codes for Cause of Death";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("SubstitutedCodes")){
					name = "Substituted Codes for Cause of Death";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("ErnCodes")){
					name = "Ern Codes";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("AcmeCodes")){
					name = "Acme Codes";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("MultipleCodes")){
					name = "Multiple Codes";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("Comments")){
					name = "PROCEDURE COMMENT";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("FreeText")){
					name = "Free Text Comment";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("ToDoList")){
					name = "TODO List";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("CoderReject")){
					name = "Coder Reject";
				}
				else if(iom.getIrisOutputVariable().equalsIgnoreCase("DiagnosisModified")){
					name = "Diagnosis Modified";
				}
				
				Concept cpt = new Concept();
				cpt.setConceptClass(ccls);
				cpt.setCreator(new User(2));
				cpt.setDatatype(cdt);
				cpt.setDateCreated(new Date());
				ConceptName fullySpecifiedName = new ConceptName(name, Locale.ENGLISH);
				fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED );
				fullySpecifiedName.setCreator(new User(2));
				fullySpecifiedName.setDateCreated(new Date());
				cpt.setFullySpecifiedName(fullySpecifiedName);
				cpt.setPreferredName(fullySpecifiedName);
	
				System.out.println(cpt+":"+name);
				Concept savedCpt = Context.getConceptService().getConcept(name);
				if(savedCpt == null) {
					savedCpt = Context.getConceptService().saveConcept(cpt);
				}
				System.out.println(cpt+":SAVED");				
				iom.setConcept(savedCpt);
				Context.getService(IrisMappingService.class).save(iom);
			}
		}
	}
	
	public static void main(String[] args) {
		
	}
}
