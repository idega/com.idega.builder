package com.idega.builder.facelets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.view.facelets.CompositeFaceletHandler;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public abstract class LegacyTagHandler extends TagHandler {

	
	public LegacyTagHandler(TagConfig config) {
		super(config);
	}

	/**
     * Searches child handlers, starting at the 'nextHandler' for all
     * instances of the passed type.  This process will stop searching
     * a branch if an instance is found.
     * 
     * @param type Class type to search for
     * @return iterator over instances of FaceletHandlers of the matching type
     * @see com.sun.facelets.tag.TagHandler
     */
    protected final Iterator findNextByType(Class type) {
        List found = new ArrayList();
        if (type.isAssignableFrom(this.nextHandler.getClass())) {
            found.add(this.nextHandler);
        } else if (this.nextHandler instanceof CompositeFaceletHandler) {
            FaceletHandler[] h = ((CompositeFaceletHandler) this.nextHandler).getHandlers();
            for (int i = 0; i < h.length; i++) {
                if (type.isAssignableFrom(h[i].getClass())) {
                    found.add(h[i]);
                }
            }
        }
        return found.iterator();
    }

}
