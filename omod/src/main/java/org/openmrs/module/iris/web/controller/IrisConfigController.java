package org.openmrs.module.iris.web.controller;

import static org.openmrs.module.iris.constants.IrisConstants.DRIVER_PROPERTY;
import static org.openmrs.module.iris.constants.IrisConstants.PASSWORD_PROPERTY;
import static org.openmrs.module.iris.constants.IrisConstants.URL_PROPERTY;
import static org.openmrs.module.iris.constants.IrisConstants.USERNAME_PROPERTY;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/iris/configureIris.form")
public class IrisConfigController
{
    protected final Log log = LogFactory.getLog( getClass() );
    
    @RequestMapping(method = RequestMethod.GET)
    public void showConfigForm( ModelMap model )
    {
    	GlobalProperty url = Context.getAdministrationService().getGlobalPropertyObject(URL_PROPERTY);
        GlobalProperty user = Context.getAdministrationService().getGlobalPropertyObject(USERNAME_PROPERTY);
        GlobalProperty pwd = Context.getAdministrationService().getGlobalPropertyObject(PASSWORD_PROPERTY);
        GlobalProperty driver = Context.getAdministrationService().getGlobalPropertyObject(DRIVER_PROPERTY);
        
        if(url == null) 
        	Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(URL_PROPERTY, "", "IRIS Certificate DB server URL"));
        if(user == null)
        	Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(USERNAME_PROPERTY, "", "IRIS Certificate DB server username"));
        if(pwd == null) 
        	Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(PASSWORD_PROPERTY, "", "IRIS Certificate DB server password"));
        if(driver == null) 
        	Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(DRIVER_PROPERTY, "com.mysql.jdbc.Driver", "IRIS Certificate DB driver class"));
    }
}
