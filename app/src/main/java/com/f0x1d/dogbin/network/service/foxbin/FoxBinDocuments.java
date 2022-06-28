package com.f0x1d.dogbin.network.service.foxbin;

import android.os.Bundle;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreateDocumentRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreateDocumentWithSlugRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreatedDocumentResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinErrorResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNoteResponse;
import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.foxbin.FoxBinApi;
import retrofit2.Response;

public class FoxBinDocuments extends DocumentsModule {

    public FoxBinDocuments(BinService binService) {
        super(binService);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        Response<FoxBinGetNoteResponse> response = FoxBinApi.getInstance().getService().getNote(slug, App.getPreferencesUtil().getFoxBinToken()).execute();
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);

        FoxBinGetNoteResponse.FoxBinNote foxBinNote = response.body().getFoxBinNote();
        return DocumentContent.create(foxBinNote.getContent(), foxBinNote.getSlug(), foxBinNote.getEditable(), false /* Unsupported */);
    }

    @Override
    public String createDocument(String slug, String content, Bundle settings) throws Exception {
        FoxBinCreateDocumentRequest foxBinCreateDocumentRequest = slug.isEmpty() ? new FoxBinCreateDocumentRequest() : new FoxBinCreateDocumentWithSlugRequest();
        foxBinCreateDocumentRequest.setContent(content);
        foxBinCreateDocumentRequest.setAccessToken(App.getPreferencesUtil().getFoxBinToken());
        foxBinCreateDocumentRequest.setDeleteAfter(settings.getLong("delete_after"));
        if (!slug.isEmpty()) ((FoxBinCreateDocumentWithSlugRequest) foxBinCreateDocumentRequest).setSlug(slug);

        Response<FoxBinCreatedDocumentResponse> response =
                FoxBinApi.getInstance().getService().createDocument(NetworkUtils.createBody(foxBinCreateDocumentRequest)).execute();
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);
        return response.body().getSlug();
    }

    @Override
    public String editDocument(String slug, String content, Bundle settings) throws Exception {
        FoxBinCreateDocumentWithSlugRequest foxBinCreateDocumentRequest = new FoxBinCreateDocumentWithSlugRequest();
        foxBinCreateDocumentRequest.setContent(content);
        foxBinCreateDocumentRequest.setAccessToken(App.getPreferencesUtil().getFoxBinToken());
        foxBinCreateDocumentRequest.setSlug(slug);

        Response<FoxBinCreatedDocumentResponse> response =
                FoxBinApi.getInstance().getService().editDocument(NetworkUtils.createBody(foxBinCreateDocumentRequest)).execute();
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);
        return response.body().getSlug();
    }

    @Override
    public boolean deleteDocument(String slug) throws Exception {
        Response<BaseFoxBinResponse> response = FoxBinApi.getInstance().getService().deleteNote(slug, App.getPreferencesUtil().getFoxBinToken()).execute();
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);
        return response.body().isOk();
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }
}
