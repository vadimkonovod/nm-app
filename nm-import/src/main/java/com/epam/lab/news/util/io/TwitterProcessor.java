package com.epam.lab.news.util.io;

import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;
import com.epam.lab.news.service.AuthorService;
import com.epam.lab.news.service.TagService;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TwitterProcessor {

    private final static String ACCESS_TOKEN = "3548254588-vuZRfl9HCVvG3IgnABpQEcVpWxIqnxetz1TGQJr";
    private final static String ACCESS_SECRET = "RD5JJOnsn0eqECVRDsc9q6ISX8G0uqwGVMHKaMyoYVDn4";
    private final static String CONSUMER_KEY = "Zy5GyUTyDWHOULjLpuF3CrC0s";
    private final static String CONSUMER_SECRET = "Rt5xGTPrivaXNkpXYUrdIPVe2apTGMDwGEZE4yooKuhaShdE6d";

    @Autowired
    private AuthorService authorService;

    @Autowired
    private TagService tagService;

    private List<News> newsList = null;

    public List<News> getNews(){
        newsList = new ArrayList<>();

        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_SECRET);
        //String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=3548254588&screen_name=vadimkonovod";
        String url = "https://api.twitter.com/1.1/statuses/home_timeline.json";
        HttpGet request = new HttpGet(url);

        try {
            consumer.sign(request);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);

            News n = null;
            JSONArray tweets = new JSONArray(EntityUtils.toString(response.getEntity()));
            for (int i = 0; i < tweets.length(); i++) {
                JSONObject tweet = tweets.getJSONObject(i);
                JSONObject author = (JSONObject) tweet.get("user");

                String tweetText = tweet.get("text").toString();
                String tweetAuthorName = author.get("name").toString();
                Date tweetDate = getTwitterDate(tweet.get("created_at").toString());

                n = makeNews(tweetText, tweetDate, tweetAuthorName, getTags(tweet));
                newsList.add(0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to get timeline: " + e.getMessage());
        }
        return newsList;
    }

    private News makeNews(String tweetText, Date tweetDate, String tweetAuthorName, Set<Tag> tags){
        News n = new News();
        Set<Author> authors = new HashSet<>();

        n.setTitle(tweetText.substring(0, Math.min(tweetText.length(), 30)));
        n.setShortText(tweetText.substring(0, Math.min(tweetText.length(), 100)));
        n.setFullText(tweetText);
        n.setCreationDate(tweetDate);

        authors.add(getAuthor(tweetAuthorName));
        n.setAuthors(authors);

        n.setTags(tags);
        return n;
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

    private Set<Tag> getTags(JSONObject tweet) throws JSONException {
        HashSet<Tag> tags = new HashSet<>();
        JSONObject entities = (JSONObject) tweet.get("entities");
        JSONArray hashtags = entities.getJSONArray("hashtags");
        for (int j = 0; j < hashtags.length(); j++) {
            JSONObject hashtag = hashtags.getJSONObject(j);
            String tagText = hashtag.get("text").toString();

            Tag tag = null;
            for (Tag t : tagService.readAll()) {
                if (t.getTag().equals(tagText)) {
                    tag = t;
                    break;
                }
            }

            if (tag == null) {
                tag = new Tag();
                tag.setTag(tagText);
                tag = tagService.create(tag);
            }
            tags.add(tag);
        }
        return tags;
    }

    private static Date getTwitterDate(String date) throws ParseException {
        final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
        sf.setLenient(true);
        return sf.parse(date);
    }
}
