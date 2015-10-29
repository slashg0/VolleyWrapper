package com.slashg.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.slashg.volleywrapper.EasyRequest;
import com.slashg.volleywrapper.JSONParamRunnable;
import com.slashg.volleywrapper.Spine;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Context thisContext = this;
        String url1 = "http://www.350gc.com/demophps/demo1.php";
        String url2 = "http://www.350gc.com/demophps/demo2.php";
        String url3 = "http://www.350gc.com/demophps/demo3.php";
        HashMap<String,String> a = new HashMap<String,String>(1);
        a.put("abc", "xyz");
        Spine.queueRequest(
                new EasyRequest(
                        Request.Method.GET,
                        url1,
                        a,
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        },
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onErrorResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        }
                )
        );
        Spine.queueRequest(
                new EasyRequest(
                        Request.Method.GET,
                        url2,
                        a,
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        },
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onErrorResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        }
                )
        );
        Spine.queueRequest(
                new EasyRequest(
                        Request.Method.GET,
                        url3,
                        a,
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        },
                        new JSONParamRunnable() {
                            @Override
                            public void run(JSONObject param) {
                                Toast.makeText(thisContext, "ApplicationExt::onErrorResponse() " + param, Toast.LENGTH_LONG).show();
                            }
                        }
                )
        );
    }
}
