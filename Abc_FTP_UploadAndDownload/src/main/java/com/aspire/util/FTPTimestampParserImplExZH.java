package com.aspire.util;

import org.apache.commons.net.ftp.parser.FTPTimestampParserImpl;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 解决apache ftp中文语言环境下，FTPClient.listFiles()为空的bug
 *
 * @author JustryDeng
 * @date 2019/2/1 15:36
 */
@SuppressWarnings("all")
class FTPTimestampParserImplExZH extends FTPTimestampParserImpl {

    private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("mm d hh:mm");
    private SimpleDateFormat recentDateFormat = new SimpleDateFormat("yyyy mm d");

    /**
     * @author hzwei206 将中文环境的时间格式进行转换
     */
    private String formatDate_Zh2En(String timeStrZh) {
        if (timeStrZh == null) {
            return "";
        }

        int len = timeStrZh.length();
        StringBuffer sb = new StringBuffer(len);
        char ch = ' ';
        for (int i = 0; i < len; i++) {
            ch = timeStrZh.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == ' ' || ch == ':') {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * Implements the one { FTPTimestampParser#parseTimestamp(String) method} in the { FTPTimestampParser
     * FTPTimestampParser} interface according to this algorithm: If the recentDateFormat member has been defined, try
     * to parse the supplied string with that. If that parse fails, or if the recentDateFormat member has not been
     * defined, attempt to parse with the defaultDateFormat member. If that fails, throw a ParseException.
     *
     * @see org.apache.commons.net.ftp.parser.FTPTimestampParser#parseTimestamp(String)
     */
    @Override
    public Calendar parseTimestamp(String timestampStr) throws ParseException {
        timestampStr = formatDate_Zh2En(timestampStr);
        Calendar now = Calendar.getInstance();
        now.setTimeZone(this.getServerTimeZone());

        Calendar working = Calendar.getInstance();
        working.setTimeZone(this.getServerTimeZone());
        ParsePosition pp = new ParsePosition(0);

        Date parsed = null;
        if (this.recentDateFormat != null) {
            parsed = recentDateFormat.parse(timestampStr, pp);
        }
        if (parsed != null && pp.getIndex() == timestampStr.length()) {
            working.setTime(parsed);
            working.set(Calendar.YEAR, now.get(Calendar.YEAR));
            if (working.after(now)) {
                working.add(Calendar.YEAR, -1);
            }
        } else {
            pp = new ParsePosition(0);
            parsed = defaultDateFormat.parse(timestampStr, pp);
            // note, length checks are mandatory for us since
            // SimpleDateFormat methods will succeed if less than
            // full string is matched. They will also accept,
            // despite "leniency" setting, a two-digit number as
            // a valid year (e.g. 22:04 will parse as 22 A.D.)
            // so could mistakenly confuse an hour with a year,
            // if we don't insist on full length parsing.
            if (parsed != null && pp.getIndex() == timestampStr.length()) {
                working.setTime(parsed);
            } else {
                throw new ParseException("Timestamp could not be parsed with older or recent DateFormat", pp.getIndex());
            }
        }
        return working;
    }
}
