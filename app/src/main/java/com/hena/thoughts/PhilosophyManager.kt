package com.hena.thoughts

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class PhilosophyManager(private val context: Context) {

    private val gson = Gson()
    private val userFile = File(context.filesDir, "user_philosophies.json")

    fun loadAll(): List<Philosophy> {
        return if (userFile.exists()) {
            parseJson(userFile.readText())
        } else {
            loadBundled().also { saveAll(it) }
        }
    }

    fun saveAll(list: List<Philosophy>) {
        userFile.writeText(gson.toJson(list))
    }

    fun add(philosophy: Philosophy): List<Philosophy> {
        val list = loadAll().toMutableList()
        val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
        list.add(philosophy.copy(id = nextId))
        saveAll(list)
        return list
    }

    fun update(philosophy: Philosophy): List<Philosophy> {
        val list = loadAll().toMutableList()
        val index = list.indexOfFirst { it.id == philosophy.id }
        if (index >= 0) list[index] = philosophy
        saveAll(list)
        return list
    }

    fun delete(id: Int): List<Philosophy> {
        val list = loadAll().toMutableList()
        list.removeAll { it.id == id }
        saveAll(list)
        return list
    }

    fun getRandom(): Philosophy? {
        val list = loadAll()
        return if (list.isNotEmpty()) list.random() else null
    }

    private fun loadBundled(): List<Philosophy> {
        val json = context.resources.openRawResource(R.raw.philosophies)
            .bufferedReader().use { it.readText() }
        return parseJson(json)
    }

    private fun parseJson(json: String): List<Philosophy> {
        val type = object : TypeToken<List<Philosophy>>() {}.type
        return gson.fromJson(json, type)
    }
}
