$(document)
		.ready(
				function() {
					var table = $('#tabelle')
							.DataTable(
									{
										"dom" : 'T<"clear">lfrtip',
										"dom" : 'Zlfrtip',
										"sScrollY" : ($(window).height() * 0.7),
										"scrollCollapse" : true,
										"bPaginate" : false,
										"bInfo" : false,
										"bAutoWidth" : false,
										"columnDefs" : [ {
											"type" : 'file-size',
											"targets" : 3
										} ],
										"aoColumns" : [ {
											"width" : "12%"
										}, {
											"width" : "57%"
										}, {
											"width" : "11%",
											"sClass" : "right"
										}, {
											"width" : "11%",
											"bSortable" : true,
											"sClass" : "right"
										}, {
											"width" : "8%",
											"bSortable" : false,
											"sClass" : "center"
										}, ],
										"order" : [ [ 2, "desc" ] ],
									});

					$('#tabelle tbody').on('click', 'tr', function() {
						$(this).toggleClass('selected');
					});

					window.init = 0;

				});