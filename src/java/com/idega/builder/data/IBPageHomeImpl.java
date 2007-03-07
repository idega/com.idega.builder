package com.idega.builder.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;


public class IBPageHomeImpl extends com.idega.data.IDOFactory implements ICPageHome
{
 protected Class getEntityInterfaceClass(){
  return ICPage.class;
 }

 public ICPage create() throws javax.ejb.CreateException{
  return (ICPage) super.idoCreate();
 }

 public ICPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(id);
 }

 public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(pk);
 }

 public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


    public Collection findByTemplate(Integer templateID) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((IBPageBMPBean)entity).ejbFindByTemplate(templateID);
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
    }

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	public ICPage findByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((IBPageBMPBean)entity).ejbFindByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findByUri(java.lang.String, int)
	 */
	public ICPage findExistingByUri(String pageUri, int domainId) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Integer pk  = ((IBPageBMPBean)entity).ejbFindExistingPageByPageUri(pageUri,domainId);
    		this.idoCheckInPooledEntity(entity);
    		return this.findByPrimaryKey(pk);
	}	
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	public Collection findAllPagesWithoutUri() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((IBPageBMPBean)entity).ejbFindAllPagesWithoutUri();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICPageHome#findAllPagesWithoutUri()
	 */
	public Collection findAllSimpleTemplates() throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids  = ((IBPageBMPBean)entity).ejbFindAllSimpleTemplates();
    		this.idoCheckInPooledEntity(entity);
    		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
}