function showBootbox(title, errorMsg){

	if(title == undefined){
		title = "Please provide";
	}

	bootbox.alert({
		title: title,
		message : errorMsg,
		closeButton: false,
		buttons : {
			ok : {
				label : "OK",
				className : "btn-primary",
			}
		}
	});
}

function bootboxConfirmation(title, message, page) {
    bootbox.confirm({
        title: title,
        message: message,
        buttons: {
            confirm: {
                label: 'OK',
                className: 'btn-primary'
            }
        },
        callback: function (result) {
            if(page){
                window.location.href = page
            }
        }
    });
}