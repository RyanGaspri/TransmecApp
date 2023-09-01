package com.example.qrgeratorbeta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText editInfo1, editInfo2, editInfo3, editInfo4, editInfo5, editInfo6;
    private Button generateButton;
    private Button BotaoEscanear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editInfo1 = findViewById(R.id.EditInfo);
        editInfo2 = findViewById(R.id.EditInfo2);
        editInfo3 = findViewById(R.id.EditInfo3);
        editInfo4 = findViewById(R.id.EditInfo4);
        editInfo5 = findViewById(R.id.EditInfo5);
        editInfo6 = findViewById(R.id.EditInfo6);

        generateButton = findViewById(R.id.BotaoGerar);
        BotaoEscanear = findViewById(R.id.BotaoEscanear);

        BotaoEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setOrientationLocked(false); // Permitir rotação do dispositivo durante a leitura
                integrator.initiateScan();
            }
        });


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info1 = editInfo1.getText().toString();
                String info2 = editInfo2.getText().toString();
                String info3 = editInfo3.getText().toString();
                String info4 = editInfo4.getText().toString();
                String info5 = editInfo5.getText().toString();
                String info6 = editInfo6.getText().toString();

                String combinedInfo = info1 + "\n" + info2 + "\n" + info3 + "\n" + info4 + "\n" + info5 + "\n" + info6;
                String qrContent = info1 + "\n" + info2 + "\n" + info3 + "\n" + info4 + "\n" + info5 + "\n" + info6;


                if (combinedInfo.trim().isEmpty()) {
                    // Não gerar QR Code se não houver informações
                    return;
                }

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = qrCodeWriter.encode(combinedInfo, BarcodeFormat.QR_CODE, 500, 500);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.white));
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, qrcodetela.class);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("qrcode.bitmap", byteArray);
                    intent.putExtra("qrcode.content", qrContent);
                    startActivity(intent);


                } catch (WriterException e) {
                    Log.e("QRCode", "Erro ao gerar QR Code: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("QRCode", "Erro inesperado: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("QRCode", "Escaneamento cancelado");
                } else {
                    String scannedData = result.getContents();
                    Log.d("QRCode", "Dados escaneados: " + scannedData);
                }
            }
        }
    }
}
