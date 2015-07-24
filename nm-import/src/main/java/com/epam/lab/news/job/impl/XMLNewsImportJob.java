package com.epam.lab.news.job.impl;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epam.lab.news.job.NewsImportJob;
import com.epam.lab.news.util.io.XMLFileProcessor;

@Component("XML")
public class XMLNewsImportJob implements NewsImportJob {
    @Value("${import.initDelay}")
    private Long initDelay;

    @Value("${import.delay}")
    private Long delay;

    @Value("${import.base.dir}")
    private String baseDirPath;

    @Value("${import.error.dir}")
    private String errorDirPath;

    @Autowired
    private XMLFileProcessor fileProcessor;

    @Autowired
    private ScheduledThreadPoolExecutor scheduled;

    @Autowired
    private ExecutorService fixed;

    @Override
    public void launchImport() {
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                fileProcessor.scanDirectory(new File(baseDirPath), new File(errorDirPath));
                while (!fileProcessor.getAvailableFile().isEmpty()) {
                    final File f = fileProcessor.takeAvailableFile();
                    if (f != null) {
                        fixed.execute(new Runnable() {
                            @Override
                            public void run() {
                                fileProcessor.parseFile(f, new File(errorDirPath));
                                fileProcessor.deleteProcessedFile(f);
                            }
                        });
                    }
                }
            }
        }, initDelay, delay, TimeUnit.SECONDS);
    }
}
