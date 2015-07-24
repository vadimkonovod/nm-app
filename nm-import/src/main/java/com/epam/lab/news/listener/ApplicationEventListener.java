package com.epam.lab.news.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.epam.lab.news.job.factory.NewsImportJobFactory;

@Component
public class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${import.file.type}")
    private String fileType;

    @Autowired
    private NewsImportJobFactory newsImportJobFactory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        newsImportJobFactory.getNewsImportJob(fileType).launchImport();
    }
}
