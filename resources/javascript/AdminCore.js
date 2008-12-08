jQuery.noConflict();

jQuery(document).ready(function() {
	if (!jQuery('body').hasClass('isContentAdmin')) {
		jQuery('div.content_item_toolbar, div.commentsController').hide();
	}
	if (jQuery('body').hasClass('isEditAdmin')) {
		jQuery('.moduleName').dropShadow({left: 0, top: 2, opacity: 0.5, blur: 2});
	}
	if (jQuery('body').hasClass('isThemesAdmin')) {
		jQuery('body').append('<div id="themeSlider"></div>');
	}
	jQuery('.applicationPropertyStyleClass').append("<span class=\"icon\"></span>");

	jQuery('#adminTopLayer li').hover(
			function() {
				jQuery(this).children('.modeHelper').fadeIn('fast');
			},
			function() {
				jQuery(this).children('.modeHelper').fadeOut('fast');
			}
	);
	
	jQuery('.moduleContainer').hover(
			function() {
				jQuery(this).children('.regionInfoImageContainer').dropShadow({left: 0, top: 2, opacity: 0.5, blur: 2});
			},
			function() {
				jQuery(this).children('.regionInfoImageContainer').removeShadow();
			}
	);
	
	jQuery('#adminTopLayer li').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').removeClass('isThemesAdmin').removeClass('isEditAdmin').removeClass('isContentAdmin');
		jQuery('.applicationPropertyStyleClass .icon').hide();
		jQuery('div.content_item_toolbar, div.commentsController').hide();
		jQuery('body div#themeSlider').remove();

		if (jQuery(this).hasClass('adminThemesMode')) {
			jQuery('body').addClass('isThemesAdmin');
			AdminToolbarSession.setMode('isThemesAdmin');
			jQuery('body').append('<div id="themeSlider"></div>');
		}

		if (jQuery(this).hasClass('adminEditMode')) {
			jQuery('body').addClass('isEditAdmin');
			AdminToolbarSession.setMode('isEditAdmin');
			jQuery('.moduleName').dropShadow({left: 0, top: 1, opacity: 0.5, blur: 2});
		}
		else {
			jQuery('.moduleName').removeShadow();
		}
		
		if (jQuery(this).hasClass('adminContentMode')) {
			jQuery('body').addClass('isContentAdmin');
			jQuery('div.content_item_toolbar, div.commentsController').fadeIn('slow');
			jQuery('.applicationPropertyStyleClass .icon').fadeIn('slow');
			AdminToolbarSession.setMode('isContentAdmin');
		}
		else {
		}
		
		if (jQuery(this).hasClass('adminPreviewMode')) {
			AdminToolbarSession.setMode('preview');
		}
	})
});