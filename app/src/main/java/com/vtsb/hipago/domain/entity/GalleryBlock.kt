package com.vtsb.hipago.domain.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class GalleryBlock(
    val id: Int,
    val type: GalleryBlockType,
    val title: String,
    val date: Date,
    val tags: Map<TagType, List<String>>,
    val thumbnail: String,
    val related: List<Int>
): Parcelable {

    private companion object : Parceler<GalleryBlock> {

        override fun GalleryBlock.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeSerializable(type)
            parcel.writeString(title)
            parcel.writeSerializable(date)

            parcel.writeInt(tags.size)
            for ((key, list) in tags.entries) {
                parcel.writeSerializable(key)
                parcel.writeList(list)
            }
            parcel.writeString(thumbnail)
            parcel.writeList(related)
        }

        override fun create(parcel: Parcel): GalleryBlock =
            GalleryBlock(parcel.readInt(), parcel.readSerializable() as GalleryBlockType,
                parcel.readString()!!, parcel.readSerializable() as Date,
                readMap(parcel), parcel.readString()!!,
                readList(parcel, Int::class.java.classLoader!!))

        private fun <T> readList(parcel: Parcel, classLoader: ClassLoader): List<T> {
            val list: MutableList<T> = ArrayList()
            parcel.readList(list, classLoader)
            return list
        }

        private fun readMap(parcel: Parcel): Map<TagType, List<String>> {
            val map: MutableMap<TagType, List<String>> = EnumMap(TagType::class.java)
            val size = parcel.readInt()
            for (i in 0..size) {
                val key: TagType = parcel.readSerializable() as TagType
                map[key] = readList(parcel, String::class.java.classLoader!!)
            }
            return map
        }
    }

    override fun equals(other: Any?): Boolean =
        other != null && other is GalleryBlock &&
        id == other.id && type == other.type &&
        title == other.title

    override fun hashCode(): Int =
        (type.hashCode()) * 31 + id

}


