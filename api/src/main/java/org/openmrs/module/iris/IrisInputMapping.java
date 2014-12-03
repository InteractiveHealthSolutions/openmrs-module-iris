package org.openmrs.module.iris;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;

public class IrisInputMapping extends BaseOpenmrsData {

	private int irisMappingId;
	
	private String irisVariable;
	
	private Integer irisIndex;
	
	private Integer conceptId;
	
	private Concept concept;
	
	private String description;
	
	private Boolean observation;
	
	private String acceptCondition;

    public IrisInputMapping() {
		
	}
    
	public int getIrisMappingId() {
		return irisMappingId;
	}

	public void setIrisMappingId(int irisMappingId) {
		this.irisMappingId = irisMappingId;
	}

	public String getIrisVariable() {
		return irisVariable;
	}

	public void setIrisVariable(String irisVariable) {
		this.irisVariable = irisVariable;
	}

	public Integer getIrisIndex() {
		return irisIndex;
	}

	public void setIrisIndex(Integer irisIndex) {
		this.irisIndex = irisIndex;
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

	public Boolean isObservation() {
		return observation;
	}
	
	public Boolean getObservation() {
		return observation;
	}

	public void setObservation(Boolean observation) {
		this.observation = observation;
	}

	public String getAcceptCondition() {
		return acceptCondition;
	}

	public void setAcceptCondition(String acceptCondition) {
		this.acceptCondition = acceptCondition;
	}

	@Override
	public Integer getId() {
		return irisMappingId;
	}

	@Override
	public void setId(Integer id) {
		irisMappingId = id;
	}
}
