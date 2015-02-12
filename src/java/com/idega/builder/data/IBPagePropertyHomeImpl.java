package com.idega.builder.data;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class IBPagePropertyHomeImpl  extends IDOFactory implements IBPagePropertyHome {

	@Override
	public Class getEntityInterfaceClass() {
		return IBPageProperty.class;
	}

	@Override
	public IBPageProperty create() throws CreateException {
		return (IBPageProperty) super.createIDO();
	}



	private Logger getLogger(){
		return Logger.getLogger(IBPagePropertyHomeImpl.class.getName());
	}
	@Override
	public IBPageProperty getProperty(int pageId, int localeId,String propertyKey){
		try{
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Object pk = ((IBPagePropertyBMPBean) entity).ejbFindProperty(pageId, localeId,propertyKey);
			this.idoCheckInPooledEntity(entity);
			return findByPrimaryKeyIDO(pk);
		}catch (FinderException e) {
		}catch (Exception e) {
			getLogger().log(Level.WARNING, 
					"Failed getting property by pageid: " + pageId + ", localeId: " +localeId + ", propertyKey: " + propertyKey, 
					e);
		}
		return null;
	}
	@Override
	public Collection<IBPageProperty> getPropertiesForAllLocales(int pageId,String propertyKey){
		try{
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Collection<Object> pks = ((IBPagePropertyBMPBean) entity).ejbFindPropertiesForAllLocales(pageId,propertyKey);
			this.idoCheckInPooledEntity(entity);
			return this.getEntityCollectionForPrimaryKeys(pks);
		}catch (FinderException e) {
		}catch (Exception e) {
			getLogger().log(Level.WARNING, 
					"Failed getting properties by pageid: " + pageId + ", propertyKey: " + propertyKey, 
					e);
		}
		return Collections.emptyList();
	}

}