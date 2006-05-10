/*
 * $Id: IBPropertyDescription.java,v 1.3 2006/05/10 08:27:08 laddi Exp $ 
 * Created in 2001 by tryggvil
 * 
 * Copyright (C) 2001-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.builder.business;

/**
 * <p>
 * Class that holds info about each property. Used by IBPropertiesWindowList
 * </p>
 * Last modified: $Date: 2006/05/10 08:27:08 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class IBPropertyDescription{

	private String propertyName;
    //private String methodIdentifier;
    private boolean javaBeansProperty;
    private String displayName;
    private String description;

    public IBPropertyDescription(String methodIdentifier){
      this.setPropertyName(methodIdentifier);
      setJavaBeansProperty(false);
    }
    
    public IBPropertyDescription(String displayName,String propertyName){
        this.setPropertyName(propertyName);
        setDisplayName(displayName);
        setJavaBeansProperty(true);
      }

    public void setDisplayName(String displayName){
      this.displayName=displayName;
    }

    //public void setMethodIdentifier(String methodIdentifier){
    //  this.methodIdentifier=methodIdentifier;
    //}

    public String getDisplayName(){
      return this.displayName;
    }

    public String getMethodIdentifier(){
    	return getPropertyName();
    }

	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}



	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	
	/**
	 * @return the javaBeansProperty
	 */
	public boolean isJavaBeansProperty() {
		return this.javaBeansProperty;
	}



	
	/**
	 * @param javaBeansProperty the javaBeansProperty to set
	 */
	public void setJavaBeansProperty(boolean javaBeansProperty) {
		this.javaBeansProperty = javaBeansProperty;
	}



	
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}



	
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

  }
