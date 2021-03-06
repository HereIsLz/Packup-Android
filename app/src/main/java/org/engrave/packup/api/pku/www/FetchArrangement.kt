package org.engrave.packup.api.pku.www

import org.engrave.packup.util.scanAsString
import org.jsoup.Jsoup
import java.net.URL
import java.time.MonthDay
import javax.net.ssl.HttpsURLConnection

private const val WWW_PKU_ROOT_URL = "https://www.pku.edu.cn"
private const val WWW_PKU_CAMPUS_URL = "https://www.pku.edu.cn/campus.html"


/**
 * @return Maps like [(2020-2021, /detail/2409.html), (2019-2020, /detail/178.html)]
 */
fun fetchCampusCalendarUrls() =
    Jsoup.parse(
        (URL(WWW_PKU_CAMPUS_URL).openConnection() as HttpsURLConnection).inputStream.scanAsString()
    )
        .select(".campus3a a")
        .map {
            it.select(".d").text() to it.attr("href")
        }
        .filter {
            it.first.isNotBlank()
        }


/**
 * @return a Map<Int, String> like { 2020 = "第一学期\n...\n第二学期\n...", ...}
 * {2020=第一学期
一、教职工上班：8月31日
二、新生报到
本科新生：9月1日
研究生新生、留学生本科新生、医学部港澳台本科新生：9月14日
医学部研究生新生：9月7日
校本部港澳台本科新生：9月17日
深圳研究生院：8月31日
三、本科新生体检：9月2日至4日
研究生、港澳台本科新生体检：9月15日至20日
医学部研究生新生体检：9月8日至13日
四、2020级本科生军训：9月5日至9月17日
五、新生开学典礼：9月20日
医学部新生开学典礼：9月18日
深圳研究生院新生开学典礼：9月3日
六、上课
校本部：9月21日
医学部：在校本科生8月31日
本科新生9月21日
研究生9月14日
深圳研究生院：9月7日
七、在校学生注册
校本部：9月14日至25日
医学部：8月31日至9月4日
深圳研究生院：9月7日至8日
八、国庆节、中秋节
10月1日至8日放假，全校停课。
10月9日上课，教职工上班。
10月10日至11日公休，课程照常进行。
九、校本部秋季运动会：10月17日，停课
十、校学位评定委员会会议：10月23日
十一、停课复习考试
校本部：1月11日至24日
医学部：1月4日至24日
深圳研究生院：1月11日至24日
十二、校学位评定委员会会议：1月15日
十三、学生寒假：1月25日至2月21日
十四、教职工轮休：1月25日至2月21日
元旦放假安排待国务院办公厅公布2021年节假日安排后另行通知。学校相关专项工作时间由有关部门另行具体通知。
第二学期
一、上课：2月22日
教职工上班：2月22日
二、在校学生注册
校本部、医学部：2月22日至26日
深圳研究生院：2月22日至23日
三、校本部运动会：4月23日至25日，23日停课
医学部运动会：5月15日
四、劳动节及校庆
5月1日劳动节，放假，全校停课。
5月2日，公休，课程照常进行。
5月3日，放假调休，全校停课。
5月4日，校庆相关单位上班，全校停课。
5月5日至7日，放假调休，全校停课。
5月8日至9日，公休，课程照常进行。
五、停课复习考试
校本部：6月14日至27日
医学部、深圳研究生院：6月28日至7月11日
六、学生暑假
校本部：6月28日起
医学部、深圳研究生院：7月12日起
七、校学位评定委员会会议：7月2日
八、办理离校手续
校本部、医学部：7月5日至9日
深圳研究生院：7月5日至8日
九、毕业典礼：7月6日至7月7日
医学部毕业典礼：7月5日
深圳研究生院毕业典礼：7月3日
十、校本部暑期学校：7月5日至8月8日
十一、教职工轮休
校本部、医学部：7月12日起
深圳研究生院：7月14日起
清明节、端午节放假安排待国务院办公厅公布2021年节假日安排后另行通知。学校相关专项工作时间由有关部门另行具体通知。
北京大学2020-2021学年上课时间
校本部、医学部上课时间
深圳研究生院上课时间
第一节
08:00—08:50
第一节
08:00—08:50
第二节
09:00—09:50
第二节
09:00—09:50
第三节
10:10—11:00
第三节
10:10—11:00
第四节
11:10—12:00
第四节
11:10—12:00
第五节
13:00—13:50
第五节
13:30—14:20
第六节
14:00—14:50
第六节
14:30—15:20
第七节
15:10—16:00
第七节
15:40—16:30
第八节
16:10—17:00
第八节
16:40—17:30
第九节
17:10—18:00
第九节
18:30—19:20
第十节
18:40—19:30
第十节
19:30—20:20
第十一节
19:40—20:30
第十一节
20:30—21:20
第十二节
20:40—21:30, 2019=第一学期
一、新生报到：
　　本科新生：8月17日
　　深圳研究生院：8月26日
　　研究生、港澳台及留学生本科新生：9月3日
二、本科新生体检：8月18日至20日
　　研究生、港澳台本科新生体检：9月4日至8日
三、2019级本科生军训：8月21日至9月2日
四、教职工上班：8月26日
五、新生开学典礼：9月6日
　　医学部新生开学典礼：9月5日
　　深圳研究生院新生开学营：8月29日至30日
六、上课：
　　校本部：9月9日
　　医学部：在校本科生8月26日，本科新生9月9日，研究生9月16日
　　深圳研究生院：9月2日
七、在校学生注册：
　　校本部：9月9日至13日
　　医学部：8月26日至30日
　　深圳研究生院：9月2日至3日
八、中秋节：
　　9月13日，中秋节，放假，全校停课。
　　9月14日至15日，公休，课程照常进行。
九、国庆节：
　　9月28日至29日，公休，课程照常进行。
　　9月30日至10月6日，放假，全校停课。
十、校学位评定委员会会议：10月29日
十一、停课复习考试：
　　校本部、医学部：12月30日至1月12日
　　深圳研究生院： 1月6日至19日
十二、元旦：1月1日，放假，停考。
十三、校学位评定委员会会议：1月3日
十四、学生寒假：
　　校本部、医学部：1月13日至2月16日
　　深圳研究生院：1月20日至2月16日
十五、教职工轮休：
　　校本部、医学部：1月13日至2月16日
　　深圳研究生院：1月20日至2月12日
　　学校相关专项工作时间由有关部门另行具体通知。
第二学期
一、上课：2月17日
　　教职工上班：校本部、医学部2月17日，深圳研究生院2月13日
二、在校学生注册：
　　校本部、医学部：2月17日至21日
　　深圳研究生院：2月17日至18日
三、校本部运动会：4月24日至26日，24日停课
　　医学部运动会：5月16日
四、劳动节及校庆：
　　5月1日劳动节，放假，全校停课。
　　5月2日至3日，公休，课程照常进行。
　　5月4日校庆相关单位上班，全校停课。
　　5月5日至8日，放假调休，全校停课。
　　5月9日至10日，公休，课程照常进行。
五、停课复习考试：
　　校本部：6月8日至21日
　　医学部、深圳研究生院：6月22日至7月5日
六、学生暑假：
　　校本部：6月22日起
　　医学部、深圳研究生院：7月6日起
七、校学位评定委员会会议：6月26日
八、办理离校手续：
　　校本部、医学部：6月29日至7月3日
　　深圳研究生院：6月29日至7月2日
九、毕业典礼：6月30日至7月1日
　　医学部毕业典礼：6月29日
　　深圳研究生院毕业典礼：6月27日
十、校本部暑期学校：6月29日至8月2日
十一、教职工轮休：
　　校本部、医学部：7月6日起
　　深圳研究生院：7月8日起
　　清明节、端午节放假安排待国务院办公厅公布2020年节假日安排后另行通知。学校相关专项工作时间由有关部门另行具体通知。
校本部、医学部上课时间
深圳研究生院上课时间
第一节
08:00—08:50
第一节
08:00—08:50
第二节
09:00—09:50
第二节
09:00—09:50
第三节
10:10—11:00
第三节
10:10—11:00
第四节
11:10—12:00
第四节
11:10—12:00
第五节
13:00—13:50
第五节
13:30—14:20
第六节
14:00—14:50
第六节
14:30—15:20
第七节
15:10—16:00
第七节
15:40—16:30
第八节
16:10—17:00
第八节
16:40—17:30
第九节
17:10—18:00
第九节
18:30—19:20
第十节
18:40—19:30
第十节
19:30—20:20
第十一节
19:40—20:30
第十一节
20:30—21:20
第十二节
20:40—21:30}
 * 2020 is yearStart
 */
