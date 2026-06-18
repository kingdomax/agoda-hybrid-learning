package com.pramoch.agodahybridcapacitor

import android.os.Build
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@CapacitorPlugin(name = "AgodaNativeInfo") // Registers native plugin identity
class AgodaNativeInfoPlugin : Plugin() {
class AgodaNativeInfoPlugin : Plugin() {

    companion object { // companion object = static i.e.  private static readonly ConcurrentDictionary<string, string?> SessionValues = new();
        private val sessionValues = ConcurrentHashMap<String, String?>()
    }

    @PluginMethod // Exposes method to JavaScript
    fun echo(call: PluginCall) { // PluginCall: Contains JS arguments and Promise controls
        val value = call.getString("value") // Reads JS input

        if (value.isNullOrBlank()) {
            call.reject("value is required")// Rejects JS Promise 
            return
        }

        val result = JSObject()
        result.put("value", value)
        result.put("platform", "android")

        call.resolve(result) // Resolves JS Promise
    }

    @PluginMethod
    fun getDeviceInfo(call: PluginCall) {
        val result = JSObject()
        result.put("platform", "android")
        result.put("osVersion", Build.VERSION.RELEASE)
        result.put("model", Build.MODEL)
        result.put("manufacturer", Build.MANUFACTURER)

        call.resolve(result)
    }

    @PluginMethod
    fun saveSessionValue(call: PluginCall) {
        val key = call.getString("key")
        val value = call.getString("value")

        if (key.isNullOrBlank()) {
            call.reject("key is required")
            return
        }

        sessionValues[key] = value

        val event = JSObject()
        event.put("key", key)
        event.put("value", value)
        event.put("source", "android")
        event.put("changedAt", System.currentTimeMillis().toString())

        notifyListeners("sessionValueChanged", event)

        val result = JSObject() // JSON-like response object
        result.put("success", true)

        call.resolve(result)
    }

    @PluginMethod
    fun getSessionValue(call: PluginCall) {
        val key = call.getString("key")

        if (key.isNullOrBlank()) {
            call.reject("key is required")
            return
        }

        val result = JSObject()
        result.put("value", sessionValues[key])

        call.resolve(result)
    }
}