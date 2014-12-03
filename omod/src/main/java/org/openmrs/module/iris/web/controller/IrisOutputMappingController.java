package org.openmrs.module.iris.web.controller;

import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.iris.IrisOutputMapping;
import org.openmrs.module.iris.api.IrisMappingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/module/iris/outputmappings/")
public class IrisOutputMappingController {

	private static final String OUTPUT_MAPPINGS_LIST_VIEW = "/module/iris/outputMapping";

	@RequestMapping(value = "outputMapping.form")
	public String getOutputMapping(Map model){
		List<IrisOutputMapping> ml = Context.getService(IrisMappingService.class).getIrisOutputMappings(true, 0, Integer.MAX_VALUE, new String[]{"concept"});
		model.put("outputmappings", ml);
		return OUTPUT_MAPPINGS_LIST_VIEW ;
	}
	
	@RequestMapping(value = "generateOutputMappingConcepts.form")
	public String generateInputMappingConcepts(Map model){
		/*List<IrisInputMapping> ml = Context.getService(IrisMappingService.class).getIrisInputMappings(true, 0, Integer.MAX_VALUE, new String[]{"concept"});
		model.put("inputmappings", ml);*/
		try{
			IrisUtils.createRequiredConcepts();
		}
		catch(Exception e){
			model.put("message", e.getMessage());
			e.printStackTrace();
		}
		
		getOutputMapping(model);
		model.put("message", "Successfully reset all concepts with deafult ones.");
		return OUTPUT_MAPPINGS_LIST_VIEW ;
	}
}