fun fetchAllCampusCalendar(): Map<Int, List<String>>{
    val urlMaps = fetchCampusCalendarUrls()
    return urlMaps.map { pair ->
        pair.first.substringBefore("-").toInt() to Jsoup.parse(
            (URL(WWW_PKU_ROOT_URL + pair.second).openConnection() as HttpsURLConnection)
                .inputStream.scanAsString()
        ).select(".txt p")
            .filter{
                it.text().isNotBlank()
            }
            .map {
                it.text()
            }
    }.toMap()
}

fun parseCampusCalendar(cld: String){

}

val monthDayRangeRegex1 = """[0-9]{1,2}月[0-9]{1,2}日""".toRegex()
val monthDayRangeRegex2 = """[0-9]{1,2}月[0-9]{1,2}日至[0-9]{1,2}日""".toRegex()
val monthDayRangeRegex3 = """[0-9]{1,2}月[0-9]{1,2}日至[0-9]{1,2}月[0-9]{1,2}日""".toRegex()

data class CampusCalendarEventEntry(
    val semesterOrdinal: Int,
    val eventOrdinal: String,
    val eventName: String,
    val literalMonthStart: Int,
    val literalMonthEnd: Int,
    val literalDayStart: Int,
    val literalDayEnd: Int,
    val target: String,
)