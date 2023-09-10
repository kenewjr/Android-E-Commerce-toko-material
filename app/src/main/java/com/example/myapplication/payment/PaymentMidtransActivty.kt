package com.example.myapplication.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class PaymentMidtransActivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans_activty)
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-UyV8fwVUJHmHywYZ")
            .setContext(applicationContext)
            .setTransactionFinishedCallback({
                    result ->

            })
            .setMerchantBaseUrl("http://192.168.1.150/skripsi/midtrans")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

    }
}