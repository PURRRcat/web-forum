package ru.forum.service.dto;

import ru.forum.model.Category;

import java.time.LocalDateTime;

public class CategoryStats {
    private final Category category;
    private final long topicCount;
    private final long postCount;
    private final String lastAuthor;
    private final LocalDateTime lastPostAt;

    public CategoryStats(Category category, long topicCount, long postCount,
                         String lastAuthor, LocalDateTime lastPostAt) {
        this.category = category;
        this.topicCount = topicCount;
        this.postCount = postCount;
        this.lastAuthor = lastAuthor;
        this.lastPostAt = lastPostAt;
    }

    public Category getCategory()      { return category; }
    public long getTopicCount()        { return topicCount; }
    public long getPostCount()         { return postCount; }
    public String getLastAuthor()      { return lastAuthor; }
    public LocalDateTime getLastPostAt() { return lastPostAt; }
}
