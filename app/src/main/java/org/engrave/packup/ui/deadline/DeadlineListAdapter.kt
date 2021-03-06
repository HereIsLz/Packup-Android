package org.engrave.packup.ui.deadline

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.engrave.packup.DEADLINE_DETAIL_ACTIVITY_UID
import org.engrave.packup.DeadlineDetailActivity
import org.engrave.packup.R
import org.engrave.packup.component.DistinctiveDiffCallback
import org.engrave.packup.component.IDistinctive
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.databinding.ItemDeadlineBinding
import org.engrave.packup.databinding.ItemDeadlineHeaderBinding
import org.engrave.packup.databinding.ItemPaddingBinding
import org.engrave.packup.util.*
import ws.vinta.pangu.Pangu
import java.util.*
import kotlin.math.floor

sealed class DeadlineItem : IDistinctive

data class DeadlineMember(val deadline: Deadline) : DeadlineItem() {
    override fun getTypeDescriptor() = "M"

    override fun getIdentityDescriptor() = deadline.uid.toString()

    override fun getModifierDescriptor() = ""

    override fun isOfSameContent(other: IDistinctive): Boolean =
        if (other is DeadlineMember) this.deadline.contentsSameWith(other.deadline)
        else false
}

data class DeadlineHeader(val title: String, val num: Int) : DeadlineItem() {
    override fun getTypeDescriptor() = "H"

    override fun getIdentityDescriptor() = title

    override fun getModifierDescriptor() = ""

    override fun isOfSameContent(other: IDistinctive): Boolean =
        if (other is DeadlineHeader) this.title == other.title && this.num == other.num
        else false
}

data class DeadlinePadding(val sumUpString: String) : DeadlineItem() {
    override fun getTypeDescriptor() = "P"

    override fun getIdentityDescriptor() = ""

    override fun getModifierDescriptor() = ""

    override fun isOfSameContent(other: IDistinctive): Boolean = false
}


const val PAYLOAD_STAR_CHANGED = "STAR"
const val PAYLOAD_HEADER_NUM_CHANGED = "HEADER"
const val PAYLOAD_SPACING_NUM_CHANGED = "PADDING"

