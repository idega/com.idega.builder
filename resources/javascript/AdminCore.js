jQuery.noConflict();

jQuery(document).ready(function() {
	if (!jQuery('body').hasClass('isContentAdmin')) {
		jQuery('div.content_item_toolbar, div.commentsController').hide();
	}
	jQuery('.applicationPropertyStyleClass').append("<span class=\"icon\"></span>");
	
	jQuery('#adminTopLayer li').click(function() {
		jQuery('#adminTopLayer li.selected').removeClass('selected');
		jQuery(this).addClass('selected');
		jQuery('body').removeClass('isEditAdmin').removeClass('isContentAdmin');
		jQuery('.applicationPropertyStyleClass .icon').hide();
		jQuery('div.content_item_toolbar, div.commentsController').fadeOut('slow');

		if (jQuery(this).hasClass('adminEditMode')) {
			jQuery('body').addClass('isEditAdmin');
			AdminToolbarSession.setMode('isEditAdmin');
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