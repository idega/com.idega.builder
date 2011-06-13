package com.idega.builder.facelets;

import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagDecorator;

public class BuilderTagDecorator implements TagDecorator {

	public Tag decorate(Tag tag) {
		/*if(tag.getNamespace().equals(BuilderFaceletConverter.sBuilderNamespace)){
			if(tag.getLocalName().equals("module")){
				TagAttributes attributes = tag.getAttributes();
				TagAttribute objectId = attributes.get("id");
				TagAttribute objectClass = attributes.get("class");

				List<TagAttribute> newAttributes = new ArrayList<TagAttribute>();
				TagAttribute componentId = new TagAttribute(objectId.getLocation(),BuilderFaceletConverter.sBuilderNamespace,"componentId","b:componentId",objectId.getValue());
				newAttributes.add(componentId);
				
				TagAttribute componentClass = new TagAttribute(objectId.getLocation(),BuilderFaceletConverter.sBuilderNamespace,"componentClass","b:componentClass",objectClass.getValue());
				newAttributes.add(componentClass);
				
				TagAttributes newTAttributes = new TagAttributes(newAttributes.toArray(new TagAttribute[0]));
				
				
				Tag moduleTag = new Tag(tag.getLocation(), BuilderFaceletConverter.sBuilderNamespace,
                        "objectcontrol", tag.getQName(), newTAttributes);
				return moduleTag;
			}
			//else if(tag.getLocalName().equals("region")){
			//	Tag regionTag = new Tag(tag.getLocation(), BuilderFaceletConverter.sBuilderNamespace,
            //           "regioncontrol", tag.getQName(), tag.getAttributes());
			//	return regionTag;		
			//}
		}*/
		/*else if(tag.getNamespace().equals(BuilderFaceletConverter.sFaceletNamespace)){
			if(tag.getLocalName().equals("define")){
				Tag regionTag = new Tag(tag.getLocation(), BuilderFaceletConverter.sBuilderNamespace,
                        "regioncontrol", tag.getQName(), tag.getAttributes());
				return regionTag;
			}
		}*/	
		return tag;
	}

}
