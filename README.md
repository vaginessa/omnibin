# omnibin

Android client for different bins like [dogbin](https://del.dog/). Previous dogbin mobile.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=com.f0x1d.dogbin)

## Integration

omnibin supports integration

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Creating intent
    Intent intent = new Intent("com.f0x1d.dogbin.ACTION_UPLOAD_TO_FOXBIN");
    // Text to publish
    intent.putExtra(Intent.EXTRA_TEXT, "vzlom beb 3");
    // Slug to use (link), example: https://some.domain/{slug}
    intent.putExtra("slug", "happynewyear");
    // omnibin won't automatically copy the result link
    intent.putExtra("copy", false);
    intent.setType("text/plain");
    // Start intent
    startActivityForResult(intent, 0);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 0 && resultCode == RESULT_OK) {
        // Text is published and all is ok, let's display the link
        TextView textView = new TextView(this);
        // data.getData().toString() will return result link, example: https://some.domain/vzlom
        textView.setText(data.getData().toString());

        setContentView(textView);
    }
}
```

Also can be used without ```onActivityResult```

## Modules SDK
Docs are available [here](https://github.com/F0x1d/omnibin/blob/master/SDK.md)

## License
omnibin is distributed under the terms of the Apache License (Version 2.0). See [license](/LICENSE) for details.
