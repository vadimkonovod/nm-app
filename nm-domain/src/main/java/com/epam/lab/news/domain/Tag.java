package com.epam.lab.news.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Formula;

@Entity
@Table(name = "TAG")
@NamedQueries({ @NamedQuery(name = "getAllTag", query = "SELECT t FROM Tag t") })
public class Tag implements Serializable {
    private static final long serialVersionUID = -5426752557428630080L;

    @Id
    @Column(name = "TAG")
    @NotNull(message = "error.tag.notNull")
    @Size(min = 1, max = 100, message = "error.tag.size")
    private String tag;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "NEWS_TAG", joinColumns = { @JoinColumn(name = "TAG") }, inverseJoinColumns = { @JoinColumn(
            name = "NEWS_ID") }, uniqueConstraints = { @UniqueConstraint(columnNames = { "NEWS_ID", "TAG" }) })
    private List<News> news;

    @Formula("(SELECT COUNT(*) FROM NEWS_TAG WHERE NEWS_TAG.TAG = TAG)")
    private Long newsCount;

    public Tag() {
    }

    public Tag(String tag) {
        super();
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
    
    public Long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Long newsCount) {
        this.newsCount = newsCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Tag))
            return false;
        Tag other = (Tag) obj;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Tag.class.getCanonicalName() + "[");
        sb.append("tag=").append(tag);
        sb.append("]");
        return sb.toString();
    }
}
