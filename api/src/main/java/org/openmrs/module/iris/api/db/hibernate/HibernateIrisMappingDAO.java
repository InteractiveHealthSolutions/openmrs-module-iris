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
package org.openmrs.module.iris.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.openmrs.module.iris.IrisInputMapping;
import org.openmrs.module.iris.IrisOutputMapping;
import org.openmrs.module.iris.api.db.IrisMappingDAO;

/**
 * It is a default implementation of  {@link IrisMappingDAO}.
 */
public class HibernateIrisMappingDAO implements IrisMappingDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<IrisInputMapping> getIrisInputMappings(boolean readonly,int firstResult, int maxResults, String[] mappingsToJoin) {
		Criteria cri = sessionFactory.getCurrentSession().createCriteria(IrisInputMapping.class)
				.setReadOnly(readonly)
				.addOrder(Order.asc("irisIndex"));

		if (mappingsToJoin != null)
			for (String mapping : mappingsToJoin) {
				cri.setFetchMode(mapping, FetchMode.JOIN);
			}

		List<IrisInputMapping> list = cri.setFirstResult(firstResult).setMaxResults(maxResults).list();
		return list;
	}

	@Override
	public List<IrisOutputMapping> getIrisOutputMappings(boolean readonly, int firstResult, int maxResults, String[] mappingsToJoin) {
		Criteria cri = sessionFactory.getCurrentSession().createCriteria(IrisOutputMapping.class)
				.setReadOnly(readonly)
				.addOrder(Order.asc("irisOutputIndex"));

		if (mappingsToJoin != null)
			for (String mapping : mappingsToJoin) {
				cri.setFetchMode(mapping, FetchMode.JOIN);
			}

		List<IrisOutputMapping> list = cri.setFirstResult(firstResult).setMaxResults(maxResults).list();
		return list;
	}

	@Override
	public void save(IrisOutputMapping iom) {
		sessionFactory.getCurrentSession().saveOrUpdate(iom);
	}
}