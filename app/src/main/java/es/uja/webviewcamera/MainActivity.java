package es.uja.webviewcamera;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    static Uri picUri = null;
    String url;
    String uriInString;
    boolean isFirstLoad = true; // for the first time there is no image.Used this not to show image in the first page load.
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.main_webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url1) { // This is the important part. Everytime camera intent finish it calls onCreate method.
                super.onPageFinished(view, url1);          // So after getting the camera image javascript run to set image in WebView
                if(!isFirstLoad)
                    webView.loadUrl("javascript:getImage('" + url + "')");
            }
        });
        webView.addJavascriptInterface(this, "Android"); // javascript interfaces are in 'this' activity
        // if javascript interfaces are added to another class we can't call 'startActivityForResult'
        webView.loadUrl("file:///android_asset/www/help.html");;
        // here I published my html file in google drive.this link should work till 2016 may.(google is going to stop this service in 2016)
        // I believe dropbox is also good to host a simple html page if you don't have a server
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
    }
    @JavascriptInterface
    public void showAndroidCamera() { // This is called when click the button in html page.
        // Create AndroidExampleFolder in storage. You can see this folder by opening Android Device Monitor. See for 'mnt' folder in DDMS --> File Explorer view.
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs(); // Create AndroidExampleFolder if not exists
        }
        // Create camera captured image file path and name
        File file = new File(imageStorageDir + File.separator + "IMG"+ String.valueOf(System.currentTimeMillis())+ ".jpg");
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE"); // crating the camera intent
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        picUri = Uri.fromFile(file);
        startActivityForResult(cameraIntent, 123); // result is passed to onActivityResult
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isFirstLoad = false;
        uriInString = picUri.toString().replace("file://", ""); //needs to remove 'file://' part in uri path.
        switch (requestCode) {
            case 123:
                if (resultCode == AppCompatActivity.RESULT_OK) {
//                    Bitmap bm = BitmapFactory.decodeFile(uriInString);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
//                    byte[] byteArrayImage = baos.toByteArray();
//                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
//                    url="data:image/png;base64,"+encodedImage; // encoded value passed to javascript
                    //webView.loadUrl(picUri.toString());
                    webView.loadUrl("javascript:getImage('" + picUri.toString() + "')");
                }
        }
    }


}


