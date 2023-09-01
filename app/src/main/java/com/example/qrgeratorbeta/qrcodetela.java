package com.example.qrgeratorbeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class qrcodetela extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Button ButtonImprimir;
    private ImageButton BotaoDeAjuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcodetela);

        ButtonImprimir = findViewById(R.id.Button_Imprimir);
        qrCodeImageView = findViewById(R.id.qrCode);
        BotaoDeAjuda = findViewById(R.id.botaoDeAjuda);

        byte[] byteArray = getIntent().getByteArrayExtra("qrcode.bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        qrCodeImageView.setImageBitmap(bitmap);

        BotaoDeAjuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AjudaTela.class);
                startActivity(intent);
            }
        });

        ButtonImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintHelper printHelper = new PrintHelper(qrcodetela.this);
                printHelper.setScaleMode(printHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("qrcode.bitmap", bitmap);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String qrContent = result.getContents(); // Conteúdo do QR Code lido

            if (qrContent != null && !qrContent.isEmpty()) {
                // Gerar o arquivo PDF com as informações do QR Code
                qrcodetela.generatePdf(qrContent);
            }
        }
    }

    private static void generatePdf(String qrContent) {
        PdfDocument document = new PdfDocument();

        // Criação de uma página vazia
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(500, 500, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Desenho das informações do QR Code na página
        // Exemplo: Desenhe o qrContent no centro da página
        int x = 250;
        int y = 250;
        page.getCanvas().drawText(qrContent, x, y, new Paint());

        document.finishPage(page);

        // Salvar o arquivo PDF
        File directory = new File(Environment.getExternalStorageDirectory(), "PDFs");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "qrcode.pdf";
        File file = new File(directory, fileName);
        try {
            document.writeTo(new FileOutputStream(file));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}