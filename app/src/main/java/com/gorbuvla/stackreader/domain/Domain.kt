package com.gorbuvla.stackreader.domain

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by vlad on 20/08/2017.
 */

class StackResponse(
        val items: List<StackNewsItem>
)

//@Parcelize
class StackNewsItem(
        val owner: QuestionOwner,
        val is_answered: Boolean,
        val view_count: Int,
        val answer_count: Int,
        val score: Int,
        val last_activity_date: Long,
        val creation_date: Long,
        val question_id: Int,
        val link: String,
        val title: String,
        val body: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(QuestionOwner::class.java.classLoader),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(owner, flags)
        parcel.writeByte(if (is_answered) 1 else 0)
        parcel.writeInt(view_count)
        parcel.writeInt(answer_count)
        parcel.writeInt(score)
        parcel.writeLong(last_activity_date)
        parcel.writeLong(creation_date)
        parcel.writeInt(question_id)
        parcel.writeString(link)
        parcel.writeString(title)
        parcel.writeString(body)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StackNewsItem> {
        override fun createFromParcel(parcel: Parcel): StackNewsItem {
            return StackNewsItem(parcel)
        }

        override fun newArray(size: Int): Array<StackNewsItem?> {
            return arrayOfNulls(size)
        }
    }

}


//@Parcelize
class QuestionOwner(
        val reputation: Int,
        val user_id: Int,
        val user_type: String,
        val accept_rate: Int,
        val profile_image: String?,
        val display_name: String,
        val link: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(reputation)
        parcel.writeInt(user_id)
        parcel.writeString(user_type)
        parcel.writeInt(accept_rate)
        parcel.writeString(profile_image)
        parcel.writeString(display_name)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionOwner> {
        override fun createFromParcel(parcel: Parcel): QuestionOwner {
            return QuestionOwner(parcel)
        }

        override fun newArray(size: Int): Array<QuestionOwner?> {
            return arrayOfNulls(size)
        }
    }
}