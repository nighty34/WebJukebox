/**
 * view-controller for testing
 *
 * M151: Refproject
 *
 * @author  Marcel Suter
 */

/**
 * fixme
 */
var objectCount = 0;
$(document).ready(function () {
    readAddress();
    /**
     * listener for submitting the form
     */
    $("#testcase").submit(send);
});

/**
 * reads the server address for this request
 */
function readAddress() {
    var origin = getAbsolutePath();
    $("#address").html(origin);
}

function send(form) {
    form.preventDefault();
    var serviceURL = "./" + $("#service").val();
    var requestType = $("#type").val();
    var queryParam = $("#queryParam").val();
    var formParam = "";

    if (requestType == "POST" || requestType == "PUT") {
        formParam = $("#formParam").val();
    }

    $
        .ajax({
            url: serviceURL + "?" + queryParam,
            dataType: "json",
            type: requestType,
            data: formParam
        })
        .done(function (jsonData) {
            $("#httpStatus").text("200");
            var data = "";
            if (typeof jsonData === 'object')
                data = show(jsonData);
            else
                data = jsonData;
            $("#outputData").html(data);
        })
        .fail(function (xhr, status, errorThrown) {
            $("#httpStatus").text(xhr.statusCode);
            $("#outputData").html(show(xhr));
        })
}

/**
 * shows the server response
 * @param jsonData
 * @returns {string}
 */
function show(jsonData) {
    var output = "";
    for (var property in jsonData) {
        if (jsonData.hasOwnProperty(property)) {
            if (typeof jsonData[property] == "object") {
                output += "<ul><li>" + property + "<ul>" + show(jsonData[property]) + "</ul></li></ul>";
            } else {
                output += "<li>" + property + ": " + jsonData[property] + "</li>";
            }
        }
    }
    return output;
}

/**
 * gets the request path
 * source: https://stackoverflow.com/a/2864169
 * @returns {string}
 */
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}