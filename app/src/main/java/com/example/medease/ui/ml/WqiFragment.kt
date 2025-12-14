package com.example.medease.ui.ml

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.medease.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medease.data.model.ml.WqiRequest
import com.example.medease.database.viewModels.WqiViewModel

class WqiFragment : Fragment(R.layout.fragment_wqi) {
    private lateinit var viewModel: WqiViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WqiViewModel::class.java]

        val btnPredict = view.findViewById<Button>(R.id.btnPredict)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)

        btnPredict.setOnClickListener {

            val request = WqiRequest(
                ammonia = 0.5,
                bod = 2.1,
                `do` = 7.8,
                orthophosphate = 0.03,
                ph = 7.2,
                temperature = 28.0,
                nitrogen = 1.2,
                nitrate = 0.4
            )

            viewModel.predictWqi(request)
        }

        viewModel.result.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                tvResult.text = """
                    Prediction : ${response.prediction}
                """.trimIndent()
            } else {
                tvResult.text = "Gagal mendapatkan prediksi"
            }
        }
    }
}
