package com.xiepuhuan.reptile.model;

import org.apache.commons.codec.Charsets;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author xiepuhuan
 */
public class Content {

    private static final String TEXT_CONTENT_TYPE_PREFIX = "text";

    private final ContentType contentType;

    private final byte[] content;

    private String textContent;

    private Document htmlContent;

    public Content(ContentType contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getTextContent() {
        if (textContent != null) {
            return textContent;
        }

        if (contentType.getMimeType().startsWith(TEXT_CONTENT_TYPE_PREFIX)) {
            textContent = new String(content, Charsets.toCharset(contentType.getCharset()));
        }

        return textContent;
    }

    public Content setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public Document getHtmlContent() {
        if (htmlContent != null) {
            return htmlContent;
        }

        if (ContentType.TEXT_HTML.getMimeType().equals(contentType.getMimeType())) {
            htmlContent = Jsoup.parse(getTextContent());
        }

        return htmlContent;
    }

    public Content setHtmlContent(Document htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Content{");
        sb.append("contentType=").append(contentType);
        sb.append(", content=");
        sb.append(getTextContent() == null ? content : textContent);
        sb.append('}');
        return sb.toString();
    }
}
