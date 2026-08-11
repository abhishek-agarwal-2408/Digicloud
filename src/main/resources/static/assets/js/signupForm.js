$(document).ready(function(){
    $("#generateOtpButton").on("click", function(){
        createUser();
    })
    $("#verifyOtpButton").on("click", function(){
        verifyUser();
    })
})

function createUser(){
    // Get form data
    var formData = {
        firstName: $('#fName').val(),
        lastName: $('#lName').val(),
        phoneNumber: $('#phoneNumber').val(),
        email: $('#email').val(),
        password: $('#password').val(),
        dob: $('#dob').val(),
    };

    // Send the AJAX request
    $.ajax({
        type: 'POST',
        url: 'signUp', // Replace with your server endpoint URL
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (response) {
            $(".generate-otp-div").hide()
            $(".give-otp-div").show()
        },
        error: function (errorThrown) {
            // Handle errors, if any
            showBootbox("Error", errorThrown.responseText)
            console.log('Error:', errorThrown.responseText);
        },
    });
}
function verifyUser(){
    // Get form data
    var formData = {
        email: $('#email').val(),
        otp: $('#otp').val()
    };

    // Send the AJAX request
    $.ajax({
        type: 'POST',
        url: 'verify', // Replace with your server endpoint URL
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (response) {
            bootboxConfirmation("Success", response, "login")
        },
        error: function (errorThrown) {
            // Handle errors, if any
            showBootbox("Error", errorThrown.responseText)
            console.log('Error:', errorThrown.responseText);
        },
    });
}