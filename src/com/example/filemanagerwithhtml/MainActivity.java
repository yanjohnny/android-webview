package com.example.filemanagerwithhtml;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class MainActivity extends Activity {
	private WebView webView;
	View v;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView=(WebView)findViewById(R.id.webView);
		//设置webView的js调用接口
		webView.addJavascriptInterface(new JavascriptExplore(), "explore");
		//设置webSettings
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.loadUrl("file:///android_asset/www/index.html");
	}
	
	class JavascriptExplore{
		@JavascriptInterface
		public void finishActivity(){
			finish();
		}
		
		@JavascriptInterface
		public String loadFiles(String path){
			Log.i("info", "path:"+path);
			try {
				//返回 path下的文件结构
				File file=new File(path);
				File[] files=file.listFiles();
				//对files进行排序
				sort(files);
				//准备封装jsonArray
				JSONArray ary=new JSONArray();
				//[ {"isDir":true, "text":"xxx"},{},{}]
				for(int i=0; i<files.length; i++){
					File f=files[i];
					JSONObject obj=new JSONObject();
					obj.put("isDir", f.isDirectory());
					obj.put("text", f.getName());
					ary.put(obj);
				}
				Log.i("info", ary.toString());
				return ary.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "[]";
		}
		@JavascriptInterface
		public void deleteFile(String path){
			File f = new File(path);
			f.delete();
		}
		
		@JavascriptInterface
		public int rename(final String path){
			v= View.inflate(MainActivity.this, 
					   R.layout.rename_view, null);
			 new AlertDialog.Builder(MainActivity.this)
			   .setIcon(android.R.drawable.btn_star)
			   .setTitle("登陆")
			   .setView(v)
			   .setPositiveButton("修改", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText et = (EditText) v.findViewById(R.id.editText1);
					String a = et.getText().toString();
					File file = new File(path);
					String newPath = path.substring(0,path.lastIndexOf('/'))+"/"+a;
					file.renameTo(new File(newPath));
				}
			})
			   .setNegativeButton("取消", null)
			   .create().show();
			return 1;
		}

	}
	/**
	 * 文件夹在前  文件在后
	 * 文件夹按字母A-Z进行排序
	 * 文件按字母A-Z进行排序
	 * @param files
	 */
	private void sort(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				if(f1.isDirectory() && !f2.isDirectory()){
					return -1;
				}
				if(!f1.isDirectory() && f2.isDirectory()){
					return 1;
				}
				//如果都是文件 或 都是文件夹时
				return f1.getName().compareToIgnoreCase(f2.getName());
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		//让webView回到上一目录
		webView.loadUrl("javascript:goBack()");
		
	}
}
