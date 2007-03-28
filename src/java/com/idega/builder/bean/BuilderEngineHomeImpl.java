package com.idega.builder.bean;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class BuilderEngineHomeImpl extends IBOHomeImpl implements BuilderEngineHome {

	private static final long serialVersionUID = -4101446217150638727L;

	public Class getBeanInterfaceClass() {
		return BuilderEngine.class;
	}

	public BuilderEngine create() throws CreateException {
		return (BuilderEngine) super.createIBO();
	}
}