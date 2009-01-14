package com.idega.builder.facelets;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class BuilderPageComponentHandler extends ComponentHandler {

	public BuilderPageComponentHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		// TODO Auto-generated method stub
		super.onComponentCreated(ctx, c, parent);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		// TODO Auto-generated method stub
		super.onComponentPopulated(ctx, c, parent);
	}

}
