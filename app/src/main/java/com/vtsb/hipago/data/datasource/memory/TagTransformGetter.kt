package com.vtsb.hipago.data.datasource.memory

import javax.inject.Inject

class TagTransformGetter @Inject constructor() {

    fun get(): Map<String, String> =
        hashMapOf(Pair("artist CG", "artistcg"), Pair("game CG", "gamecg"),
            Pair("all", "(all)"))

    fun getLanguages(): Map<String, String> = hashMapOf(
        Pair("indonesian", "Bahasa Indonesia"),
        Pair("catalan", "català"),
        Pair("cebuano", "Cebuano"),
        Pair("czech", "Čeština"),
        Pair("danish", "Dansk"),
        Pair("german", "Deutsch"),
        Pair("estonian", "eesti"),
        Pair("english", "English"),
        Pair("spanish", "Español"),
        Pair("esperanto", "Esperanto"),
        Pair("french", "Français"),
        Pair("italian", "Italiano"),
        Pair("latin", "Latina"),
        Pair("hungarian", "magyar"),
        Pair("dutch", "Nederlands"),
        Pair("norwegian", "norsk"),
        Pair("polish", "polski"),
        Pair("portuguese", "Português"),
        Pair("romanian", "română"),
        Pair("albanian", "shqip"),
        Pair("slovak", "Slovenčina"),
        Pair("finnish", "Suomi"),
        Pair("swedish", "Svenska"),
        Pair("tagalog", "Tagalog"),
        Pair("vietnamese", "tiếng việt"),
        Pair("turkish", "Türkçe"),
        Pair("greek", "Ελληνικά"),
        Pair("bulgarian", "български"),
        Pair("mongolian", "Монгол"),
        Pair("russian", "Русский"),
        Pair("ukrainian", "Українська"),
        Pair("hebrew", "עברית"),
        Pair("arabic", "العربية"),
        Pair("persian", "فارسی"),
        Pair("thai", "ไทย"),
        Pair("korean", "한국어"),
        Pair("chinese", "中文"),
        Pair("japanese", "日本語"))


}