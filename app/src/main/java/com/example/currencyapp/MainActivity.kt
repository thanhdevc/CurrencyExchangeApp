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
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "VND" to 25000.0,
        "EUR" to 0.85,
        "JPY" to 130.0,
        "GBP" to 0.75
    )

    private var isEditing = false  // Biến tránh vòng lặp vô hạn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner1.adapter = adapter
        binding.spinner2.adapter = adapter

        val textWatcherFrom = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                convertCurrency(binding.edit1, binding.edit2, binding.spinner1, binding.spinner2)
                isEditing = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        val textWatcherTo = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                convertCurrency(binding.edit2, binding.edit1, binding.spinner2, binding.spinner1)
                isEditing = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.edit1.addTextChangedListener(textWatcherFrom)
        binding.edit2.addTextChangedListener(textWatcherTo)

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(binding.edit1, binding.edit2, binding.spinner1, binding.spinner2)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(binding.edit2, binding.edit1, binding.spinner2, binding.spinner1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun convertCurrency(fromEditText: EditText, toEditText: EditText, fromSpinner: Spinner, toSpinner: Spinner) {
        val amount = fromEditText.text.toString().toDoubleOrNull() ?: return
        val fromRate = exchangeRates[fromSpinner.selectedItem.toString()] ?: 1.0
        val toRate = exchangeRates[toSpinner.selectedItem.toString()] ?: 1.0
        val convertedAmount = amount * (toRate / fromRate)
        toEditText.setText(String.format("%.2f", convertedAmount))
    }
}
