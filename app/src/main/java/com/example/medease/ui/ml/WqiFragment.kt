package com.example.medease.ui.ml

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
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

        val sbAmmonia = view.findViewById<SeekBar>(R.id.sbAmmonia)
        val sbBod = view.findViewById<SeekBar>(R.id.sbBod)
        val sbDo = view.findViewById<SeekBar>(R.id.sbDo)
        val sbPh = view.findViewById<SeekBar>(R.id.sbPh)
        val sbTemp = view.findViewById<SeekBar>(R.id.sbTemp)

        val tvAmmonia = view.findViewById<TextView>(R.id.tvAmmoniaValue)
        val tvBod = view.findViewById<TextView>(R.id.tvBodValue)
        val tvDo = view.findViewById<TextView>(R.id.tvDoValue)
        val tvPh = view.findViewById<TextView>(R.id.tvPhValue)
        val tvTemp = view.findViewById<TextView>(R.id.tvTempValue)

        fun SeekBar.bind(
            scale: Double,
            textView: TextView
        ) {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = progress / scale
                    textView.text = value.toString()
                }

                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }

        sbAmmonia.bind(100.0, tvAmmonia)   // 0.00 – 1.00
        sbBod.bind(10.0, tvBod)            // 0 – 10
        sbDo.bind(10.0, tvDo)              // 0 – 10
        sbPh.bind(10.0, tvPh)              // 0 – 14
        sbTemp.bind(10.0, tvTemp)          // 0 – 50

        val btnPredict = view.findViewById<Button>(R.id.btnPredict)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressLoading)

        btnPredict.setOnClickListener {

            val request = WqiRequest(
                ammonia = sbAmmonia.progress / 100.0,
                bod = sbBod.progress / 10.0,
                `do` = sbDo.progress / 10.0,
                orthophosphate = 0.03, // bisa ditambah slider juga
                ph = sbPh.progress / 10.0,
                temperature = sbTemp.progress / 10.0,
                nitrogen = 1.2,
                nitrate = 0.4
            )

            viewModel.predictWqi(request)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnPredict.isEnabled = !isLoading
        }

        viewModel.result.observe(viewLifecycleOwner) { res ->
            if (res != null) {
                tvResult.text = "Prediction: ${res.prediction}"

                val color = when (res.prediction) {
                    "Excellent" -> Color.parseColor("#2E7D32") // hijau tua
                    "Good" -> Color.parseColor("#388E3C")
                    "Fair" -> Color.parseColor("#F9A825")
                    "Marginal" -> Color.parseColor("#D32F2F")
                    else -> Color.BLACK
                }
                tvResult.setTextColor(color)
            }
        }





    }
}

