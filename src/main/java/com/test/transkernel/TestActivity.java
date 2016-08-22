package com.test.transkernel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nationz.nk_bluetooth.R;

/**
 * Created by LEO on 2016/8/22.
 */
public class TestActivity extends Activity {

    static {
        System.loadLibrary("transkernel");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

    }

    public void OnClick(View view) {
        transkernel kernel = transkernel.getInstance(TestActivity.this);
        kernel.TransInitEnv();
        int index = kernel.TransInitTransaction(1, "160822113600", 0);
        Log.i("Test","TransInitTransaction: "+index);
        if (index == 2) {
            kernel.TransStartTransaction(0, 0, "000000000100", "");
        }
        kernel.TransProcessTransaction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transkernel.getInstance(TestActivity.this).clear();
    }
}
