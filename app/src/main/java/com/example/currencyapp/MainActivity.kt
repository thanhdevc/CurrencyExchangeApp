package com.example.currencyapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencyapp.databinding.ActivityMainBinding




private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private var isEditing = false
    private var isFromEditText1 = true  // Xác định EditText nào đang được nhập

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "VND" to 25000.0,
        "EUR" to 0.85,
        "JPY" to 130.0,
        "GBP" to 0.75
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner1.adapter = adapter
        binding.spinner2.adapter = adapter

        binding.edit1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isFromEditText1 = true
        }

        binding.edit2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isFromEditText1 = false
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                if (isFromEditText1) {
                    convertCurrency(binding.edit1, binding.edit2, binding.spinner1, binding.spinner2)
                } else {
                    convertCurrency(binding.edit2, binding.edit1, binding.spinner2, binding.spinner1)
                }

                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.edit1.addTextChangedListener(textWatcher)
        binding.edit2.addTextChangedListener(textWatcher)

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFromEditText1) {
                    convertCurrency(binding.edit1, binding.edit2, binding.spinner1, binding.spinner2)
                } else {
                    convertCurrency(binding.edit2, binding.edit1, binding.spinner2, binding.spinner1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinner1.onItemSelectedListener = spinnerListener
        binding.spinner2.onItemSelectedListener = spinnerListener
    }

    private fun convertCurrency(fromEditText: EditText, toEditText: EditText, fromSpinner: Spinner, toSpinner: Spinner) {
        val amount = fromEditText.text.toString().toDoubleOrNull()

        if (amount == null || amount == 0.0) {
            toEditText.setText("")
            return
        }

        val fromRate = exchangeRates[fromSpinner.selectedItem.toString()] ?: 1.0
        val toRate = exchangeRates[toSpinner.selectedItem.toString()] ?: 1.0
        val convertedAmount = amount * (toRate / fromRate)

        toEditText.setText(String.format("%.2f", convertedAmount))
    }
}
