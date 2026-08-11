$(document).ready(function () {
    $("#login-form-btn").on("click", function () {
        loginUser();
    });
});

function loginUser() {
    // Get form data
    var formData = {
        email: $("#email").val(),
        password: $("#password").val(),
    };

    // Send the AJAX request
    $.ajax({
        type: "POST",
        url: "signIn", // Replace with your server endpoint URL
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: function (response) {
            response = JSON.parse(response);
            var responseText = "Log in successful.";
            if (response.hasOwnProperty("sessionId")) {
                window.location.href = "dashboard";
            } else {
                responseText = response.message;
                bootboxConfirmation("Failed", responseText, "login");
            }
        },
        error: function (errorThrown) {
            // Handle errors, if any
            showBootbox("Error", errorThrown.responseText);
            console.log("Error:", errorThrown.responseText);
        },
    });
}
