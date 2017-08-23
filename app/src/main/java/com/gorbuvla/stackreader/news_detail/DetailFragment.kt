package com.gorbuvla.stackreader.news_detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorbuvla.stackreader.R
import com.gorbuvla.stackreader.common.fromHTML
import com.gorbuvla.stackreader.common.toStackPoints
import com.gorbuvla.stackreader.common.toStringDate
import com.gorbuvla.stackreader.domain.StackNewsItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_detail.*

/**
 * Created by vlad on 21/08/2017.
 */
class DetailFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(R.layout.layout_detail, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val item = arguments.getParcelable<StackNewsItem>("NEWS_ITEM") ?: throw IllegalArgumentException("News item should be provided")
            val color = arguments.getInt("COLOR", R.color.m_teal)

            //set card color
            user_pane.setBackgroundColor(ContextCompat.getColor(context, color))
            //profile image
            Picasso.with(context).load(item.owner.profile_image).noFade().fit().into(detail_avatar)
            //username
            username_textview.text = item.owner.display_name
            //reputation
            points_textview.text = item.owner.reputation.toStackPoints()
            //question title
            q_title.text = item.title.fromHTML()
            //answer count
            answer_count.text = if (item.answer_count > 0) { "Answers: ${item.answer_count}" } else { "Not answered"}
            //date created
            date_textview.text = item.creation_date.toStringDate()
            //question body
            content_textview.text = item.body.fromHTML()
            content_textview.movementMethod = ScrollingMovementMethod()
        }
    }
}