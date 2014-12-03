package org.openmrs.module.iris.web.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.User;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleMustStartException;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

public class ConceptGenerator {

	public static void main(String[] args) throws FileNotFoundException, ModuleMustStartException, DatabaseUpdateException, InputRequiredException {
		Scanner snr = new Scanner(new File("G:\\CODBR\\32778Rfixed.csv"));
		snr.nextLine();// skip headings
		snr.nextLine();// skip 2nd heading
		
		String classname = "WHO ICD10";
		String datatype = "N/A";
		
		Properties props = OpenmrsUtil.getRuntimeProperties("openmrs");
		
		boolean usetest = true;
		
		if (usetest) {
			props.put("connection.username", "root");
			props.put("connection.password", "63WJFdd8");
			Context.startup("jdbc:mysql://202.141.249.106:3306/openmrs?autoReconnect=true", "root", "63WJFdd8", props);
		} else {
			props.put("connection.username", "root");
			props.put("connection.password", "VA1913wm");
			Context.startup("jdbc:mysql://localhost:3306/openmrs?autoReconnect=true", "root", "VA1913wm", props);
		}
		
		try {
			Context.openSession();
			Context.authenticate("admin", "Admin123");
			
			ConceptClass ccls = Context.getConceptService().getConceptClassByName(classname);
			if(ccls == null){
				ConceptClass cc = new ConceptClass();
				cc.setCreator(new User(2));
				cc.setDateCreated(new Date());
				cc.setName(classname);
				cc.setDescription("Class for IRIS required ICD10 symptoms and findings");
				ccls = Context.getConceptService().saveConceptClass(cc);
			}
			
			ConceptDatatype cdt = Context.getConceptService().getConceptDatatypeByName(datatype);
			
			while(snr.hasNext()){
				String line = snr.nextLine(); // move to next record and get current line

				String code = line.substring(0, line.indexOf(",")).replace(".", "").replace("(", "").replace(")", "").trim();//make icd alpha numeric only
				String name = line.substring(line.indexOf(",")+1, line.lastIndexOf(",")).replace("\"", "").trim();
				String codeDotted = line.substring(0, line.indexOf(",")).replace("(", "").replace(")", "").trim();//make icd alpha numeric only
				String nameWOCode = line.substring(line.indexOf(codeDotted)+2, line.lastIndexOf(",")).replace("\"", "").trim();

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
	
				// code is not actual ICD10 code but rather Serial# of parent then donot set it as a separate name
				if(code.matches("^[A-Za-z]\\d\\d.*")){
					cpt.addDescription(new ConceptDescription("DONOT EDIT SHORT NAME. IT IS USED BY IRIS", Locale.ENGLISH));
					
					ConceptName shortName = new ConceptName(code, Locale.ENGLISH);
					shortName.setConceptNameType(ConceptNameType.SHORT);
					cpt.setShortName(shortName);
					
					ConceptName conceptNameCodeDotted = new ConceptName(codeDotted, Locale.ENGLISH);
					conceptNameCodeDotted.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
					cpt.addName(conceptNameCodeDotted);
					
					ConceptName conceptNameWOCode = new ConceptName(nameWOCode, Locale.ENGLISH);
					conceptNameWOCode.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
					cpt.addName(conceptNameWOCode);
				}
				/*_DbConnection con = new _DbConnection();
				con.setDriverClassName("com.mysql.jdbc.Driver");
				con.setPassword("VA1913wm");
				con.setUrl("jdbc:mysql://localhost:3306/certificates?autoReconnect=true");
				con.setUsername("root");*/
				
				System.out.println(cpt+":"+name);
				Concept excpt = Context.getConceptService().getConcept(name);
				if(excpt == null) {
					Context.getConceptService().saveConcept(cpt);
					System.out.println(cpt+":SAVED");
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			Context.closeSession();
		}
	}
}
