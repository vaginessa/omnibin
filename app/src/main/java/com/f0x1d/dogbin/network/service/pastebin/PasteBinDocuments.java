package com.f0x1d.dogbin.network.service.pastebin;

import android.os.Bundle;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.BuildConfig;
import com.f0x1d.dogbin.network.retrofit.pastebin.PasteBinApi;
import okhttp3.MultipartBody;

import static com.f0x1d.dogbin.network.service.pastebin.PasteBinService.isResponseOk;

public class PasteBinDocuments extends DocumentsModule {

    public PasteBinDocuments(BinService binService) {
        super(binService);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("api_dev_key", BuildConfig.PASTEBIN_API_KEY);
        if (App.getPreferencesUtil().getPastebinToken() != null)
            builder.addFormDataPart("api_user_key", App.getPreferencesUtil().getPastebinToken());
        builder.addFormDataPart("api_paste_key", slug);
        builder.addFormDataPart("api_option", "show_paste");

        String text = PasteBinApi.getInstance().getService().getText(builder.build()).execute().body();

        if (!isResponseOk(text)) {
            text = PasteBinApi.getInstance().getRawService().getContent(slug).execute().body();

            if (!isResponseOk(text))
                throw new Exception(text);
        }

        return DocumentContent.create(text, slug, false, false);
    }

    @Override
    public String createDocument(String slug, String content, Bundle settings) throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("api_dev_key", BuildConfig.PASTEBIN_API_KEY);
        if (App.getPreferencesUtil().getPastebinToken() != null)
            builder.addFormDataPart("api_user_key", App.getPreferencesUtil().getPastebinToken());
        builder.addFormDataPart("api_paste_code", content);
        if (!slug.isEmpty())
            builder.addFormDataPart("api_paste_name", slug);
        builder.addFormDataPart("api_paste_expire_date", settings.getString("expiration"));
        builder.addFormDataPart("api_option", "paste");

        String url = PasteBinApi.getInstance().getService().paste(builder.build()).execute().body();

        if (!isResponseOk(url))
            throw new Exception(url);

        return url.replace("https://pastebin.com/", "");
    }

    @Override
    public String editDocument(String slug, String content, Bundle settings) throws Exception {
        return null;
    }

    @Override
    public boolean deleteDocument(String slug) throws Exception {
        return false;
    }

    @Override
    public boolean canDelete(UserDocument userDocument) {
        return false;
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }
}
