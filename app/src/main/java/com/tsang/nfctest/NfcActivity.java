package com.tsang.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.math.BigInteger;


public class NfcActivity extends Activity {

    TextView textMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        initData();
        textMain = findViewById(R.id.textMain);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (null == adapter) {
            Toast.makeText(this, "不支持NFC功能", Toast.LENGTH_SHORT).show();
        } else if (!adapter.isEnabled()) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            // 根据包名打开对应的设置界面
            startActivity(intent);
        }

        //我项目中是拿了NFC卡的tag中的id数据，这根据具体情况来；
        // 可以在NfcAdapter源码中查看，具体能拿到哪些数据
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = bytesToHex(tag.getId());
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();

        Log.d("DEBUG",id);

        //TODO 目前我这边项目中，拿到数据后，通过EventBus分发到对应的activity，当然也能使用其他分发响应方式，

        //关闭动画，毕竟对用户来说，刷卡应当是一个无感知的过程
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    /**
     *  2转10
     * @param src
     * @return
     */
    private static String bytesToTenNum(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[1] = Character.toUpperCase(Character.forDigit(
                    (src[i] >>> 4) & 0x0F, 16));
            buffer[0] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
                    16));
            stringBuilder.append(buffer);
        }
        stringBuilder.reverse();
        BigInteger bigi = new BigInteger(stringBuilder.toString(), 16);
        return bigi.toString();
    }

    /**
     * 2转16
     * @param src
     * @return
     */
    private static String bytesToHex(byte[] src){
        StringBuffer sb = new StringBuffer();
        if (src == null || src.length <= 0) {
            return null;
        }
        String sTemp;
        for (int i = 0; i < src.length; i++) {
            sTemp = Integer.toHexString(0xFF & src[i]);
            if (sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
}