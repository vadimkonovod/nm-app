$(document)
    .ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
        var data = JSON.parse(jqXHR.responseText);
        if (data.validationErrors != null) {
            alert("Error! \n" + "Message: " + data.message + "\nValidation Errors: " + data.validationErrors);
        } else {
            alert("Error! \n" + "Message: " + data.message);
        }
    })
    .ready(function() {
        var firstPage = 1;

        i18n.init({
            lng : 'en',
            ns : 'translation', 
            debug : true,
            resGetPath : "resources/locales/__lng__/__ns__.json"
        }, function() {    
            $("[data-i18n]").i18n();
        });

        $(window).on("popstate", function(event) {
            if (window.history.state != null && window.history.state.page != null
                    && window.history.state.pageNumber != null) {
                window[services[window.history.state.page](window.history.state.pageNumber, window.history.state.id)];
            } else {
                window[services[window.history.state.page](window.history.state.id)];
            }
        });

        services.viewAllNews(firstPage);
});