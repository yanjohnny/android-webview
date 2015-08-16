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
		//����webView��js���ýӿ�
		webView.addJavascriptInterface(new JavascriptExplore(), "explore");
		//����webSettings
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
				//���� path�µ��ļ��ṹ
				File file=new File(path);
				File[] files=file.listFiles();
				//��files��������
				sort(files);
				//׼����װjsonArray
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
			   .setTitle("��½")
			   .setView(v)
			   .setPositiveButton("�޸�", new OnClickListener() {
				
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
			   .setNegativeButton("ȡ��", null)
			   .create().show();
			return 1;
		}

	}
	/**
	 * �ļ�����ǰ  �ļ��ں�
	 * �ļ��а���ĸA-Z��������
	 * �ļ�����ĸA-Z��������
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
				//��������ļ� �� �����ļ���ʱ
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
		//��webView�ص���һĿ¼
		webView.loadUrl("javascript:goBack()");
		
	}
}
