# SDK Documentation

Modules in dogbin mobile are a very interesting idea. Module is an android application from which dogbin mobile loads code.

[Here](https://github.com/F0x1d/dogbin-mobile/tree/master/testservice) you can the example of the module.

## Quick start

1. Create android application project in Android Studio
2. Add [DM-SDK](https://f0x1d.com/files/dm-sdk.aar) as dependency to the project
3. Create class and implement ```BinService``` class from SDK
4. In manifest add ```<meta-data android:name="service" android:value="com.your.package.to.your.BinService" />``` in ```<application``` tag

## Methods

BinService must implement list of methods, these methods are called by dogbin mobile.

Methods, which ```throw Exception``` are run **not** on the UI thread.

Methods, which dont't ```throw Exception``` are run on the UI thread (expect caching methods).

### Main

```void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences);``` is called when module is loaded, here you can use module application context, dogbin application context and SharedPreferences where you can store something.

```String getDomain();``` must return service domain. Example: https://f0x1d.com

```String getSlugFromLink(String link);``` must parse link in order to get slug. Example: https://del.dog/test -> test

### Login

```String getUsername() throws Exception;``` must return user's username, called only when user is logged in.

```boolean login(String username, String password) throws Exception;``` must return true if login is successful, false if not. Later i will implement object with error, etc.

```boolean register(String username, String password) throws Exception;``` same as login, but registration.

```boolean loggedIn();``` must return true if user is logged in, false if not.

```void logout();``` is called when user clicked logout button.

### Documents

```String getDocumentText(String slug) throws Exception;``` must return content for slug.

```String createDocument(String slug, String content) throws Exception;``` must create a document with content on slug (may be empty) and return its slug, **not link, just slug**.

```String editDocument(String slug, String content) throws Exception;``` must edit a user's document with content on slug (may be empty) and return its slug, **not link, just slug**.

```boolean isEditableNote(String slug) throws Exception;``` must return true if user can edit note, false if not.

### Cache

You don't need to implement caching if you don't want to do it.

These methods are also called **not** on the UI thread.

```List<CachedNote> getNoteListFromCache();``` must return list of cached notes, when user doesn't have internet connection for example. Please return empty list if there are no notes or you don't want to implement cache.

```String getContentFromCache(String slug);``` must return note's content from cache. Please return null if such note is not cached or you don't want to implement cache.

```void cacheNote(String slug, String content, boolean myNote);``` must cache a note with content.

### Folders

```boolean showFoldersItem();``` must return true if folders item should be showed in bottom navigation, false if not.

```Folder getDefaultFolder();``` must return default folder, which should be showed in bottom navigation near folder item.

```List<Folder> getAvailableFolders() throws Exception;``` must return list of available folders. Please return empty list if there are no such folders.

```List<UserNote> getUserNotesForFolder(String key) throws Exception;``` must return a list of notes for folder as each folder has a key.

### Errors

You can ```throw Exception``` in methods, where you are allowed to do so, then dogbin mobile will display a Toast with such text: ```Error: Exception.getLocalizedMessage()```.


## Viewing text from links

If you want to do so, then add such ```<intent-filter>``` to activity in your manifest.

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

## End

Hope it's all. Thank you.