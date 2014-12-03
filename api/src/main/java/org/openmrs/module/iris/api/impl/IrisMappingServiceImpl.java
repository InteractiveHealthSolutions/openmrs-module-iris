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
package org.openmrs.module.iris.api.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.iris.IrisInputMapping;
import org.openmrs.module.iris.IrisOutputMapping;
import org.openmrs.module.iris.api.IrisMappingService;
import org.openmrs.module.iris.api.db.IrisMappingDAO;

/**
 * It is a default implementation of {@link IrisMappingService}.
 */
public class IrisMappingServiceImpl extends BaseOpenmrsService implements IrisMappingService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private IrisMappingDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(IrisMappingDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public IrisMappingDAO getDao() {
	    return dao;
    }

	@Override
	public List<IrisInputMapping> getIrisInputMappings(boolean readonly, int firstResult, int maxResults, String[] mappingsToJoin) {
		return dao.getIrisInputMappings(readonly, firstResult, maxResults, mappingsToJoin);
	}

	@Override
	public List<IrisOutputMapping> getIrisOutputMappings(boolean readonly, int firstResult, int maxResults, String[] mappingsToJoin) {
		return dao.getIrisOutputMappings(readonly, firstResult, maxResults, mappingsToJoin);
	}

	@Override
	public void save(IrisOutputMapping iom) {
		dao.save(iom);
	}
}