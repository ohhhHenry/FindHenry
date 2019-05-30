package com.lighthouselabs.findhenry.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polidea.rxandroidble2.scan.ScanResult

class ScanResultsAdapter : RecyclerView.Adapter<ScanResultViewHolder>() {

    private val data = mutableListOf<ScanResult>()

    fun addScanResult(bleScanResult: ScanResult) {
        // Not the best way to ensure distinct devices, just for the sake of the demo.
        data.withIndex()
            .firstOrNull { it.value.bleDevice == bleScanResult.bleDevice }
            ?.let {
                // device already in data list => update
                data[it.index] = bleScanResult
                notifyItemChanged(it.index)
            }
            ?: run {
                // new device => add to data list
                with(data) {
                    add(bleScanResult)
                    distinct()
                    sortBy { it.bleDevice.macAddress }
                }
                notifyDataSetChanged()
            }
    }

    fun clearScanResults() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ScanResultViewHolder, position: Int) {
        with(data[position]) {
            holder.device.text = String.format("${bleDevice.macAddress} ${bleDevice.name}")
            holder.rssi.text = String.format("RSSI: $rssi")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultViewHolder {
        Log.i("ScanResultsAdapter", "onCreateViewHolder() called!")
        return LayoutInflater.from(parent.context)
            .inflate(android.R.layout.two_line_list_item, parent, false)
            .let { ScanResultViewHolder(it) }
    }
}