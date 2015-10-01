package com.epam.lab.news.job.impl;

import com.epam.lab.news.domain.News;
import com.epam.lab.news.job.NewsImportJob;
import com.epam.lab.news.service.NewsService;
import com.epam.lab.news.util.io.Twitter4JProcessor;
import com.epam.lab.news.util.io.TwitterProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component("Twitter")
public class TwitterNewsImportJob implements NewsImportJob {
    @Value("${import.initDelay}")
    private Long initDelay;

    @Value("${import.twitter.delay}")
    private Long delay;

    @Autowired
    private TwitterProcessor twitterProcessor;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ScheduledThreadPoolExecutor scheduled;

    @Override
    public void launchImport() {
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<News> n = twitterProcessor.getNews();
                newsService.insertListNews(n);
            }
        }, initDelay, delay, TimeUnit.SECONDS);
    }
}
