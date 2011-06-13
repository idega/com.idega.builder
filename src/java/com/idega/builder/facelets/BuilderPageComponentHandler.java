package com.idega.builder.facelets;

import javax.faces.component.UIComponent;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;

public class BuilderPageComponentHandler extends ComponentHandler {

	public BuilderPageComponentHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	public void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		// TODO Auto-generated method stub
		super.onComponentCreated(ctx, c, parent);
	}

	@Override
	public void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		// TODO Auto-generated method stub
		super.onComponentPopulated(ctx, c, parent);
	}

}
