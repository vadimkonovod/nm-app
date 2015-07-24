package com.epam.lab.news.job.factory;

import com.epam.lab.news.job.NewsImportJob;

public interface NewsImportJobFactory {
    NewsImportJob getNewsImportJob(String type);
}