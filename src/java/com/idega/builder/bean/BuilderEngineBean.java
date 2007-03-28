package com.idega.builder.bean;

import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOServiceBean;
import com.idega.presentation.IWContext;

public class BuilderEngineBean extends IBOServiceBean implements BuilderEngine {
	
	private static final long serialVersionUID = -4806588458269035118L;
	private static final Logger log = Logger.getLogger(BuilderEngineBean.class.getName());
	
	private BuilderLogic builder = BuilderLogic.getInstance();

	public String getComponentsPanel() {
		log.info("IWContext: " + getIWContext());
		String panel = "new panel";
		return panel;
	}
	
	private IWContext getIWContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null) {
			return IWContext.getInstance();
		}
		else {
			return IWContext.getIWContext(fc);
		}
	}

}
