package org.openmrs.module.iris.web.controller;

import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.iris.api.IrisMappingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/module/iris/inputmappings/")
public class IrisInputMappingController {

	private static final String INPUT_MAPPINGS_LIST_VIEW = "/module/iris/inputMapping";

	@RequestMapping(value = "inputMapping.form")
	public String getInputMapping(Map model){
		/*List<IrisInputMapping> ml = Context.getService(IrisMappingService.class).getIrisInputMappings(true, 0, Integer.MAX_VALUE, new String[]{"concept"});
		model.put("inputmappings", ml);*/
		return INPUT_MAPPINGS_LIST_VIEW ;
	}
	
}
