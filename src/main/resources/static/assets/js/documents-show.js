$(document).ready(function () {
    showAllDocuments();
    $(".image-item").click(function () {
        var imageData = $(this).attr("data");
        var imageMimeType = $(this).attr("mime-type");
        var modal = $("#showRecentDocumentModal");
        let src = "data:" + imageMimeType + ";base64," + btoa(atob(imageData));
        if (imageMimeType.includes("application")) {
            modal.find("#documentObject").attr("data", src);
            modal.find("#documentImage").removeAttr("src");
        } else {
            modal.find("#documentImage").attr("src", src);
            modal.find("#documentObject").removeAttr("data");
        }
    });

    $("body").on("click", ".doc-item", function () {
        var storageId = $(this).attr("storageid");
        $.ajax({
            url: "getDoc",
            type: "POST",
            data: { storageId: storageId },
            dataType: "json",
            success: function (response) {
                var imageMimeType = response.mimeType;
                let base64 = "data:" + imageMimeType + ";base64," + response.base64Data;
                console.log(base64);
                var modal = $("#showRecentDocumentModal");
                let src = base64;
                if (imageMimeType.includes("application")) {
                    modal.find("#documentObject").attr("data", src);
                    modal.find("#documentObject").attr("type", imageMimeType);
                    modal.find("#documentImage").removeAttr("src");
                } else {
                    modal.find("#documentImage").attr("src", src);
                    modal.find("#documentObject").removeAttr("data");
                }
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error:", error);
            },
        });
    });

    $("#documentTypeModal").on("show.bs.modal", function (event) {
        var target = $(event.relatedTarget); // Button that triggered the modal
        var fileCategory = target.attr("docType"); // Assuming you have a data attribute for file category
        showDocumentFromCategory(fileCategory);
    });

    $("body").on("click", ".download-doc", function (event) {
        const storageId = $(this).attr("storageid");
        $.ajax({
            type: "POST",
            url: "downloadDoc",
            data: { storageId: storageId },
            dataType: "json",
            success: function (response) {
                downloadBase64Image(response.base64Data, response.fileName);
            },
            error: function (error) {
                console.log("Error fetching documents: " + error);
            },
        });
    });

    $("body").on("click", ".delete-doc", function (event) {
        const storageId = $(this).attr("storageid");
        $.ajax({
            type: "POST",
            url: "deleteDoc",
            data: { storageId: storageId },
            dataType: "json",
            success: function (response) {
                bootboxConfirmation("Success", response.message, "dashboard");
            },
            error: function (error) {
                console.log("Error fetching documents: " + error);
            },
        });
    });
});

function showDocumentFromCategory(fileCategory) {
    var columns = [
        { title: "Id", data: "storageId" },
        { className: "overflow-hidden", title: "File name", data: "fileName" },
        { title: "File type", data: "fileCategory" },
        { title: "Size", data: "size" },
        { title: "Date and time", data: "dateTime" },
        {
            title: "Download",
            data: "storageId",
            render: function (data, type) {
                return "<div class='btn btn-primary btn-sm download-doc' storageId=" + data + ">Download</div>";
            },
        },
        {
            title: "Delete",
            data: "storageId",
            render: function (data, type) {
                return "<div class='btn btn-danger btn-sm delete-doc' storageId=" + data + ">Delete</div>";
            },
        },
        // Add more data as needed
    ];

    new DataTable("#documentTypeTable", {
        bDestroy: true,
        ajax: {
            type: "POST",
            url: "getDocuments",
            data: { fileCategory: fileCategory },
            dataType: "json",
        },
        ordering: false,
        searching: false,
        columns: columns,
    });
}

function showAllDocuments() {
    var columns = [
        {
            className: "overflow-hidden",
            title: "File name",
            data: "fileName",
            render: function (data, type, row) {
                return '<div data-bs-toggle="modal" data-bs-target="#showRecentDocumentModal" class="doc-item" storageid="' + row.storageId + '">' + data + "</div>";
            },
        },
        { title: "File type", data: "fileCategory" },
        { title: "Size", data: "size" },
        { title: "Date and time", data: "dateTime" },
        {
            title: "Download",
            data: "storageId",
            render: function (data, type) {
                return "<div class='btn btn-primary btn-sm download-doc' storageId=" + data + ">Download</div>";
            },
        },
        {
            title: "Delete",
            data: "storageId",
            render: function (data, type) {
                return "<div class='btn btn-danger btn-sm delete-doc' storageId=" + data + ">Delete</div>";
            },
        },
        // Add more data as needed
    ];

    new DataTable("#allDocsTable", {
        bDestroy: true,
        ajax: {
            type: "POST",
            url: "getDocuments",
            data: { fileCategory: "all" },
            dataType: "json",
        },
        ordering: false,
        searching: false,
        columns: columns,
    });
}

function downloadBase64Image(encodedData, filename) {
    var blob = base64ToBlob(encodedData);
    var blobUrl = URL.createObjectURL(blob);

    var a = document.createElement("a");
    a.href = blobUrl;
    a.download = filename;

    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    // Clean up the URL object to release resources
    URL.revokeObjectURL(blobUrl);
}

// Function to convert Base64 to Blob
function base64ToBlob(base64Data) {
    var contentType = base64Data.split(";")[0].split(":")[1];
    var byteCharacters = atob(base64Data);
    var byteNumbers = new Array(byteCharacters.length);

    for (var i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    var byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
}
