package com.lighthouselabs.findhenry.ext

import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lighthouselabs.findhenry.R
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.exceptions.BleScanException.*
import java.util.*
import java.util.concurrent.TimeUnit

val ERROR_MESSAGES = mapOf(
    Pair(BLUETOOTH_NOT_AVAILABLE, R.string.error_bluetooth_not_available),
    Pair(BLUETOOTH_DISABLED, R.string.error_bluetooth_disabled),
    Pair(LOCATION_PERMISSION_MISSING, R.string.error_location_permission_missing),
    Pair(LOCATION_SERVICES_DISABLED, R.string.error_location_services_disabled),
    Pair(SCAN_FAILED_ALREADY_STARTED, R.string.error_scan_failed_already_started),
    Pair(SCAN_FAILED_APPLICATION_REGISTRATION_FAILED, R.string.error_scan_failed_application_registration_failed),
    Pair(SCAN_FAILED_FEATURE_UNSUPPORTED, R.string.error_scan_failed_feature_unsupported),
    Pair(SCAN_FAILED_INTERNAL_ERROR, R.string.error_scan_failed_internal_error),
    Pair(SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES, R.string.error_scan_failed_out_of_hardware_resources),
    Pair(BLUETOOTH_CANNOT_START, R.string.error_bluetooth_cannot_start),
    Pair(UNKNOWN_ERROR_CODE, R.string.error_unknown_error)
)

fun Fragment.showError(exception: BleScanException) =
    getErrorMessage(exception).let { errorMessage ->
        Log.e("Scanning", errorMessage, exception)
        showSnackBarShort(errorMessage)
    }

private fun Fragment.showSnackBarShort(text: String) {
    Snackbar.make(activity!!.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show()
}

private fun Fragment.getErrorMessage(exception: BleScanException): String =
// Special case, as there might or might not be a retry date suggestion
    if (exception.reason == UNDOCUMENTED_SCAN_THROTTLE) {
        getScanThrottleErrorMessage(exception.retryDateSuggestion)
    } else {
        // Handle all other possible errors
        ERROR_MESSAGES[exception.reason]?.let { errorResId ->
            getString(errorResId)
        } ?: run {
            // unknown error - return default message
            Log.w("Scanning", String.format(getString(R.string.error_no_message), exception.reason))
            getString(R.string.error_unknown_error)
        }
    }

private fun Fragment.getScanThrottleErrorMessage(retryDate: Date?): String =
    with(StringBuilder(getString(R.string.error_undocumented_scan_throttle))) {
        retryDate?.let { date ->
            String.format(
                Locale.getDefault(),
                getString(R.string.error_undocumented_scan_throttle_retry),
                date.secondsUntil
            ).let { append(it) }
        }
        toString()
    }

private val Date.secondsUntil: Long
    get () = TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis())