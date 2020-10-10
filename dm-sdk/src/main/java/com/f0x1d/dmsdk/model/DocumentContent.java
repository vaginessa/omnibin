package com.f0x1d.dmsdk.model;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

@Keep
public class DocumentContent {

    private String mContent;
    private String mSlug;
    @Nullable
    private Boolean mEditable;
    private boolean mIsUrl;

    public static DocumentContent create(String content, String slug, @Nullable Boolean editable, boolean isUrl) {
        DocumentContent documentContent = new DocumentContent();
        documentContent.setContent(content);
        documentContent.setSlug(slug);
        documentContent.setEditable(editable);
        documentContent.setIsUrl(isUrl);
        return documentContent;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        this.mSlug = slug;
    }

    public Boolean getEditable() {
        return mEditable;
    }

    public void setEditable(Boolean editable) {
        this.mEditable = editable;
    }

    public boolean isUrl() {
        return mIsUrl;
    }

    public void setIsUrl(boolean isUrl) {
        this.mIsUrl = isUrl;
    }
}
