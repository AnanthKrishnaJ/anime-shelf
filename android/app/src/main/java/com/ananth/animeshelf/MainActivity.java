package com.ananth.animeshelf;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;

/**
 * Anime Shelf for Android: a full-screen window onto the live site, so the app always
 * shows the latest version and syncs with AniList exactly like the website does.
 * The page talks to the app through window.ShelfApp to save and share files.
 */
public class MainActivity extends Activity {
    static final String HOST = "anime-shelf-seven.vercel.app";
    static final String HOME = "https://" + HOST + "/";

    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        web.setBackgroundColor(night() ? Color.rgb(11, 11, 11) : Color.rgb(238, 238, 238));
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportMultipleWindows(false);
        s.setUserAgentString(s.getUserAgentString() + " AnimeShelfApp/" + BuildConfig.VERSION_NAME);
        CookieManager.getInstance().setAcceptCookie(true);

        web.addJavascriptInterface(new Bridge(), "ShelfApp");
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if (HOST.equals(u.getHost()) || "about".equals(u.getScheme()) || "data".equals(u.getScheme())) return false;
                openExternal(u);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame()) showOffline();
            }
        });

        if (state != null && web.restoreState(state) != null) return;
        web.loadUrl(startUrl(getIntent()));
    }

    private String startUrl(Intent i) {
        Uri d = i == null ? null : i.getData();
        return d != null && HOST.equals(d.getHost()) ? d.toString() : HOME;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) web.loadUrl(startUrl(intent));
    }

    private boolean night() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    private void openExternal(Uri u) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, u));
        } catch (ActivityNotFoundException e) {
            toast("No app can open that link");
        }
    }

    private void showOffline() {
        boolean n = night();
        String bg = n ? "#0b0b0b" : "#eeeeee", ink = n ? "#f2f2f2" : "#111111", btnBg = n ? "#f2f2f2" : "#111111", btnInk = n ? "#111111" : "#ffffff";
        String html = "<!doctype html><meta name=viewport content='width=device-width,initial-scale=1'>"
                + "<body style='margin:0;min-height:100vh;display:grid;place-items:center;background:" + bg + ";color:" + ink + ";font-family:sans-serif;text-align:center'>"
                + "<div style='padding:24px'><div style='font:700 44px serif'>A</div><h2 style='font-family:serif'>You're offline</h2>"
                + "<p style='opacity:.65'>Anime Shelf needs the internet to reach AniList.</p>"
                + "<a href='" + HOME + "' style='display:inline-block;margin-top:10px;padding:12px 22px;border-radius:10px;background:" + btnBg + ";color:" + btnInk + ";text-decoration:none;font-weight:600'>Try again</a></div></body>";
        web.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        web.evaluateJavascript("(window.appBack&&window.appBack())?1:0", v -> {
            if ("1".equals(v)) return;
            if (web.canGoBack()) web.goBack();
            else finish();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onPause() {
        web.onPause();
        CookieManager.getInstance().flush();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }

    private void toast(String m) {
        runOnUiThread(() -> Toast.makeText(this, m, Toast.LENGTH_LONG).show());
    }

    private static String clean(String name) {
        String n = name == null ? "" : name.replaceAll("[\\\\/:*?\"<>|\\n\\r]", "_").trim();
        return n.isEmpty() ? "anime-shelf" : n;
    }

    /** Writes a file to Downloads/Anime Shelf and returns its content URI. */
    private Uri writeDownload(String name, String b64, String mime) throws Exception {
        byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, clean(name));
        cv.put(MediaStore.MediaColumns.MIME_TYPE, mime == null || mime.isEmpty() ? "application/octet-stream" : mime);
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Anime Shelf");
        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
        if (uri == null) throw new IllegalStateException("storage unavailable");
        try (OutputStream o = getContentResolver().openOutputStream(uri)) {
            if (o == null) throw new IllegalStateException("storage unavailable");
            o.write(bytes);
        }
        return uri;
    }

    /** Called from the page as window.ShelfApp. */
    private class Bridge {
        @JavascriptInterface
        public void save(String name, String b64, String mime) {
            try {
                Uri uri = writeDownload(name, b64, mime);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Saved to Downloads/Anime Shelf", Toast.LENGTH_LONG).show();
                    Intent view = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(view, "Open " + clean(name)));
                    } catch (Exception ignored) {
                    }
                });
            } catch (Exception e) {
                toast("Couldn't save the file: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void shareText(String title, String text) {
            Intent i = new Intent(Intent.ACTION_SEND).setType("text/plain");
            if (title != null && !title.isEmpty()) i.putExtra(Intent.EXTRA_SUBJECT, title);
            i.putExtra(Intent.EXTRA_TEXT, text);
            runOnUiThread(() -> startActivity(Intent.createChooser(i, "Share")));
        }

        @JavascriptInterface
        public void shareFile(String name, String b64, String mime, String title) {
            try {
                Uri uri = writeDownload(name, b64, mime);
                Intent i = new Intent(Intent.ACTION_SEND).setType(mime).putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (title != null && !title.isEmpty()) i.putExtra(Intent.EXTRA_SUBJECT, title);
                runOnUiThread(() -> startActivity(Intent.createChooser(i, "Share")));
            } catch (Exception e) {
                toast("Couldn't share the file: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public String version() {
            return BuildConfig.VERSION_NAME;
        }
    }
}
