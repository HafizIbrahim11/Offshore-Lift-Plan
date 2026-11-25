package com.hafiz.offshoreliftplan;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.webkit.WebViewAssetLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
  private static final String HOST = "appassets.androidplatform.net";
  private static final int PICK = 1;
  protected WebView web;
  private ValueCallback<Uri[]> fileCb;

  protected String page() { return "index.html"; }

  @Override protected void onCreate(Bundle saved) {
    super.onCreate(saved);
    web = new WebView(this);
    setContentView(web);
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setDatabaseEnabled(true);
    s.setAllowFileAccess(false);
    final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
    web.setWebViewClient(new WebViewClient() {
      @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
        return loader.shouldInterceptRequest(r.getUrl());
      }
      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
        Uri u = r.getUrl();
        if (HOST.equals(u.getHost())) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception e) { }
        return true;
      }
    });
    web.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
        if (fileCb != null) fileCb.onReceiveValue(null);
        fileCb = cb;
        try { startActivityForResult(p.createIntent(), PICK); }
        catch (Exception e) { fileCb = null; return false; }
        return true;
      }
    });
    web.addJavascriptInterface(new Bridge(), "Android");
    if (saved != null) web.restoreState(saved);
    else web.loadUrl("https://" + HOST + "/assets/" + page());
  }

  @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); web.saveState(out); }

  @Override protected void onActivityResult(int req, int res, Intent data) {
    super.onActivityResult(req, res, data);
    if (req == PICK && fileCb != null) {
      fileCb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data));
      fileCb = null;
    }
  }

  @Override public void onBackPressed() {
    if (web.canGoBack()) web.goBack(); else super.onBackPressed();
  }

  private static String mime(String name) {
    String n = name.toLowerCase();
    if (n.endsWith(".pdf")) return "application/pdf";
    if (n.endsWith(".png")) return "image/png";
    if (n.endsWith(".jpg") || n.endsWith(".jpeg")) return "image/jpeg";
    if (n.endsWith(".json")) return "application/json";
    return "application/octet-stream";
  }

  private class Bridge {
    @JavascriptInterface public void print() {
      runOnUiThread(() -> {
        PrintManager pm = (PrintManager) getSystemService(PRINT_SERVICE);
        String job = getTitle() + " document";
        pm.print(job, web.createPrintDocumentAdapter(job),
            new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build());
      });
    }

    @JavascriptInterface public void saveFile(final String name, final String b64) {
      runOnUiThread(() -> {
        try {
          byte[] data = Base64.decode(b64, Base64.DEFAULT);
          String where = "";
          if (Build.VERSION.SDK_INT >= 29) {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            cv.put(MediaStore.MediaColumns.MIME_TYPE, mime(name));
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri u = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
            if (u != null) {
              try (OutputStream o = getContentResolver().openOutputStream(u)) { o.write(data); }
              where = "Saved to Downloads. ";
            }
          }
          File dir = new File(getCacheDir(), "share");
          dir.mkdirs();
          File f = new File(dir, name);
          try (FileOutputStream o = new FileOutputStream(f)) { o.write(data); }
          Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".files", f);
          Intent i = new Intent(Intent.ACTION_SEND);
          i.setType(mime(name));
          i.putExtra(Intent.EXTRA_STREAM, uri);
          i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          Toast.makeText(MainActivity.this, where + "Choose where to share it.", Toast.LENGTH_SHORT).show();
          startActivity(Intent.createChooser(i, "Share " + name));
        } catch (Exception e) {
          Toast.makeText(MainActivity.this, "Could not save the file.", Toast.LENGTH_LONG).show();
        }
      });
    }
  }
}