class DeadlineListAdapter(
    private val activity: Activity,
    private val context: Context,
    private val onClickStar: (Int, Boolean) -> Unit,
    private val onClickComplete: (Int, Boolean) -> Unit,
    private val onClickRestore: (Int, Boolean) -> Unit,
    private val onSwipeDelete: (Int) -> Unit,
    private val onSwipeComplete: (Int) -> Unit
) :
    ListAdapter<DeadlineItem, RecyclerView.ViewHolder>(object :
        DistinctiveDiffCallback<DeadlineItem>() {
        override fun getChangePayload(oldItem: DeadlineItem, newItem: DeadlineItem): Any? {
            if (oldItem is DeadlineMember && newItem is DeadlineMember) {
                if (oldItem.deadline.is_starred != newItem.deadline.is_starred) {
                    return PAYLOAD_STAR_CHANGED
                }
            }
            if (oldItem is DeadlineHeader && newItem is DeadlineHeader) {
                if (oldItem.num != newItem.num) {
                    return PAYLOAD_HEADER_NUM_CHANGED
                }
            }
            if (oldItem is DeadlinePadding && newItem is DeadlinePadding){
                return PAYLOAD_SPACING_NUM_CHANGED
            }
            return super.getChangePayload(oldItem, newItem)
        }
    }),
    DeadlineItemTouchHelper.IDeadlineItemTouchHelperAdapter {
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    val pangu = Pangu()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            DEADLINE_ITEM_HEADER -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeadlineHeaderBinding.inflate(layoutInflater, parent, false)
                DeadlineHeaderViewHolder(binding)
            }
            DEADLINE_ITEM_MEMBER -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeadlineBinding.inflate(layoutInflater, parent, false)
                DeadlineMemberViewHolder(binding)
            }
            DEADLINE_PADDING -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPaddingBinding.inflate(layoutInflater, parent, false)
                DeadlinePaddingViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType when inflating deadline list.")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is DeadlineHeaderViewHolder -> holder.bind(getItem(position) as DeadlineHeader)
        is DeadlineMemberViewHolder -> holder.bind(getItem(position) as DeadlineMember)
        is DeadlinePaddingViewHolder -> holder.bind(getItem(position) as DeadlinePadding)
        else -> throw ClassCastException("Unknown ViewHolder Class ${holder::class.simpleName} when inflating deadline list.")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (holder is DeadlineMemberViewHolder) {
            if (payloads.contains(PAYLOAD_STAR_CHANGED))
                holder.resetStarStatus(getItem(position) as DeadlineMember)
        }
        if (holder is DeadlineHeaderViewHolder) {
            if (payloads.contains(PAYLOAD_HEADER_NUM_CHANGED))
                holder.reBindNum(getItem(position) as DeadlineHeader)
        }
        if (holder is DeadlinePaddingViewHolder){
            if(payloads.contains(PAYLOAD_SPACING_NUM_CHANGED))
                holder.bind(getItem(position) as DeadlinePadding)
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if(holder is DeadlineMemberViewHolder){
            holder.binding.starAnim.cancelAnimation()
            holder.binding.starAnim.frame = 0
            holder.binding.checkAnim.cancelAnimation()
            holder.binding.checkAnim.frame = 0
        }
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DeadlineMember -> DEADLINE_ITEM_MEMBER
        is DeadlineHeader -> DEADLINE_ITEM_HEADER
        is DeadlinePadding -> DEADLINE_PADDING
    }

    interface DeadlineItemViewHolder

    inner class DeadlineMemberViewHolder internal constructor(val binding: ItemDeadlineBinding) :
        RecyclerView.ViewHolder(binding.root), DeadlineItemViewHolder {
        fun bind(item: DeadlineMember) {
            val remainingTime = item.deadline.due_time?.minus(Date().time)
            binding.apply {
                root.setOnClickListener {

                    context.startActivity(
                        Intent(
                            context,
                            DeadlineDetailActivity::class.java
                        ).apply {
                            putExtra(DEADLINE_DETAIL_ACTIVITY_UID, item.deadline.uid)
                        }
                    )
                }
                deadlineItemMemberTitle.text = pangu.spacingText(item.deadline.name)
                deadlineItemMemberDesc.text =
                    item.deadline.due_time.asLocalCalendar()?.toGlobalizedString(
                        context,
                        autoOmitYear = true,
                        omitTime = false,
                        omitWeek = true
                    )
                deadlineItemMemberWarningPill.apply {
                    if (item.deadline.has_submission) {
                        text = "已提交"
                        visibility = View.VISIBLE
                        background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pill_safe_green
                            )
                    } else when {
                        remainingTime == null -> {
                            visibility = View.GONE
                        }
                        remainingTime <= 0 -> {
                            text = "已逾期"
                            visibility = View.VISIBLE
                            background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.pill_warning_purple
                                )
                        }
                        remainingTime < DAY_IN_MILLIS -> {
                            text =
                                "剩余 ${floor(remainingTime.toDouble() / HOUR_IN_MILLIS).toInt()} 小时"
                            visibility = View.VISIBLE
                            background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.pill_warning_red
                                )
                        }
                        remainingTime < WEEK_IN_MILLIS -> {
                            text =
                                "剩余 ${floor(remainingTime.toDouble() / DAY_IN_MILLIS).toInt()} 天"
                            visibility = View.VISIBLE
                            background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.pill_warning_orange
                                )
                        }
                        else -> {
                            visibility = View.GONE
                        }
                    }
                }
                deadlineItemStarButton.apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            if (item.deadline.is_starred)
                                R.drawable.outline_button_star_filled
                            else R.drawable.outline_button_star
                        )
                    )
