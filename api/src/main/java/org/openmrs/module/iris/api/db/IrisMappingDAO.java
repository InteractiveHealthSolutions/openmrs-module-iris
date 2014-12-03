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
package org.openmrs.module.iris.api.db;

import java.util.List;

import org.openmrs.module.iris.IrisInputMapping;
import org.openmrs.module.iris.IrisOutputMapping;

/**
 *  Database methods for {@link IrisMappingService}.
 */
public interface IrisMappingDAO {
	
	List<IrisInputMapping> getIrisInputMappings(boolean readonly, int firstResult, int maxResults, String[] mappingsToJoin);

	List<IrisOutputMapping> getIrisOutputMappings(boolean readonly, int firstResult, int maxResults, String[] mappingsToJoin);

	void save(IrisOutputMapping iom);
}