package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

  public class IBPropertyDescription{

    private String _methodIdentifier;
    private String _description;

    public IBPropertyDescription(String methodIdentifier){
      this.setMethodIdentifier(methodIdentifier);
    }

    public void setMethodDescription(String description){
      this._description=description;
    }

    public void setMethodIdentifier(String methodIdentifier){
      this._methodIdentifier=methodIdentifier;
    }

    public String getMethodDescription(){
      return this._description;
    }

    public String getMethodIdentifier(){
      return this._methodIdentifier;
    }

  }
