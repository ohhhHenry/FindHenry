package com.lighthouselabs.findhenry.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lighthouselabs.findhenry.R
import com.lighthouselabs.findhenry.ext.showError
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.koin.android.ext.android.inject

class MainFragment : Fragment() {

    private val rxBleClient: RxBleClient by inject()
    private val scanResultsAdapter: ScanResultsAdapter by lazy { ScanResultsAdapter() }
    private val isScanning: Boolean
        get() = scanDisposable != null

    private var scanDisposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        // Stop scanning in onPause callback.
        if (isScanning) scanDisposable?.dispose()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            scan_toggle_button.setOnClickListener {
                Log.i("MainFragment", "onScanToggleClicked()!")
                onScanToggleClicked()
            }

            clear_list_button.setOnClickListener {
                Log.i("MainFragment", "onClearScanResultsClicked()!")
                scanResultsAdapter.clearScanResults()
            }
        }
    }


    private fun configureResultList() {
        with(scan_results_recyclerview) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = scanResultsAdapter
        }
    }

    private fun onScanToggleClicked() {
        configureResultList()
        if (isScanning) {
            Log.i("MainFragment.Scan", "Already scanning! Disposing disposable...")
            scanDisposable?.dispose()
        } else {
            getBleScanningObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { dispose("BleScanningObservable") }
                .subscribe(
                    {
                        Log.i(
                            "MainFragment.Scan",
                            "Adding ScanResult: Device Name: ${it.bleDevice.name}, MAC: ${it.bleDevice.macAddress}"
                        )
                        scanResultsAdapter.addScanResult(it)
                    },
                    { onScanFailure(it) })
                .let { scanDisposable = it }
        }

        updateButtonUIState()
    }

    private fun getBleScanningObservable(): Observable<ScanResult> {
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val scanFilter = ScanFilter.Builder()
//            .setDeviceAddress("04:52:C7:EF:9D:C0")
            // add custom filters if needed
            .build()

        return rxBleClient.scanBleDevices(scanSettings, scanFilter)
    }

    private fun dispose(caller: String) {
        Log.i("MainFragment.Lifecycle", "onDispose() called from $caller")
        scanDisposable = null
        updateButtonUIState()
    }

    private fun onScanFailure(throwable: Throwable) {
        if (throwable is BleScanException) showError(throwable)
    }

    private fun updateButtonUIState() =
        scan_toggle_button.setText(if (isScanning) R.string.button_stop_scan else R.string.button_start_scan)

    companion object {
        fun newInstance() = MainFragment()
    }
}
