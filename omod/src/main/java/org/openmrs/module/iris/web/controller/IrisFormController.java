/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.iris.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class configured as controller using annotation and mapped with the URL of 'module/basicmodule/basicmoduleLink.form'.
 */
@Controller
@RequestMapping(value = "/module/iris/iohandler/")
public class IrisFormController{
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	public final static String IRIS_IO_FORM_VIEW = "/module/iris/irisioactivityForm";
	
	@RequestMapping(value = "irisioactivityForm.form")
	public String showIOForm(Map model) throws IOException{
		return IRIS_IO_FORM_VIEW;
	}
	
	@RequestMapping(value = "pushDataToIris.form")
	public @ResponseBody String pushData() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			map.put("message", IrisManager.pushDataToIris());
		}
		catch(Exception e){
			map.put("message", e.getMessage());
			e.printStackTrace();
		}
		ObjectMapper converter = new ObjectMapper();
		return converter.writeValueAsString(map);
	}
	
	@RequestMapping(value = "pullIrisData.form")
	public @ResponseBody String pullData() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			map.put("message", IrisManager.pullDataFromIris());
		}
		catch(Exception e){
			map.put("message", e.getMessage());
			e.printStackTrace();
		}
		ObjectMapper converter = new ObjectMapper();
		return converter.writeValueAsString(map);
	}
}
