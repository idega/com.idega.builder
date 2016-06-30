package com.idega.builder.data.dao.impl;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.builder.data.IBPageName;
import com.idega.builder.data.IBPageNameHome;
import com.idega.core.builder.dao.IBPageNameDAO;
import com.idega.core.persistence.impl.GenericDaoImpl;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(IBPageNameDAOImpl.BEAN_NAME)
@Transactional(readOnly = true)
public class IBPageNameDAOImpl extends GenericDaoImpl implements IBPageNameDAO {

	public static final String BEAN_NAME = "ibPageNameDAO";
	
	@Override
	public String getNameByPageAndLocale(int pageId, int localeId) {
		try {
			IBPageNameHome home = (IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class);
			IBPageName name = home.findByPageIdAndLocaleId(pageId, localeId);
			return name.getPageName();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
