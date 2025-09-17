	function initSearchableSelects() {
		$('.searchable-select').each(function() {
			if (!$(this).hasClass('select2-hidden-accessible')) { // prevent double init
				$(this).select2({
					width: '100%',
					placeholder: "--Select--",
					allowClear: true
				});
			}
		});

		$('.searchable-select').on('select2:open', function () {
			$('.select2-container--open .select2-search__field').focus();
		});
	}

	// Run on page load
	$(document).ready(function () {
		initSearchableSelects();
	});