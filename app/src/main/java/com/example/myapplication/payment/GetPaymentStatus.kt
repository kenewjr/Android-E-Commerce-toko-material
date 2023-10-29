package com.example.myapplication.payment

data class GetPaymentStatus(
    val currency: String,
    val expiry_time: String,
    val fraud_status: String,
    val gross_amount: String,
    val merchant_id: String,
    val metadata: Metadata,
    val order_id: String,
    val store : String,
    val payment_amounts: List<Any>,
    val payment_type: String,
    val payment_code: String,
    val settlement_time: String,
    val signature_key: String,
    val status_code: String,
    val status_message: String,
    val transaction_id: String,
    val transaction_status: String,
    val transaction_time: String,
    val va_numbers: List<VaNumber>
):java.io.Serializable