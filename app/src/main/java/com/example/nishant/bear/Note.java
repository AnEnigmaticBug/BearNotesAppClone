package com.example.nishant.bear;

import java.util.ArrayList;
import java.util.List;

public final class Note {

    private NoteMetaData metaData;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

class NoteMetaData {

    private long id;
    private List<String> tags;
    private String title;
    private String preview;
    private String date;
    private List<String> contributors;

    NoteMetaData() {

    }

    NoteMetaData(List<String> tags, String title) {
        this.tags = tags;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }
}
