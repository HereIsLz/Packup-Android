package org.engrave.packup.apitest

import org.engrave.packup.api.pku.course.fetchCourseLoginCookies
import org.engrave.packup.api.pku.course.fetchDeadlineDetailHtml
import org.engrave.packup.api.pku.www.CampusCalendarEventEntry
import org.engrave.packup.api.pku.www.fetchAllCampusCalendar
import org.engrave.packup.api.pku.www.monthDayRangeRegex1
import org.engrave.packup.pw
import org.engrave.packup.sid
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.asDocument
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CampusCalendarTest {
    @Test
    fun mainTest() {
        val entries: List<CampusCalendarEventEntry>;
        val lst = fetchAllCampusCalendar()[2020] ?: listOf()
        var flagSemesterOrdinal: Int?
        var flagEventName: String? = null
        var flagCapitalOrdinal: Int? = null

        val capitalOrdinals = listOf(
            "一、",
            "二、",
            "三、",
            "四、",
            "五、",
            "六、",
            "七、",
            "八、",
            "九、",
            "十、",
            "十一、",
            "十二、",
            "十三、",
            "十四、",
            "十五、",
            "十六、",
            "十七、",
            "十八、",
            "十九、",
            "二十、"
        )

        for (i in 0..lst.size - 1) {
            val str = lst[i]
            if (str.matches("第[一二]学期".toRegex())) {
                flagSemesterOrdinal = when {
                    str.contains("一") -> 1
                    str.contains("二") -> 2
                    else -> throw Exception("")
                }
                continue
            }
            capitalOrdinals.forEachIndexed { idx, itm ->
                if (str.contains(itm)) {
                    flagCapitalOrdinal = idx
                    flagEventName = str.substringAfter(capitalOrdinals[idx]).substringBefore("：")
                    str.replace(capitalOrdinals[idx], "")
                }
            }
            val dateStrings = monthDayRangeRegex1.findAll(str).toList()
            /* 不包含任何日期信息 标记事件 */
            if (dateStrings.isNullOrEmpty()) {
                print("!!")
            }
            println("${flagCapitalOrdinal?.plus(1)}${flagEventName.orEmpty()}  $str")
        }
    }

}
