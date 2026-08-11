$(document).ready(function () {
    $("#user-profile").on("change", function () {
        console.log("Profile image selected...");
        var selectedImage = this.files[0];
        if (selectedImage) {
            var fileName = selectedImage.name;
            var fileSize = selectedImage.size;
            var fileSizeFormatted = formatBytes(fileSize);

            $(".profileImageDetailsDiv")
                .html(fileName + " Size: " + fileSizeFormatted)
                .after('<button class="btn btn-primary btn-sm my-2 update-profile-btn" type="button">Update Details</button>');
        }
    });

    $("body").on("click", ".update-profile-btn", function (event) {
        const formData = new FormData($("#user-profile-image-form")[0]);
        $.ajax({
            type: "POST",
            url: "update-profile-image",
            contentType: "application/json",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                bootboxConfirmation("Success", response, "dashboard");
            },
            error: function (error) {
                console.log("Error saving details: " + error);
            },
        });
    });

    $("body").on("click", "#updateCustomerBtn", function (event) {
        const formData = new FormData($("#updateCustomerForm")[0]);
        const jsonObject = {};
        formData.forEach((value, key) => {
            jsonObject[key] = value;
        });
        console.log(jsonObject);
        $.ajax({
            type: "POST",
            url: "updateCustomer",
            contentType: "application/json",
            data: JSON.stringify(jsonObject),
            success: function (response) {
                bootboxConfirmation("Success", response, "dashboard");
            },
            error: function (error) {
                console.log("Error saving details: " + error);
            },
        });
    });

    $("body").on("click", "#updateCustomerContactBtn", function (event) {
        const formData = new FormData($("#updateCustomerContactForm")[0]);
        const jsonObject = {};
        formData.forEach((value, key) => {
            jsonObject[key] = value;
        });
        console.log(jsonObject);
        $.ajax({
            type: "POST",
            url: "updateCustomerContact",
            contentType: "application/json",
            data: JSON.stringify(jsonObject),
            success: function (response) {
                bootboxConfirmation("Success", response, "dashboard");
            },
            error: function (error) {
                console.log("Error saving details: " + error);
            },
        });
    });
});
function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ["Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i];
}
