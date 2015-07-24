$(function(){
    var firstPage = 1;

    $(document)
        .on("click", ".langBtn", function() {
            var language = $(this).attr("href");
            i18n.setLng(language, function() {
                $("[data-i18n]").i18n();
            });
            return false;
        })
        .on("click", ".add", function() {
            history.pushState({"page" : services.addNews.name, "pageNumber" : null, "id" : null}, '', null);
            services.addNews();
            return false;
        })
        .on("click", ".del", function() {
            var id = $(this).attr("href");
            var deleteConfirmMessage = i18n.t("confirm.delete");
            if (confirm(deleteConfirmMessage)) {
                services.deleteNews(id);
            }
            return false;
        })
        .on("click", ".view", function() {
            var id = $(this).attr("href");
            history.pushState({"page" : services.viewNews.name, "pageNumber" : firstPage, "id" : id}, '', null);
            services.viewNews(1, id);
            return false;
        })
        .on("click", ".edit", function() {
            var id = $(this).attr("href");
            history.pushState({"page" : services.editNews.name, "pageNumber" : null, "id" : id}, '', null);
            services.editNews(id);
            return false;
        })
        .on("click", "#cancel, .all", function() {
            if ($("#title").val() != "" || $("#shortText").val() != "" || $("#fullText").val() != "") {
                var noSaveConfirmMessage = i18n.t("confirm.noSave");
                if (confirm(noSaveConfirmMessage)) {
                    history.pushState({"page" : services.viewAllNews.name, "pageNumber" : firstPage, "id": null}, '', null);
                    services.viewAllNews(firstPage);
                    }
                } else {
                    history.pushState({"page" : services.viewAllNews.name, "pageNumber" : firstPage, "id": null}, '', null);
                    services.viewAllNews(firstPage);
                    }
            return false;
            })
        .on("click", "#back", function() {
            window.history.back();
            $("[data-i18n]").i18n();
            return false;
        })
        .on("click", "#commentsBtn", function() {
            document.getElementById("commentList").style.display = "block";
            return false;
        })
        .on("click", "#tagsBtn", function() {
            services.getTagList();
            document.getElementById("tagList").style.display = "block";
            return false;
        })
        .on("click", "#authorsBtn", function() {
            services.getAuthorList();
            document.getElementById("authorList").style.display = "block";
            return false;
        })
        .on("click", ".top", function() {
            var count = $(this).attr("href");
            history.pushState({"page" : services.viewTopCommentedNews.name, 
                               "pageNumber": firstPage,
                               "id" : count}, '', null);
            services.viewTopCommentedNews(firstPage, count);
            return false;
        })
        .on("click", ".tag", function() {
            var tag = $(this).attr("href");
            history.pushState({"page" : services.viewNewsByTag.name,
                               "pageNumber": firstPage,
                               "id" : tag}, '', null);
            services.viewNewsByTag(firstPage, tag);
            return false;
        })
        .on("click", ".author", function() {
            var authorId = $(this).attr("href");
            history.pushState({"page" : services.viewNewsByAuthor.name,
                               "pageNumber": firstPage,
                               "id" : authorId}, '', null);
            services.viewNewsByAuthor(firstPage, authorId);
            return false;
        })
        .on("click", ".pageNumber", function() {
            var pageNumber = $(this).attr("href");
            history.pushState({"page" : history.state.page,
                               "pageNumber" : pageNumber,
                               "id": history.state.id}, '', null);
            services[history.state.page](pageNumber, history.state.id);
            return false;
        })
        .on("click", ".rightArrow", function() {
            var pageNumber = $(this).attr("href");
            history.pushState({"page" : history.state.page,
                               "pageNumber" : (+pageNumber + 1),
                               "id": history.state.id}, '', null);
            services[history.state.page]((+pageNumber + 1), history.state.id);
            return false;
        })
        .on("click", ".leftArrow", function() {
            var pageNumber = $(this).attr("href");
            history.pushState({"page" : history.state.page,
                               "pageNumber" : (+pageNumber - 1),
                               "id": history.state.id}, '', null);
            services[history.state.page]((+pageNumber - 1), history.state.id);
            return false;
        })
        .on("click", ".delCmnt", function() {
            var commentId = $(this).attr("href");
            var deleteConfirmMessage = i18n.t("comment.confirmDelete");
            if (confirm(deleteConfirmMessage)) {
                services.deleteComment(commentId);
            }
            return false;
        })
        .on("click", ".viewComments", function() {
            var newsId = $(this).attr("href");
            history.pushState({"page" : services.viewComments.name,
                               "pageNumber": firstPage,
                               "id" : newsId}, '', null);
            services.viewComments(firstPage, newsId);
            return false;
        });
});