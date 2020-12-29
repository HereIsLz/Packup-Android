package org.engrave.packup.ui.event

import android.content.Context
import android.graphics.*
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.DrawableMarginSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.util.inDp


class EventAdapter(
    private val context: Context
) : RecyclerView.Adapter<EventAdapter.DailyViewHolder>() {

    private var eventList: List<DailyEventsItem> = listOf()
    fun postList(ss: List<DailyEventsItem>) {
        eventList = ss
        notifyDataSetChanged()
    }

    inner class DailyViewHolder(private val itemView: View, private val parentHeight: Int) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var eventDateHeroText: TextView
        lateinit var eventContainer: FrameLayout

        var canvasBaseY: Int = 36.inDp(context)
        var canvasHeight: Int = parentHeight - canvasBaseY
        var minuteHeight = canvasHeight.toFloat() / (960) // 8:00 ~ 22:00 + 更早/ 更晚

        private fun Int.toY() = ((this - 420) * minuteHeight).toInt()

        fun bind(dailyRoutineItem: DailyEventsItem) {
            eventContainer = itemView.findViewById(R.id.event_item_day_container)
            eventDateHeroText = itemView.findViewById(R.id.event_date_title)
            eventContainer.doOnPreDraw {

            }
            eventDateHeroText.text = dailyRoutineItem.startOfDayInMillis.toString()
            dailyRoutineItem.courses.forEach {
                eventContainer.addView(
                    generateClassInfoGrid(it)
                )
            }
        }

        private fun generateClassInfoGrid(course: DailyCourseItem) =
            object : AppCompatButton(context) {
//                val paint = Paint()
//                val rect = Rect(0, 0, width, width)
//                override fun onDraw(canvas: Canvas?) {
//                    paint.isAntiAlias = true
//                    paint.color = Color.rgb(255, 255, 215)
//                    canvas?.drawRect(rect, paint)
//                    super.onDraw(canvas)
//                }
            }.apply {
                isAllCaps = false
                gravity = Gravity.START or Gravity.TOP
                background = ContextCompat.getDrawable(context, R.drawable.course_button_default)
                elevation = 0F
                stateListAnimator = null

                setPadding(6.inDp(context),2.inDp(context), 6.inDp(context), 2.inDp(context))
                text = SpannableStringBuilder(
                    "${course.eventName}\n${course.place}"
                ).apply {
                    setSpan(
                        AbsoluteSizeSpan(14, true),
                        0,
                        course.eventName.length + course.place.length + 1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        course.eventName.length + 1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.eventTextColor
                            )
                        ), 0, course.eventName.length + course.place.length + 1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    val drawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_loc
                    )
                    drawable?.let {
                        setSpan(
                            DrawableMarginSpan(it.apply {
                                setTint(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.eventTextColor
                                    )
                                )
                            }, 2),
                            course.eventName.length + 1,
                            course.eventName.length + 2,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                y = (course.startMinute.toY()).toFloat()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ((course.endMinute - course.startMinute) * minuteHeight).toInt()
                )
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_day, parent, false)
        view.layoutParams.width = parent.measuredWidth / 3
        return DailyViewHolder(view, parent.measuredHeight)
    }

    override fun onViewRecycled(holder: DailyViewHolder) {
        super.onViewRecycled(holder)
        holder.eventContainer.removeAllViews()
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size
}