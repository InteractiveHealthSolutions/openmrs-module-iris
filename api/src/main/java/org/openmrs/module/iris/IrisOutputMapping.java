package org.openmrs.module.iris;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;

public class IrisOutputMapping extends BaseOpenmrsData {

	private int irisOutputMappingId;
	
	private String irisOutputVariable;

	private int irisOutputIndex;

	private Integer conceptId;
	
	private Concept concept;
	
	private String description;
	
	private Boolean observation;
	
	private Boolean required;
	
    public IrisOutputMapping() {
		
	}
    

	public int getIrisOutputMappingId() {
		return irisOutputMappingId;
	}


	public void setIrisOutputMappingId(int irisOutputMappingId) {
		this.irisOutputMappingId = irisOutputMappingId;
	}


	public String getIrisOutputVariable() {
		return irisOutputVariable;
	}


	public void setIrisOutputVariable(String irisOutputVariable) {
		this.irisOutputVariable = irisOutputVariable;
	}


	public int getIrisOutputIndex() {
		return irisOutputIndex;
	}


	public void setIrisOutputIndex(int irisOutputIndex) {
		this.irisOutputIndex = irisOutputIndex;
	}


	public Integer getConceptId() {
		return conceptId;
	}


	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}


	public Concept getConcept() {
		return concept;
	}


	public void setConcept(Concept concept) {
		this.concept = concept;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Boolean getObservation() {
		return observation;
	}


	public void setObservation(Boolean observation) {
		this.observation = observation;
	}


	public Boolean getRequired() {
		return required;
	}


	public void setRequired(Boolean required) {
		this.required = required;
	}


	@Override
	public Integer getId() {
		return irisOutputMappingId;
	}

	@Override
	public void setId(Integer id) {
		irisOutputMappingId = id;
	}
}
