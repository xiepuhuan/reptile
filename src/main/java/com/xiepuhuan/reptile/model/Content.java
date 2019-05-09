package com.xiepuhuan.reptile.model;

import org.apache.http.entity.ContentType;

/**
 * @author xiepuhuan
 */
public class Content {

    private ContentType contentType;

    private byte[] content;

    private String textContent;

    public Content(ContentType contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Content setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Content setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getTextContent() {
        return textContent;
    }

    public Content setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Content{");
        sb.append("contentType=").append(contentType);
        sb.append(", content=");
        sb.append(textContent == null ? content : textContent);
        sb.append('}');
        return sb.toString();
    }
}
