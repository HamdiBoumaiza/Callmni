package hb.com.callmni.utils

import android.content.Context
import hb.com.callmni.R
import java.text.SimpleDateFormat
import java.util.*


private val SECOND_MILLIS: Long = 1000
private val MINUTE_MILLIS = SECOND_MILLIS * 60
private val HOUR_MILLIS = MINUTE_MILLIS * 60
private val DAY_MILLIS = HOUR_MILLIS * 24
private val WEEK_MILLIS = DAY_MILLIS * 7
private val MONTH_MILLIS = DAY_MILLIS * 30
private val YEAR_MILLIS = MONTH_MILLIS * 12


fun getDateDifference(mContext: Context, timestamp: String): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val mResult = Calendar.getInstance()
    val current = Calendar.getInstance()


    val timeST: Long = timestamp.toLong() * 1000    // timestamp in millis
    val da = Date(timeST)
    val date = df.format(da)

    try {
        val result = df.parse(date)
        mResult.time = result
    } catch (e: Exception) {
        e.printStackTrace()
    }


    var diff = ((current.time.time - mResult.time.time) / YEAR_MILLIS).toInt()
    if (diff > 0) {
        return if (diff > 1)
            String.format(mContext.getString(R.string.befor_years), diff.toString())
        else
            String.format(mContext.getString(R.string.befor_year), diff.toString())
    } else {
        diff = ((current.time.time - mResult.time.time) / MONTH_MILLIS).toInt()
        if (diff > 0) {
            return if (diff > 1)
                String.format(mContext.getString(R.string.befor_months), diff.toString())
            else
                String.format(mContext.getString(R.string.befor_month), diff.toString())
        } else {

            diff = ((current.time.time - mResult.time.time) / WEEK_MILLIS).toInt()
            if (diff > 0) {
                return if (diff > 1)
                    String.format(mContext.getString(R.string.befor_weeks), diff.toString())
                else
                    String.format(mContext.getString(R.string.befor_week), diff.toString())
            } else {
                diff = ((current.time.time - mResult.time.time) / DAY_MILLIS).toInt()
                if (diff > 0) {
                    return if (diff > 1)
                        String.format(mContext.getString(R.string.befor_days), diff.toString())
                    else
                        String.format(mContext.getString(R.string.befor_day), diff.toString())
                } else {
                    diff = ((current.time.time - mResult.time.time) / HOUR_MILLIS).toInt()
                    if (diff > 0) {
                        return String.format(mContext.getString(R.string.befor_hours), diff.toString())
                    } else {
                        diff = ((current.time.time - mResult.time.time) / MINUTE_MILLIS).toInt()
                        if (diff > 0) {
                            return String.format(mContext.getString(R.string.befor_minutes), diff.toString())
                        } else {
                            diff = ((current.time.time - mResult.time.time) / SECOND_MILLIS).toInt()
                            return if (diff > 0) {
                                String.format(mContext.getString(R.string.befor_seconds), diff.toString())
                            } else {
                                mContext.getString(R.string.now)
                            }
                        }
                    }
                }
            }
        }
    }


}

