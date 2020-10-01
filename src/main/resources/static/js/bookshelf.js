/**
 * view-controller for bookshelf.html
 *
 * M133: Bookshelf
 *
 * @author  Marcel Suter
 */

/**
 * register listeners and load all books
 */
$(document).ready(function () {
    loadBooks();

    /**
     * listener for buttons within shelfForm
     */
    $("#shelfForm").on("click", "button", function () {
        if (confirm("Wollen Sie dieses Buch wirklich löschen?")) {
            deleteBook(this.value);
        }
    });

});

/**
 * loads the books from the webservice
 *
 */
function loadBooks() {
    $
        .ajax({
            url: "./resource/book/list",
            dataType: "json",
            type: "GET"
        })
        .done(showBooks)
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == 403) {
                window.location.href("./login.html");
            } else if (xhr.status == 404) {
                $("#message").text("keine Bücher vorhanden");
            }else {
                $("#message").text("Fehler beim Lesen der Bücher");
            }
        })
}

/**
 * shows all books as a table
 *
 * @param bookData all books as an array
 */
function showBooks(bookData) {
    $("#message").val("");
    $("#bookshelf > tbody").html("");
    var tableData = "";
    $.each(bookData, function (uuid, book) {
        tableData += "<tr>";
        tableData += "<td>" + book.title + "</td>";
        tableData += "<td>" + book.author + "</td>";
        tableData += "<td>" + book.publisher.publisher + "</td>";
        tableData += "<td>" + book.price + "</td>";
        tableData += "<td>" + book.isbn + "</td>";
        if (Cookies.get("userRole") == "admin") {
            tableData += "<td><a href='./bookedit.html?uuid=" + uuid + "'>Bearbeiten</a></td>";
            tableData += "<td><button type='button' id='delete_" + uuid + "' value='" + uuid + "'>Löschen</button></td>";
        } else {
            tableData += "<td><a href='./bookedit.html?uuid=" + uuid + "'>Ansehen</a></td>";
        }
        tableData += "</tr>";
    });
    $("#bookshelf > tbody").html(tableData);
}

/**
 * send delete request for a book
 * @param bookUUID
 */
function deleteBook(bookUUID) {
    $
        .ajax({
            url: "./resource/book/delete?uuid=" + bookUUID,
            dataType: "text",
            type: "DELETE",
        })
        .done(function (data) {
            loadBooks();
            $("#message").text("Buch gelöscht");

        })
        .fail(function (xhr, status, errorThrown) {
            $("#message").text("Fehler beim Löschen des Buchs");
        })
}