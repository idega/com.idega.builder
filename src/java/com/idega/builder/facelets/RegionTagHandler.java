package com.idega.builder.facelets;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.idega.presentation.BuilderPageFacetMap;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

/**
 * Tag handler to handle the "region" tag in the Builder.
 * 
 * @author Tryggvi Larusson
 * @version $Id: RegionTagHandler.java,v 1.1 2009/01/14 15:07:18 tryggvil Exp $
 */
public final class RegionTagHandler extends TagHandler {

	public static final String KEY = "facelets.FACET_NAME";

	protected final TagAttribute id;

	public RegionTagHandler(TagConfig config) {
		super(config);
		this.id = this.getRequiredAttribute("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (parent == null) {
			throw new TagException(this.tag, "Parent UIComponent was null");
		}
		String builderRegion = BuilderPageFacetMap.PREFIX+this.id.getValue(ctx);
		parent.getAttributes().put(KEY, builderRegion);
		try {
			this.nextHandler.apply(ctx, parent);
		} finally {
			parent.getAttributes().remove(KEY);
		}
	}
}
