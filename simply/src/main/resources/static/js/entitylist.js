function collectFilters() {
	var entityListForm = document.getElementById("entitylist");
	var inputs = entityListForm.getElementsByTagName("input");
	for (var i = 0; i < inputs.length; i++) {
		if (inputs[i].type == "text" && inputs[i].name.endsWith('Filter')) {
			document.getElementById(inputs[i].name).value = inputs[i].value;
		}
	}
}

function filter() {
	collectFilters();
	var form = document.getElementById("listhidden");
	form.submit();
	return false;
}

function previousPage() {
	collectFilters();
	var form = document.getElementById("listhidden");
	var page = document.getElementById("page");
	page.value = parseInt(page.value) - 1;
	form.submit();
	return false;
}

function nextPage() {
	collectFilters();
	var form = document.getElementById("listhidden");
	var page = document.getElementById("page");
	page.value = "" + (parseInt(page.value) + 1);
	form.submit();
	return false;
}