//                    if (item.deadline.is_starred)
//                        imageTintList = ColorStateList.valueOf(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.starColor
//                            )
//                        )
                    setOnClickListener {
                        if (!item.deadline.is_starred) {
                            binding.starAnim.playAnimation()
                        }
                        onClickStar(item.deadline.uid, !item.deadline.is_starred)
                    }
                }
                if (!item.deadline.is_deleted) {
                    deadlineItemCompleteButton.apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                if (item.deadline.is_completed)
                                    R.drawable.outline_button_circle_filled
                                else R.drawable.outline_button_circle
                            )
                        )


                        setOnClickListener {
                            if (!item.deadline.is_completed) {
                                binding.checkAnim.playAnimation()
                                onClickComplete(item.deadline.uid, true)
                                setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.outline_button_circle_filled
                                    )
                                )
                            } else {
                                onClickComplete(item.deadline.uid, false)
                                setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.outline_button_circle
                                    )
                                )
//                                imageTintList = ColorStateList.valueOf(
//                                    ContextCompat.getColor(
//                                        context,
//                                        R.color.colorText
//                                    )
//                                )
//                                floatingCheckmark.visibility = View.INVISIBLE
                            }
                        }
                    }
                } else {
                    deadlineItemCompleteButton.apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_packup_delete
                            )
                        )
//                        imageTintList = ColorStateList.valueOf(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.colorText
//                            )
//                        )
                        setOnClickListener {
                            onClickRestore(item.deadline.uid, false)
                        }
                    }
                }
                if (item.deadline.source_course_name_without_semester.isBlank()) {
                    deadlineItemMemberSource.visibility = View.INVISIBLE
                    textSeparator.visibility = View.INVISIBLE
                } else {
                    deadlineItemMemberSource.visibility = View.VISIBLE
                    textSeparator.visibility = View.VISIBLE
                    deadlineItemMemberSource.text =
                        pangu.spacingText(item.deadline.source_course_name_without_semester)
                }
            }
        }

        fun resetStarStatus(item: DeadlineMember) {
            if (item.deadline.is_starred) {
                binding.deadlineItemStarButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.outline_button_star_filled
                    )
                )
//                binding.deadlineItemStarButton.imageTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.starColor
//                    )
//                )
            } else {
                binding.deadlineItemStarButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.outline_button_star
                    )
                )
//                binding.deadlineItemStarButton.imageTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.colorText
//                    )
//                )
            }
        }
    }

    inner class DeadlineHeaderViewHolder internal constructor(private val binding: ItemDeadlineHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), DeadlineItemViewHolder {
        fun bind(header: DeadlineHeader) {
            binding.apply {
                deadlineItemHeaderDigest.text = header.title
                deadlineItemHeaderDetail.text = header.num.toString()
            }
        }
        fun reBindNum(header: DeadlineHeader) {
            binding.apply {
                deadlineItemHeaderDetail.text = header.num.toString()
            }
        }
    }

    inner class DeadlinePaddingViewHolder internal constructor(private val binding: ItemPaddingBinding) :
        RecyclerView.ViewHolder(binding.root), DeadlineItemViewHolder {
        fun bind(padding: DeadlinePadding) {
            binding.sumUpText.text = padding.sumUpString
        }
    }

    companion object {
        const val DEADLINE_ITEM_HEADER = 0
        const val DEADLINE_ITEM_MEMBER = 1
        const val DEADLINE_PADDING = 2
    }

    override fun onItemRemoved(position: Int) {
        val item = getItem(position)
        if (item is DeadlineMember)
            onSwipeDelete(item.deadline.uid)
    }

    override fun onItemCompleted(position: Int) {
        val item = getItem(position)
        if (item is DeadlineMember)
            onSwipeComplete(item.deadline.uid)
    }

}
