/**
 * view-controller for bookedit.html
 *
 * M133: Bookshelf
 *
 * @author  Marcel Suter
 */

/**
 * register listeners and load the book data
 */
$(document).ready(function () {
    loadPublishers();
    loadBook();

    /**
     * listener for submitting the form
     */
    $("#bookeditForm").submit(saveBook);

    /**
     * listener for button [abbrechen], redirects to bookshelf
     */
    $("#cancel").click(function () {
        window.location.href = "./bookshelf.html";
    });

});

/**
 *  loads the data of this book
 *
 */
function loadBook() {
    var bookUUID = $.urlParam('uuid');
    if (bookUUID !== null && bookUUID != -1) {
        $
            .ajax({
                url: "./resource/book/read?uuid=" + bookUUID,
                dataType: "json",
                type: "GET"
            })
            .done(showBook)
            .fail(function (xhr, status, errorThrown) {
                if (xhr.status == 403) {
                    window.location.href = "./login.html";
                } else if (xhr.status == 404) {
                    $("#message").text("Kein Buch gefunden");
                } else {
                    window.location.href = "./bookshelf.html";
                }
            })
    }
}

/**
 * shows the data of this book
 * @param  book  the book data to be shown
 */
function showBook(book) {
    $("#message").empty();
    $("#bookUUID").val(book.bookUUID);
    $("#title").val(book.title);
    $("#author").val(book.author);
    $("#publisher").val(book.publisher.publisherUUID);
    $("#price").val(book.price);
    $("#isbn").val(book.isbn);

    if (Cookies.get("userRole") != "admin") {
        $("#title, #author, #publisher, #price, #isbn").prop("readonly", true);
        $("#save, #reset").prop("disabled", true);
    }
}

/**
 * sends the book data to the webservice
 * @param form the form being submitted
 */
function saveBook(form) {
    form.preventDefault();
    var bookUUID = $("#bookUUID").val();
    var url = "./resource/book/";
    var type = "";
    if (bookUUID) {
        url += "update";
        type = "PUT";
    } else {
        url += "create";
        type = "POST";
    }
    $
        .ajax({
            url: url,
            dataType: "text",
            type: type,
            data: $("#bookeditForm").serialize(),
        })
        .done(function (jsonData) {
            window.location.href = "./bookshelf.html";
        })
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == 404) {
                $("#message").text("Dieses Buch existiert nicht");
            } else {
                $("#message").text("Fehler beim Speichern des Buchs");
            }
        })
}

function loadPublishers() {
    $
        .ajax({
            url: "./resource/publisher/list",
            dataType: "json",
            type: "GET"
        })
        .done(showPublishers)
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == 404) {
                $("#message").text("Kein Buch gefunden");
            } else {
                window.location.href = "./bookshelf.html";
            }
        })
}

function showPublishers(publishers) {

    $.each(publishers, function (uuid, publisher) {
        $('#publisher').append($('<option>', {
            value: publisher.publisherUUID,
            text : publisher.publisher
        }));
    });
}