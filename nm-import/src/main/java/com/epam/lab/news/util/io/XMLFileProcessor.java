package com.epam.lab.news.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.epam.lab.news.domain.News;
import com.epam.lab.news.exception.ValidationException;
import com.epam.lab.news.service.NewsService;
import com.epam.lab.news.util.parser.StAXNewsParser;
import com.epam.lab.news.util.validator.NewsValidator;

@Component
public class XMLFileProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLFileProcessor.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private NewsValidator validator;

    private Queue<File> processedFiles;
    private Queue<File> availableFiles;

    private Lock lock = new ReentrantLock();

    public XMLFileProcessor() {
        this.processedFiles = new LinkedList<File>();
        this.availableFiles = new LinkedList<File>();
    }

    public Queue<File> getAvailableFile() {
        return this.availableFiles;
    }

    public File takeAvailableFile() {
        try {
            lock.lock();
            File file = this.availableFiles.poll();
            if (file != null) {
                this.processedFiles.add(file);
            }
            return file;
        } finally {
            lock.unlock();
        }
    }

    public void deleteProcessedFile(File file) {
        this.processedFiles.remove(file);
    }

    public void scanDirectory(File baseDir, File errorDir) {
        searchFiles(baseDir, errorDir);
    }

    private void searchFiles(File dir, File errorDir) {
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                try {
                    lock.lock();
                    if (!this.availableFiles.contains(f) && !this.processedFiles.contains(f)) {
                        this.availableFiles.add(f);
                    }
                } finally {
                    lock.unlock();
                }
            } else if (f.isDirectory() && (!(errorDir.getPath()).equals(f.getPath()))) {
                searchFiles(new File(f.getAbsolutePath()), errorDir);
            }
        }
    }

    private boolean validate(String filePath, List<News> newsList) {
        boolean flag = true;
        for (News n : newsList) {
            try {
                validator.isValid(n);
            } catch (ValidationException e) {
                flag = false;
                LOGGER.error("Not valid data in file: " + filePath + " : " + e.getMessage());
            }
        }
        return flag;
    }

    public void parseFile(File file, File errorDir) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        List<News> newsList = null;
        try (InputStream input = new FileInputStream(file)) {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(input);
            StAXNewsParser parser = (StAXNewsParser) context.getBean("stAXNewsParser");
            newsList = parser.process(reader);
            if (validate(file.getPath(), newsList)) {
                newsService.insertListNews(newsList);
                deleteFile(file);
            }
        } catch (XMLStreamException e) {
            LOGGER.error("Incorrect file: " + file.getPath(), e);
        }  catch (FileNotFoundException e) {
            LOGGER.error("File " + file.getPath() + " not found", e);
        } catch (IOException e) {
            LOGGER.error("Error occured while reading data", e);
        }
        moveFile(file, errorDir);
    }

    private boolean moveFile(File file, File dir) {
        if (!file.exists()) {
            return false;
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File target = new File(dir, file.getName());
        try {
            Files.move(Paths.get(file.getPath()), Paths.get(target.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean deleteFile(File file) {
        return file.delete();
    }
}