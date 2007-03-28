var activeAddComponentElement = null;

var REGION_COMPONENT_AND_INFO_CONTAINER = "builderRegionComponentAndInfoImagesContainer";
var ACTIVE_ADD_COMPONENT_ELEMENT_ID = "addComponentToActiveRegionId";
var ACTIVE_ADD_INFO_ELEMENT_ID = "addInfoPanelToActiveRegionId";

//window.onload = registerBuilderActions;

function registerBuilderActions() {
	var builderRules = {
		'div.moduleContainer' : function(element) {
			element.onmouseover = function() {
				showAllComponentsLabels(element);
				showComponentAndInfoImages(element);
				//showAddComponentImage(element);
				//showSetComponentmage(element);
			}
			element.onmouseout = function() {
				//hideComponentAndInfoImages(element);
			}
		}
	};
	Behaviour.register(builderRules);
	Behaviour.apply();
}

function showAllComponentsLabels(element) {
	if (element == null) {
		return;
	}
	var children = getNeededBuilderElements(element, "DnDAreaTable");
	if (children == null) {
		return;
	}
	var child = null;
	var elementsToHighlight = null;
	for (var i = 0; i < children.length; i++) {
		child = children[i];
		child.style.visibility = "visible";
		elementsToHighlight = getNeededBuilderElementsFromList(child.getElementsByTagName("div"), "moduleName");
		for (var j = 0; j < elementsToHighlight.length; j++) {
			new Effect.Highlight(elementsToHighlight[j]);
		}
	}
}

function showComponentAndInfoImages(element) {
	if (element == null) {
		return;
	}
	var children = getNeededBuilderElementsFromListById(element.childNodes, REGION_COMPONENT_AND_INFO_CONTAINER);
	if (children.length > 0) {
		return;
	}
	
	var container = document.createElement("div");
	container.setAttribute("id", REGION_COMPONENT_AND_INFO_CONTAINER);
	container.style.display = "inline";
	
	addComponent = document.createElement("div");
	addComponent.setAttribute("id", ACTIVE_ADD_COMPONENT_ELEMENT_ID);
	addComponent.className = "regionComponentImageContainer";
	
	var linkToComponents = document.createElement("a");

	var windowLink = "/idegaweb/bundles/com.idega.block.web2.0.bundle/resources/javascript/lightbox/particletree/text.html";
	linkToComponents.setAttribute("href", windowLink);
	linkToComponents.setAttribute("title", "Add new component");
	linkToComponents.className="lbOn";
	
	var image = document.createElement("img");
	image.setAttribute("src", "/idegaweb/bundles/com.idega.builder.bundle/resources/add.png");
	image.setAttribute("title", "Add new component");
	/*if (typeof addComponent.attachEvent == "undefined") {
		image.addEventListener("click", function(e){openAddComponentLayer(addComponent);}, false);
	} else {
	   	image.attachEvent("onclick", function(e){openAddComponentLayer(addComponent);});
	}*/
	linkToComponents.appendChild(image);
	addComponent.appendChild(linkToComponents);
	container.appendChild(addComponent);
	
	var setInfo = document.createElement("div");
	setInfo.setAttribute("id", ACTIVE_ADD_INFO_ELEMENT_ID);
	setInfo.className = "regionInfoImageContainer";
	var infoImage = document.createElement("img");
	infoImage.setAttribute("src", "/idegaweb/bundles/com.idega.builder.bundle/resources/information.png");
	infoImage.setAttribute("title", "Set component properties");
	addEvent(infoImage, 'click', turnComponentAround);
	setInfo.appendChild(infoImage);
	container.appendChild(setInfo);

	element.appendChild(container);
	
	initialize();
	
	new Effect.Pulsate(setInfo);
}

function hideComponentAndInfoImages(element) {
	if (element == null) {
		return;
	}
	var elementsToRemove = getNeededBuilderElementsFromListById(element.childNodes, REGION_COMPONENT_AND_INFO_CONTAINER);
	for (var i = 0; i < elementsToRemove.length; i++) {
		element.removeChild(elementsToRemove[i]);
	}
}

function hideRegionImage(element) {
	if (element == null) {
		return;
	}
	var elemetToHide = getNeededBuilderElement(element.parentNode, "_regionLabel");
	//alert(elemetToHide);
	if (elemetToHide == null) {
		return;
	}
	new Effect.Fade(elemetToHide);
}

function getNeededBuilderElements(element, className) {
	if (element == null) {
		return null;
	}
	return getNeededBuilderElementsFromList(element.childNodes, className);
}

function getNeededBuilderElementsFromList(list, className) {
	if (list == null || className == null) {
		return new Array();;
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.className != null) {
				if (childElement.className == className) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function getNeededBuilderElementsFromListById(list, id) {
	if (list == null || id == null) {
		return new Array();;
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.id != null) {
				if (childElement.id == id) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function openAddComponentLayer(parentElement) {
	if (parentElement == null) {
		return;
	}
	
}

function turnComponentAround() {
	alert("turn me around!");
}