package org.engrave.packup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.FragmentEventBinding
import org.engrave.packup.ui.main.MainViewModel
import org.engrave.packup.util.HOUR_IN_MILLIS
import java.util.*

@AndroidEntryPoint
class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var linearManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        eventAdapter = EventAdapter(requireContext()).apply {
            setHasStableIds(true)
        }
        linearManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.apply {
            eventsRecyclerView.apply {

                layoutManager = linearManager
                adapter = eventAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    var runnable = Runnable {
                        smoothScrollToPosition()
                    }

                    @Volatile
                    private var isUserControl: Boolean = false

                    override fun onScrolled(r: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(r, dx, dy)
                        mainViewModel.eventViewTimeStamp.value = getChildItemId(getChildAt(0))

                        eventViewModel.nthWeek.value =
                            (getChildViewHolder(getChildAt(0)) as EventAdapter.DailyViewHolder).routine.nthWeek

                        if (r.scrollState == RecyclerView.SCROLL_STATE_SETTLING && !isUserControl) {
                            if (dx in -5..5) {
                                r.stopScroll()
                            }
                        }
                    }


                    fun smoothScrollToPosition() {
                        isUserControl = true
                        eventViewModel.userScrolled = true
                        val stickyInfoView = getChildAt(0)
                        val bottom = stickyInfoView.right
                        val height = stickyInfoView.measuredWidth
                        if (bottom != height) {
                            if (bottom >= (height / 2)) {
                                smoothScrollBy(-(height - bottom), 0)
                            } else {
                                smoothScrollBy(bottom, 0)
                            }
                        }
                    }

                    override fun onScrollStateChanged(r: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(r, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE)
                            if (!isUserControl)
                                postDelayed(runnable, 100)
                        if (r.scrollState != RecyclerView.SCROLL_STATE_SETTLING)
                            isUserControl = false
                    }
                })
            }
        }
        eventViewModel.apply {
            eventList.observe(viewLifecycleOwner) {
                eventAdapter.postList(
                    it
                )
                var currentIndex = 0
                val time = Date().time + 8 * HOUR_IN_MILLIS
                for (i in it.indices) {
                    if (it[i].startOfDayInMillis > time) {
                        currentIndex = i - 1
                        break
                    }
                }
                Log.e("INDEX", currentIndex.toString())
                if (!userScrolled) {
                    binding.eventsRecyclerView.scrollToPosition(
                        currentIndex
                    )
                }
            }
            nthWeek.observe(viewLifecycleOwner){
                binding.eventsNthweek.text = if (it > 16) "考试周" else "$it 周"
            }
        }

        mainViewModel.statusBarStatus.observe(viewLifecycleOwner) {

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}