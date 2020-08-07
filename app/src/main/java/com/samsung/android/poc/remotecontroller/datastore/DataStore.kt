package com.samsung.android.poc.remotecontroller.datastore

import android.content.Context
import com.google.gson.Gson


object DataStore {

    fun saveTVToken(context: Context, token: String) {
        saveString(context, token, KEY_TV_SECURITY_TOKEN)
    }

    fun getTVToken(context: Context): String {
        return getString(context, KEY_TV_SECURITY_TOKEN)
    }

    fun <T> saveObject(context: Context, t: T, key: String) {
        val preference = context.getSharedPreferences(PREFERENCE_REMOTE_CONTROL, 0)
        val gson = Gson()
        val json = gson.toJson(t)
        val edit = preference.edit()
        edit.putString(key, json)
        edit.apply()
    }

    fun <T> getObject(context: Context, clazz: Class<T>, key: String): T {
        return Gson().fromJson(getString(context, key), clazz)
    }

    fun saveString(context: Context, value: String, key: String) {
        val preference = context.getSharedPreferences(PREFERENCE_REMOTE_CONTROL, 0)
        val edit = preference.edit()
        edit.putString(key, value)
        edit.apply()
    }

    fun getString(context: Context, key: String): String {
        val preference = context.getSharedPreferences(PREFERENCE_REMOTE_CONTROL, 0)
        return preference.getString(key, "")!!
    }

    fun saveBoolean(context: Context, value: Boolean, key: String) {
        val preference = context.getSharedPreferences(PREFERENCE_REMOTE_CONTROL, 0)
        val edit = preference.edit()
        edit.putBoolean(key, value)
        edit.apply()
    }

    fun getBoolean(context: Context, key: String): Boolean {
        val preference = context.getSharedPreferences(PREFERENCE_REMOTE_CONTROL, 0)
        return preference.getBoolean(key, false)
    }

    fun saveSwitchButtonStatus(context: Context, value: Boolean) {
        saveBoolean(context, value, KEY_NUMPAD_SWITCH_STATUS)
    }

    fun getSwitchButtonStatus(context: Context): Boolean {
        return getBoolean(context, KEY_NUMPAD_SWITCH_STATUS)
    }
}