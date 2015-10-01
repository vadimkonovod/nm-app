package com.epam.lab.news.util.io;

import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;
import com.epam.lab.news.service.AuthorService;
import com.epam.lab.news.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.util.*;

@Component
public class Twitter4JProcessor {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private TagService tagService;

    private List<News> newsList = null;

    public List<News> getNews() {
        newsList = new ArrayList<>();

        try {
            Twitter twitter = new TwitterFactory().getInstance();
            User user = twitter.verifyCredentials();
            List<Status> statuses = twitter.getHomeTimeline();
            News n = null;
            Set<Author> authors = null;

            for (Status status : statuses) {
                n = new News();
                authors = new HashSet<>();

                String tweetText = status.getText();
                String tweetAuthorName = status.getUser().getName();
                Date tweetDate = status.getCreatedAt();

                n.setTitle(tweetText.substring(0, Math.min(tweetText.length(), 30)));
                n.setShortText(tweetText.substring(0, Math.min(tweetText.length(), 100)));
                n.setFullText(tweetText);
                n.setCreationDate(tweetDate);

                authors.add(getAuthor(tweetAuthorName));
                n.setAuthors(authors);

                n.setTags(getTags(status));

                newsList.add(0, n);
             }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
        return newsList;
    }

    private Author getAuthor(String tweetAuthorName) {
        Author author = null;

        for (Author a : authorService.readAll()) {
            if (a.getName().equals(tweetAuthorName)) {
                author = a;
                break;
            }
        }

        if (author == null) {
            author = new Author();
            author.setName(tweetAuthorName);
            author = authorService.create(author);
        }
        return author;
    }

    private Set<Tag> getTags(Status tweet) {
        HashSet<Tag> tags = new HashSet<>();
        for (HashtagEntity hashtag : tweet.getHashtagEntities()) {
            Tag tag = null;
            for (Tag t : tagService.readAll()) {
                if (t.getTag().equals(hashtag.getText())) {
                    tag = t;
                    break;
                }
            }

            if (tag == null) {
                tag = new Tag();
                tag.setTag(hashtag.getText());
                tag = tagService.create(tag);
            }
            tags.add(tag);
        }
        return tags;
    }
}
