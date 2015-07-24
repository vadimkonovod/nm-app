services = (function() {
    var NEWS_PER_PAGE = 6;
    var firstPage = 1;

    function request(method, url, data, successHandler) {
        return $.ajax({
            method : method,
            url : url,
            data : JSON.stringify(data),
            contentType : "application/json",
            success : successHandler
        });
    }

    function editSuccess(pageNumber, newsId) {
        history.pushState({"page" : viewNews.name, "pageNumber" : firstPage, "id" : newsId}, '', null);
        services.viewNews(firstPage, newsId);
    }

    function viewAllNews(pageNumber) {

        function renderNewsList(data, template, pagination, newsCount) {
            var $content = $(".content");
            $content.empty();

            for (i = 0; i < data.length; i++) {
                var newsItem = data[i];
                var newsElement = $(template);

                $(".title", newsElement).text(newsItem.title);
                $(".date", newsElement).text(newsItem.modificationDate);
                $(".shortText", newsElement).text(newsItem.shortText);
                $(".view", newsElement).attr("href", newsItem.newsId);
                $(".edit", newsElement).attr("href", newsItem.newsId);
                $(".del", newsElement).attr("href", newsItem.newsId);
                $content.append(newsElement);
            }

            $content.append(pagination);

            var lastPage = Math.ceil(newsCount / NEWS_PER_PAGE);
            var of = i18n.t("message.of");
            var and = i18n.t("message.and");
            var invalidPageMessage = i18n.t("error.invalidPageNumber");
            
            $(".leftArrow").attr("href", pageNumber);
            $(".rightArrow").attr("href", pageNumber);
            $("#myInp").attr("placeholder", pageNumber + ' ' + of + ' ' + lastPage);

            if (pageNumber == firstPage) {
                document.getElementById("left").style.visibility = "hidden";
            }
            if (pageNumber >= (newsCount / NEWS_PER_PAGE)) {
                document.getElementById("right").style.visibility = "hidden";
            }
            
            $('#myInputForm').submit(function() {
                var pageInput = $("#myInp");
                var pageInputValue = pageInput.val();

                if (pageInputValue < firstPage || pageInputValue > lastPage || !pageInputValue.match(/\d+/)) {
                    alert(invalidPageMessage + firstPage + ' ' + and + ' ' + lastPage);
                    return false;
                } else {
                    history.pushState({
                        "page" : history.state.page,
                        "pageNumber" : pageInputValue,
                        "id": history.state.id}, '', null);
                    viewAllNews(pageInputValue);
                    return false;
                }
            });
            
            $("[data-i18n]").i18n();
        }

        $(".header").load("resources/templates/header.html");
        $(".sidebar").load("resources/templates/newsListSidebar.html");
 
        request("GET", "resources/templates/newsStyle.html", null, function(template) {
            request("GET", "resources/templates/pagination.html", null, function(pagination) {
                request("GET", "nm/news/count", null, function(newsCount) {
                    var offset = (pageNumber - 1) * NEWS_PER_PAGE;
                    request("GET", "nm/news?offset=" + offset + "&limit=" + NEWS_PER_PAGE, null, function(data) {
                            renderNewsList(data, template, pagination, newsCount)
                    });
                });
            });
        });

        if (history.state == null || history.state.page != viewAllNews.name) {
            history.pushState({"page" : viewAllNews.name, "pageNumber" : pageNumber, "id": null}, '', null);
        }
    };

    function viewNews(pageNumber, newsId) {

        function renderNews(data, template, commentTemplate, pagination) {
            var COMMENTS_PER_PAGE = 5;
            var $content = $(".content");
            var newsItem = data;
            var newsElement = $(template);
            var tags = "";

            $(".title", newsElement).text(newsItem.title);
            $(".date", newsElement).text(newsItem.modificationDate);
            $(".shortText", newsElement).text(newsItem.shortText);
            $(".fullText", newsElement).text(newsItem.fullText);

            tags = newsItem.tags.map(function(x) {return x.tag;}).join(", ");
            $(".tags", newsElement).text(tags);

            var newData = newsByPage(newsItem.comments, pageNumber, COMMENTS_PER_PAGE);
            for (i = 0; i < newData.length; i++) {
                var cmntElement = $(commentTemplate);

                $(".date", cmntElement).text(newData[i].creationDate);
                $(".commentText", cmntElement).text(newData[i].commentText);

                newsElement.append(cmntElement);
            }

            $content.html(newsElement);
            newsElement.append(pagination);

            for (i = 1; i < (newsItem.comments.length / COMMENTS_PER_PAGE + 1); i++) {
                var template = document.createElement("a");

                $(template).text(i);
                $(template).attr("href", i);
                $(template).attr("class", "pageNumber");

                $("#my-pagination").append($("<li></li>").html(template));
            }

            $(".edit").attr("href", newsItem.newsId);
            $(".del").attr("href", newsItem.newsId);
            $("[data-i18n]").i18n();

            $(".cmntForm").on("submit", function() {
                var comment = {
                    commentText : $("#commentText").val().trim()
                }
                request("POST", "nm/comment/" + newsId, comment, viewNews(firstPage, newsId));
                return false;
            })
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsViewSidebar.html");

        request("GET", "resources/templates/newsView.html", null, function(template) {
            request("GET", "nm/news/" + newsId, null, function(data) {
                request("GET", "resources/templates/singleComment.html", null, function(commentTemplate) {
                    request("GET", "resources/templates/paginationComment.html", null, function(pagination) {
                        renderNews(data, template, commentTemplate, pagination)
                    });
                });
            });
        });
    }

    function addNews() {
        $("[data-i18n]").i18n();
        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsAddFormSidebar.html");

        request("GET", "resources/templates/newsForm.html", null, function(template) {
            $(".content").html(template);
            $("[data-i18n]").i18n();

            var tags = [];
            var authors = [];
            var tagsOptions = [];
            var authorsOptions = [];

            request("GET", "nm/tag/", null, function(data) {
                for (i = 0; i < data.length; i++) {
                    var item = data[i];
                    tagsOptions.push({label: item.tag, value: item.tag, title: item.tag});
                }

                $('#tagsSelect').multiselect({
                    buttonText: function(options, select) {
                        if (options.length === 0) {
                            return i18n.t("multiselect.tags");
                        }
                        else if (this.allSelectedText 
                                    && options.length === $('option', $(select)).length 
                                    && $('option', $(select)).length !== 1 
                                    && this.multiple) {

                            if (this.selectAllNumber) {
                                return this.allSelectedText + ' (' + options.length + ')';
                            }
                            else {
                                return this.allSelectedText;
                            }
                        }
                        else if (options.length > this.numberDisplayed) {
                            return options.length + ' ' + this.nSelectedText;
                        }
                        else {
                            var selected = '';
                            var delimiter = this.delimiterText;

                            options.each(function() {
                                var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).text();
                                selected += label + delimiter;
                            });
                            
                            return selected.substr(0, selected.length - 2);
                        }
                    },
                    onChange: function(option, checked, select) {
                        if (checked == true) {
                            tags.push({tag: option.context.value});
                        } else if (checked == false) {                   
                            var index = tags.map(function(x) {return x.tag;}).indexOf(option.context.value);
                            if (index >= 0) {
                                tags.splice(index, 1);
                            }
                        }
                    }
                });

                $('#tagsSelect').multiselect('dataprovider', tagsOptions);

                $('#tag-add').on('click', function() {
                    var addTagMessage = i18n.t("add.tag");
                    var newTag = prompt(addTagMessage);
                    if (newTag != null){
                        if (newTag == "") {
                            var errorText = i18n.t("error.tagEmpty");
                            alert(errorText);
                        } else if (tagsOptions.map(function(x) {return x.value;}).indexOf(newTag) >= 0) {
                            var errorText = i18n.t("error.tagExist");
                            alert(errorText);
                        } else {
                            var tag = {tag: newTag};
                            tagsOptions.push({label: newTag, value: newTag, title: newTag});
                            request("POST", "nm/tag", tag, null);
                            $('#tagsSelect').append("<option value=" + newTag + ">" + newTag + "</option>");
                            $('#tagsSelect').multiselect('rebuild');
                        }
                    } 
                });
         
            });

            request("GET", "nm/author/", null, function(data) {
                for (i = 0; i < data.length; i++) {
                    var item = data[i];
                    authorsOptions.push({label: item.name, value: item.authorId, title: item.name});
                }

                $('#authorsSelect').multiselect({
                    buttonText: function(options, select) {
                        if (options.length === 0) {
                            return i18n.t("multiselect.authors");
                        }
                        else if (this.allSelectedText 
                                    && options.length === $('option', $(select)).length 
                                    && $('option', $(select)).length !== 1 
                                    && this.multiple) {

                            if (this.selectAllNumber) {
                                return this.allSelectedText + ' (' + options.length + ')';
                            }
                            else {
                                return this.allSelectedText;
                            }
                        }
                        else if (options.length > this.numberDisplayed) {
                            return options.length + ' ' + this.nSelectedText;
                        }
                        else {
                            var selected = '';
                            var delimiter = this.delimiterText;

                            options.each(function() {
                                var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).text();
                                selected += label + delimiter;
                            });

                            return selected.substr(0, selected.length - 2);
                        }
                    },
                    onChange: function(option, checked, select) {
                        if (checked == true) {
                            authors.push({authorId: +option.context.value});
                        } else if (checked == false) {
                            var index = authors.map(function(x) {return x.authorId; }).indexOf(+option.context.value); 
                            if (index >= 0) {
                                authors.splice(index, 1);
                            }
                        }
                    }
                });
                
                $('#authorsSelect').multiselect('dataprovider', authorsOptions);
                
                $('#author-add').on('click', function() {
                    var addAuthorMessage = i18n.t("add.author");
                    var newAuthor = prompt(addAuthorMessage);
                    if (newAuthor != null){
                        if (newAuthor == "") {
                            var errorText = i18n.t("error.authorEmpty");
                            alert(errorText);
                        } else if (authorsOptions.map(function(x) {return x.title;}).indexOf(newAuthor) >= 0) {
                            var errorText = i18n.t("error.authorExist");
                            alert(errorText);
                        } else {
                            var author = {name: newAuthor};
                            request("POST", "nm/author", author, function(data) {
                                authorsOptions.push({label: data.name, value: data.authorId, title: data.name});
                                $('#authorsSelect').append("<option value=" + data.authorId + ">" + data.name + "</option>");
                                $('#authorsSelect').multiselect('rebuild');
                            });
                        }
                    } 
                });
            });

            $(".newsForm").on("submit", function() {
                if (authors.length == 0) {
                    var addAuthorError = i18n.t("error.addAuthor");
                    alert(addAuthorError);
                } else {
                    var news = {
                            title : $("#title").val().trim(),
                            shortText : $("#shortText").val().trim(),
                            fullText : $("#fullText").val().trim(),
                            authors : authors,
                            tags: tags
                        }
                    request("POST", "nm/news", news, viewAllNews(firstPage));
                }
                return false;
            })
        })
    }

    function editNews(newsId) {

        function renderAndEditNews(data, template) {
            var $content = $(".content");
            var newsItem = data;
            var newsElement = $(template);

            $("#title", newsElement).text(newsItem.title);
            $("#shortText", newsElement).text(newsItem.shortText);
            $("#fullText", newsElement).text(newsItem.fullText);
            $(".del").attr("href", newsItem.newsId);
            $(".viewComments").attr("href", newsItem.newsId);
            $content.html(newsElement);
            $("[data-i18n]").i18n();

            var tags = newsItem.tags;
            var authors = newsItem.authors;
            var tagsOptions = [];
            var authorsOptions = [];

            request("GET", "nm/tag/", null, function(data) {
                for (i = 0; i < data.length; i++) {
                    var item = data[i];
                    if (tags.map(function(x) {return x.tag;}).indexOf(item.tag) >= 0) {
                        tagsOptions.push({label: item.tag, value: item.tag, title: item.tag, selected: true});
                    } else {
                        tagsOptions.push({label: item.tag, value: item.tag, title: item.tag});
                    }
                }

                $('#tagsSelect').multiselect({
                    buttonText: function(options, select) {
                        if (options.length === 0) {
                            return i18n.t("multiselect.tags");
                        }
                        else if (this.allSelectedText 
                                    && options.length === $('option', $(select)).length 
                                    && $('option', $(select)).length !== 1 
                                    && this.multiple) {

                            if (this.selectAllNumber) {
                                return this.allSelectedText + ' (' + options.length + ')';
                            }
                            else {
                                return this.allSelectedText;
                            }
                        }
                        else if (options.length > this.numberDisplayed) {
                            return options.length + ' ' + this.nSelectedText;
                        }
                        else {
                            var selected = '';
                            var delimiter = this.delimiterText;

                            options.each(function() {
                                var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).text();
                                selected += label + delimiter;
                            });

                            return selected.substr(0, selected.length - 2);
                        }
                    },
                    onChange: function(option, checked, select) {
                        if (checked == true) {
                            tags.push({tag: option.context.value});
                        } else if (checked == false) {
                            var index = tags.map(function(x) {return x.tag;}).indexOf(option.context.value);
                            if (index >= 0) {
                                tags.splice(index, 1);
                            }
                        }
                    }

                });
                
                $('#tagsSelect').multiselect('dataprovider', tagsOptions);
                
                $('#tag-add').on('click', function() {
                    var addTagMessage = i18n.t("add.tag");
                    var newTag = prompt(addTagMessage);
                    if (newTag != null){
                        if (newTag == "") {
                            var errorText = i18n.t("error.tagEmpty");
                            alert(errorText);
                        } else if (tagsOptions.map(function(x) {return x.value;}).indexOf(newTag) >= 0) {
                            var errorText = i18n.t("error.tagExist");
                            alert(errorText);
                        } else {
                            var tag = {tag: newTag};
                            tagsOptions.push({label: newTag, value: newTag, title: newTag});
                            request("POST", "nm/tag", tag, null);
                            $('#tagsSelect').append("<option value=" + newTag + ">" + newTag + "</option>");
                            $('#tagsSelect').multiselect('rebuild');
                        }
                    } 
                });
            });
            
            request("GET", "nm/author/", null, function(data) {
                for (i = 0; i < data.length; i++) {
                    var item = data[i];
                    if (authors.map(function(x) {return x.authorId;}).indexOf(item.authorId) >= 0) {
                        authorsOptions.push({label: item.name, value: item.authorId, title: item.name, selected: true});
                    } else {
                        authorsOptions.push({label: item.name, value: item.authorId, title: item.name});
                    }
                }

                $('#authorsSelect').multiselect({
                    buttonText: function(options, select) {
                        if (options.length === 0) {
                            return i18n.t("multiselect.authors");
                        }
                        else if (this.allSelectedText 
                                    && options.length === $('option', $(select)).length 
                                    && $('option', $(select)).length !== 1 
                                    && this.multiple) {

                            if (this.selectAllNumber) {
                                return this.allSelectedText + ' (' + options.length + ')';
                            }
                            else {
                                return this.allSelectedText;
                            }
                        }
                        else if (options.length > this.numberDisplayed) {
                            return options.length + ' ' + this.nSelectedText;
                        }
                        else {
                            var selected = '';
                            var delimiter = this.delimiterText;

                            options.each(function() {
                                var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).text();
                                selected += label + delimiter;
                            });

                            return selected.substr(0, selected.length - 2);
                        }
                    },
                    onChange: function(option, checked, select) {
                        if (checked == true) {
                            authors.push({authorId: +option.context.value});
                        } else if (checked == false) {              
                            var index = authors.map(function(x) {return x.authorId; }).indexOf(+option.context.value); 
                            if (index >= 0) {
                                authors.splice(index, 1);
                            }
                        }
                    }
                });

                $('#authorsSelect').multiselect('dataprovider', authorsOptions);

                $('#author-add').on('click', function() {
                    var addAuthorMessage = i18n.t("add.author");
                    var newAuthor = prompt(addAuthorMessage);
                    if (newAuthor != null){
                        if (newAuthor == "") {
                            var errorText = i18n.t("error.authorEmpty");
                            alert(errorText);
                        } else if (authorsOptions.map(function(x) {return x.title;}).indexOf(newAuthor) >= 0) {
                            var errorText = i18n.t("error.authorExist");
                            alert(errorText);
                        } else {
                            var author = {name: newAuthor};
                            request("POST", "nm/author", author, function(data) {
                                authorsOptions.push({label: data.name, value: data.authorId, title: data.name});
                                $('#authorsSelect').append("<option value=" + data.authorId + ">" + data.name + "</option>");
                                $('#authorsSelect').multiselect('rebuild');
                            });
                        }
                    } 
                });
            });

            $(".newsForm").on("submit", function() {
                if (authors.length == 0) {
                    var addAuthorError = i18n.t("error.addAuthor");
                    alert(addAuthorError);
                } else {
                    var news = {
                        newsId : newsId,
                        title : $("#title").val().trim(),
                        shortText : $("#shortText").val().trim(),
                        fullText : $("#fullText").val().trim(),
                        authors : authors,
                        tags: tags
                    }
                    var editUrl = "nm/news/" + newsId;
                    request("PUT", editUrl, news, function() {editSuccess(firstPage, newsId)});
                }
                return false;
            })
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsEditFormSidebar.html");

        request("GET", "resources/templates/newsForm.html", null, function(template) {
            request("GET", "nm/news/" + newsId, null, function(data) {
                renderAndEditNews(data, template)
            });
        });
    }

    function deleteNews(newsId) {
        var deleteUrl = "nm/news/" + newsId;
        history.pushState(window.history.state, '', null);
        if (window.history.state.page == "viewNews" || window.history.state.page == "editNews") {
            request("DELETE", deleteUrl, null, viewAllNews(firstPage));
        } else {
            request("DELETE", deleteUrl, null, window.history.back());
        }
        return false;
    }

    function deleteComment(commentId) {
        var deleteUrl = "nm/comment/" + commentId;
        request("DELETE", deleteUrl, null, viewComments(firstPage, history.state.id));
        return false;
    }

    function viewTopCommentedNews(pageNumber, count) {

        function renderNewsByComments(data, template, pagination) {
            var $content = $(".content");
            document.getElementById("commentList").style.display = "block";
            $('.top[href="'+count+'"]').addClass("chosen");

            $content.empty();

            var newData = newsByPage(data, pageNumber, NEWS_PER_PAGE);
            for (i = 0; i < newData.length; i++) {
                var newsItem = newData[i];
                var newsElement = $(template);
                
                $(".title", newsElement).text(newsItem.title);
                $(".date", newsElement).text(newsItem.modificationDate);
                $(".shortText", newsElement).text(newsItem.shortText);
                $(".view", newsElement).attr("href", newsItem.newsId);
                $(".edit", newsElement).attr("href", newsItem.newsId);
                $(".del", newsElement).attr("href", newsItem.newsId);
                $content.append(newsElement);
            }
            $("[data-i18n]").i18n();

            $content.append(pagination);

            var lastPage = Math.ceil(data.length / NEWS_PER_PAGE);
            var of = i18n.t("message.of");
            var and = i18n.t("message.and");
            var invalidPageMessage = i18n.t("error.invalidPageNumber");
            
            $(".leftArrow").attr("href", pageNumber);
            $(".rightArrow").attr("href", pageNumber);
            $("#myInp").attr("placeholder", pageNumber + ' ' + of + ' ' + lastPage);

            if (pageNumber == firstPage) {
                document.getElementById("left").style.visibility = "hidden";
            }
            if (pageNumber >= (data.length / NEWS_PER_PAGE)) {
                document.getElementById("right").style.visibility = "hidden";
            }
            
            $('#myInputForm').submit(function() {
                var pageInput = $("#myInp");
                var pageInputValue = pageInput.val();
                
                if (pageInputValue < firstPage || pageInputValue > lastPage || !pageInputValue.match(/\d+/)) {
                    alert(invalidPageMessage + firstPage + ' ' + and + ' ' + lastPage);
                    return false;
                } else {
                    history.pushState({
                        "page" : history.state.page,
                        "pageNumber" : +pageInputValue,
                        "id": history.state.id}, '', null);
                    viewTopCommentedNews(pageInputValue, count);
                    return false;
                }
            });
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsListSidebar.html");

        request("GET", "resources/templates/newsStyle.html", null, function(template) {
            request("GET", "resources/templates/pagination.html", null, function(pagination) {
                request("GET", "nm/news/top?count=" + count, null, function(data) {
                    renderNewsByComments(data, template, pagination)
                });
            });
        });
    };

    function viewNewsByTag(pageNumber, tag) {

        function renderNewsByTag(data, template, pagination, newsCount) {
            var $content = $(".content");
            getTagList(tag);
            document.getElementById("tagList").style.display = "block";

            $content.empty();

            for (i = 0; i < data.length; i++) {
                var newsItem = data[i];
                var newsElement = $(template);
                
                $(".title", newsElement).text(newsItem.title);
                $(".date", newsElement).text(newsItem.modificationDate);
                $(".shortText", newsElement).text(newsItem.shortText);
                $(".view", newsElement).attr("href", newsItem.newsId);
                $(".edit", newsElement).attr("href", newsItem.newsId);
                $(".del", newsElement).attr("href", newsItem.newsId);
                $content.append(newsElement);
            }
            $("[data-i18n]").i18n();

            $content.append(pagination);

            var lastPage = Math.ceil(newsCount / NEWS_PER_PAGE);
            var of = i18n.t("message.of");
            var and = i18n.t("message.and");
            var invalidPageMessage = i18n.t("error.invalidPageNumber");
            
            $(".leftArrow").attr("href", pageNumber);
            $(".rightArrow").attr("href", pageNumber);
            $("#myInp").attr("placeholder", pageNumber + ' ' + of + ' ' + lastPage);

            if (pageNumber == firstPage) {
                document.getElementById("left").style.visibility = "hidden";
            }
            if (pageNumber >= (newsCount / NEWS_PER_PAGE)) {
                document.getElementById("right").style.visibility = "hidden";
            }
            
            $('#myInputForm').submit(function() {
                var pageInput = $("#myInp");
                var pageInputValue = pageInput.val();

                if (pageInputValue < firstPage || pageInputValue > lastPage || !pageInputValue.match(/\d+/)) {
                    alert(invalidPageMessage + firstPage + ' ' + and + ' ' + lastPage);
                    return false;
                } else {
                    history.pushState({
                        "page" : history.state.page,
                        "pageNumber" : pageInputValue,
                        "id": history.state.id}, '', null);
                    viewNewsByTag(pageInputValue, tag);
                    return false;
                }
            });
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsListSidebar.html");

        request("GET", "resources/templates/newsStyle.html", null, function(template) {
            request("GET", "resources/templates/pagination.html", null, function(pagination) {
                request("GET", "nm/news/count/tag?tag=" + tag, null, function(newsCount) {
                    var offset = (pageNumber - 1) * NEWS_PER_PAGE;
                    request("GET", "nm/news/tag?tag=" + tag + "&offset=" + offset + "&limit=" + NEWS_PER_PAGE,
                            null, function(data) {
                        renderNewsByTag(data, template, pagination, newsCount)
                    });
                });
            });
        });
    };

    function viewNewsByAuthor(pageNumber, authorId) {

        function renderNewsByAuthor(data, template, pagination, newsCount) {
            var $content = $(".content");
            getAuthorList(authorId);
            document.getElementById("authorList").style.display = "block";

            $content.empty();

            for (i = 0; i < data.length; i++) {
                var newsItem = data[i];
                var newsElement = $(template);
                
                $(".title", newsElement).text(newsItem.title);
                $(".date", newsElement).text(newsItem.modificationDate);
                $(".shortText", newsElement).text(newsItem.shortText);
                $(".view", newsElement).attr("href", newsItem.newsId);
                $(".edit", newsElement).attr("href", newsItem.newsId);
                $(".del", newsElement).attr("href", newsItem.newsId);
                $content.append(newsElement);
            }
            $("[data-i18n]").i18n();

            $content.append(pagination);

            var lastPage = Math.ceil(newsCount / NEWS_PER_PAGE);
            var of = i18n.t("message.of");
            var and = i18n.t("message.and");
            var invalidPageMessage = i18n.t("error.invalidPageNumber");
            
            $(".leftArrow").attr("href", pageNumber);
            $(".rightArrow").attr("href", pageNumber);
            $("#myInp").attr("placeholder", pageNumber + ' ' + of + ' ' + lastPage);

            if (pageNumber == firstPage) {
                document.getElementById("left").style.visibility = "hidden";
            }
            if (pageNumber >= (newsCount / NEWS_PER_PAGE)) {
                document.getElementById("right").style.visibility = "hidden";
            }
            
            $('#myInputForm').submit(function() {
                var pageInput = $("#myInp");
                var pageInputValue = pageInput.val();

                if (pageInputValue < firstPage || pageInputValue > lastPage || !pageInputValue.match(/\d+/)) {
                    alert(invalidPageMessage + firstPage + ' ' + and + ' ' + lastPage);
                    return false;
                } else {
                    history.pushState({
                        "page" : history.state.page,
                        "pageNumber" : pageInputValue,
                        "id": history.state.id}, '', null);
                    viewNewsByAuthor(pageInputValue, authorId);
                    return false;
                }
            });
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/newsListSidebar.html");

        request("GET", "resources/templates/newsStyle.html", null, function(template) {
            request("GET", "resources/templates/pagination.html", null, function(pagination) {
                request("GET", "nm/news/count/author?id=" + authorId, null, function(newsCount) {
                    var offset = (pageNumber - 1) * NEWS_PER_PAGE;
                    request("GET", "nm/news/author?id=" + authorId + "&offset=" + offset + "&limit=" + NEWS_PER_PAGE,
                            null, function(data) {
                        renderNewsByAuthor(data, template, pagination, newsCount)
                    });
                });
            });
        });
    };

    function viewComments(pageNumber, newsId) {

        function renderNewsList(data, template, pagination) {
            var COMMENTS_PER_PAGE = 12;
            var $content = $(".content");
            $content.empty();

            var newData = newsByPage(data, pageNumber, COMMENTS_PER_PAGE);
            for (i = 0; i < newData.length; i++) {
                var commentItem = newData[i];
                var commentElement = $(template);
                
                $(".date", commentElement).text(commentItem.creationDate);
                $(".commentText", commentElement).text(commentItem.commentText);
                $(".delCmnt", commentElement).attr("href", commentItem.commentId);
                $content.append(commentElement);
            }

            $content.append(pagination);

            var $pagenationBlock = $(".pagination");
            for (i = 1; i < (data.length / COMMENTS_PER_PAGE + 1); i++) {
                var template = document.createElement("a");

                $(template).text(i);
                $(template).attr("href", i);
                $(template).attr("class", "pageNumber");
                
                $pagenationBlock.append($("<li></li>").html(template));
            }

            $("[data-i18n]").i18n();
        }

        $(".header").load("resources/templates/headerBack.html");
        $(".sidebar").load("resources/templates/viewCommentsSidebar.html");

        request("GET", "resources/templates/singleCommentDelete.html", null, function(template) {
            request("GET", "resources/templates/paginationComment.html", null, function(pagination) {
                request("GET", "nm/news/" + newsId, null, function(data) {
                            renderNewsList(data.comments, template, pagination)
                        });
                    });
        });
    };

    function newsByPage(data, pageNumber, itemsPerPage) {
        var newData = [];
        var startIndex = (pageNumber - 1) * itemsPerPage;

        if (pageNumber * itemsPerPage < data.length) {
            newData = data.slice(startIndex, startIndex + itemsPerPage);
        } else {
            newData = data.slice(startIndex, data.length)
        }
        return newData;
    }

    function getTagList(chosenTag) {

        function renderTagList(data) {
            var $content = $("#tagList");

            $content.empty();
            for (i = 0; i < data.length; i++) {
                var item = data[i];
                
                if (item.newsCount != 0){
                    var template = document.createElement("a");

                    $(template).text(item.tag + "(" + item.newsCount + ")" );
                    $(template).attr("href", item.tag);
                    $(template).attr("class", "tag");

                    if (chosenTag == item.tag) {
                        //$('a[href="'+chosenTag+'"]').attr("background-color", "yellow");
                        $(template).addClass("chosen");
                    }

                    $content.append($("<li></li>").html(template));
                }
            }
        }

        request("GET", "nm/tag/", null, function(data) {
            renderTagList(data);
        });
    };

    function getAuthorList(chosenAuthor) {

        function renderAuthorList(data) {
            var $content = $("#authorList");

            $content.empty();
            for (i = 0; i < data.length; i++) {
                var item = data[i];
                if (item.newsCount != 0){
                    var template = document.createElement("a");

                    $(template).text(item.name + "(" + item.newsCount + ")" );
                    $(template).attr("href", item.authorId);
                    $(template).attr("class", "author");

                    if (chosenAuthor == item.authorId) {
                        //$('a[href="'+chosenTag+'"]').attr("background-color", "yellow");
                        $(template).addClass("chosen");
                    }

                    $content.append($("<li></li>").html(template));
                }
                
            }
        }

        request("GET", "nm/author/", null, function(data) {
            renderAuthorList(data)
        });
    };

    return {
        viewAllNews : viewAllNews,
        viewNews : viewNews,
        addNews : addNews,
        editNews : editNews,
        deleteNews : deleteNews,
        viewTopCommentedNews : viewTopCommentedNews,
        viewNewsByTag : viewNewsByTag,
        viewNewsByAuthor : viewNewsByAuthor,
        getTagList : getTagList,
        getAuthorList : getAuthorList,
        viewComments: viewComments,
        deleteComment: deleteComment
    }
}());