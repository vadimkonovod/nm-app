package com.epam.lab.news.util.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.epam.lab.news.domain.Author;
import com.epam.lab.news.domain.News;
import com.epam.lab.news.domain.Tag;

@Component
@Scope("prototype")
public class StAXNewsParser {
    private List<News> newsList = new ArrayList<News>();
    private News news = null;
    private Set<Author> authors = null;
    private Set<Tag> tags = null;
    private Author author = null;
    private Tag tag = null;
    private String elementName = null;

    public List<News> process(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int type = reader.next();
            switch (type) {
                case XMLStreamConstants.START_ELEMENT:
                    elementName = reader.getLocalName().toUpperCase();
                    processStartElement(elementName);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = reader.getText().trim();
                    if (text.isEmpty()) {
                        break;
                    }
                    processCharacters(elementName, text);
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName().toUpperCase();
                    processEndElement(elementName);
            }
        }
        return newsList;
    }

    private void processStartElement(String elementName) {
        switch (elementName) {
        case "NEWS":
            news = new News();
            break;
        case "AUTHORS":
            authors = new HashSet<Author>();
            break;
        case "TAGS":
            tags = new HashSet<Tag>();
            break;
        default:
            break;
        }
    }

    private void processCharacters(String elementName, String text) {
        switch (elementName) {
        case "TITLE":
            news.setTitle(text);
            break;
        case "SHORT_TEXT":
            news.setShortText(text);
            break;
        case "FULL_TEXT":
            news.setFullText(text);
            break;
        case "AUTHOR":
            author = new Author();
            author.setAuthorId(Long.valueOf(text));
            authors.add(author);
            break;
        case "TAG":
            tag = new Tag();
            tag.setTag(text);
            tags.add(tag);
            break;
        default:
            break;
        }
    }

    private void processEndElement(String elementName) {
        switch (elementName) {
        case "NEWS":
            newsList.add(news);
        case "AUTHORS":
            news.setAuthors(authors);
            break;
        case "TAGS":
            news.setTags(tags);
            break;
        default:
            break;
        }
    }
}

