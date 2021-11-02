# SDK Documentation

Module is an android application from which omnibin loads code.

## Quick start

1. Create android application project in Android Studio.
2. Add [DM-SDK](https://files.f0x1d.com/files/dm-sdk.aar) as dependency to the project.
3. Create class and implement ```BinService``` from SDK.
4. In manifest add ```<meta-data android:name="binservice" android:value="com.your.package.to.your.BinService" />``` in ```<application``` tag.
5. Android 11 is cool, also add ```<intent-filter>``` with ```<action android:name="com.f0x1d.dogbin.VISIBILITY"/>``` in ```<activity``` tag.

## Methods

BinService must implement list of methods, these methods are called by omnibin.

Methods, which ```throw Exception``` are run **not** on the UI thread.

Methods, which don't ```throw Exception``` are run on the UI thread (except caching methods).
Not all are run straight on the UI thread, idea is not to request smth from net (for example) in them.

### Main

```void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences);``` is called when module is loaded, here you can use module application context, dogbin application context and SharedPreferences where you can store something.

```String getDomain();``` must return service domain. Example: https://f0x1d.com/

```String getSlugFromLink(String link);``` must parse link in order to get slug. Example: https://del.dog/test -> test

### Login

```String getUsername() throws Exception;``` must return user's username, called only when user is logged in.

```void login(String username, String password) throws Exception;``` is called when user clicked log in button.

```void register(String username, String password) throws Exception;``` same as login, but registration.

```boolean loggedIn();``` must return true if user is logged in, false if not.

```void logout();``` is called when user clicked logout button.

### Documents

```DocumentContent getDocumentContent(String slug) throws Exception;``` must return content for slug. Set editable as null if you return it later.

```String createDocument(String slug, String content) throws Exception;``` must create a document with content on slug (may be empty) and return its slug, **not link, just slug**.

```String editDocument(String slug, String content) throws Exception;``` must edit a user's document with content on slug (may be empty) and return its slug, **not link, just slug**.

```Boolean isEditableDocument(String slug) throws Exception;``` must return true if user can edit document, false if not, null if set in ```DocumentContent``` object.

### Cache

You don't need to implement caching if you don't want to do it.

These methods are also called **not** on the UI thread.

```List<UserDocument> getDocumentListFromCache();``` must return list of cached documents, when user doesn't have internet connection for example. Please return empty list if there are no documents or you don't want to implement cache.

```DocumentContent getContentFromCache(String slug);``` must return document's content from cache. Please return null if such document is not cached or you don't want to implement cache.

```void cacheDocument(String slug, String content, boolean myDocument);``` cache a document with content.

### Folders

```boolean showFoldersItem();``` must return true if folders item should be showed in bottom navigation, false if not.

```Folder getDefaultFolder();``` must return default folder, which should be showed in bottom navigation near settings item.

```List<Folder> getAvailableFolders() throws Exception;``` must return list of available folders. Please return empty list if there are no such folders.

```List<UserDocument> getUserDocumentsForFolder(String key) throws Exception;``` must return a list of documents for folder as each folder has a key.

### Errors

You can ```throw Exception``` in methods, where you are allowed to do so, then omnibin will display a Toast with such text: ```Error: Exception.getLocalizedMessage()```.


## Viewing text from links

If you want to do so, then add your ```<intent-filter>``` to activity in your manifest.

And add something like this in this activity:
```
    if (getIntent().getData() != null) {
        Intent intent = new Intent("com.f0x1d.dogbin.ACTION_TEXT_VIEW");
        intent.setType("text/plain");
        intent.putExtra("module_package_name", getPackageName());
        intent.putExtra("url", getIntent().getData().toString());

        startActivity(intent);
        finish();
    }
```

## Proguard

If you're using proguard, please use ```@Keep``` annotation in your ```BinService``` class.

## End

Hope it's all. Thank you.
