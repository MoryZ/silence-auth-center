package com.old.silence.auth.center.vo;

/**
 * @author moryzang
 */
public class OcrResult {
    private String title;
    private String words;

    public OcrResult() {
    }

    public OcrResult(String words) {
        this.words = words;
    }

    public OcrResult(String title, String words) {
        this.title = title;
        this.words = words;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
