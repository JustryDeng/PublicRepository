package com.aspire.util;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;

/**
 * common-net-1.4.1.jar源码，修改对于日期中文格式的支持，从而解决FTPClient.listFiles()返回为空问题
 */
@SuppressWarnings("all")
public class UnixFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {

    private static Logger logger = LoggerFactory.getLogger(UnixFTPEntryParser.class);
    /**
     * months abbreviations looked for by this parser.  Also used
     * to determine which month is matched by the parser
     */
    private static final String DEFAULT_MONTHS =
            "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";

    static final String DEFAULT_DATE_FORMAT
            = "MMM d yyyy"; //Nov 9 2001

    static final String DEFAULT_RECENT_DATE_FORMAT
            = "MMM d HH:mm"; //Nov 9 20:06

    static final String NUMERIC_DATE_FORMAT
            = "yyyy-MM-dd HH:mm"; //2001-11-09 20:06

    /**
     * Some Linux distributions are now shipping an FTP server which formats
     * file listing dates in an all-numeric format:
     * <code>"yyyy-MM-dd HH:mm</code>.
     * This is a very welcome development,  and hopefully it will soon become
     * the standard.  However, since it is so new, for now, and possibly
     * forever, we merely accomodate it, but do not make it the default.
     * <p>
     * For now end users may specify this format only via
     * <code>UnixFTPEntryParser(FTPClientConfig)</code>.
     * Steve Cohen - 2005-04-17
     */
    public static final FTPClientConfig NUMERIC_DATE_CONFIG =
            new FTPClientConfig(
                    FTPClientConfig.SYST_UNIX,
                    NUMERIC_DATE_FORMAT,
                    null, null, null, null);

    /**
     * this is the regular expression used by this parser.
     * <p>
     * Permissions:
     * r   the file is readable
     * w   the file is writable
     * x   the file is executable
     * -   the indicated permission is not granted
     * L   mandatory locking occurs during access (the set-group-ID bit is
     * on and the group execution bit is off)
     * s   the set-user-ID or set-group-ID bit is on, and the corresponding
     * user or group execution bit is also on
     * S   undefined bit-state (the set-user-ID bit is on and the user
     * execution bit is off)
     * t   the 1000 (octal) bit, or sticky bit, is on [see chmod(1)], and
     * execution is on
     * T   the 1000 bit is turned on, and execution is off (undefined bit-
     * state)
     */
    private static final String REGEX =
            "([bcdlfmpSs-])"
                    + "(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+"
                    + "(\\d+)\\s+"
                    + "(\\S+)\\s+"
                    + "(?:(\\S+)\\s+)?"
                    + "(\\d+)\\s+"

        /*
          numeric or standard format date
        */
                    //问题出在此处，这个匹配只匹配2中形式：
                    //(1)2008-08-03
                    //(2)Jan  9或4月 26
                    //而出错的hp机器下的显示为 8月20日（没有空格分开）
                    //故无法匹配而报错
                    //将下面字符串改为：
                    + "((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S+\\s+\\S+)|(?:\\S+))\\s+"
                    //+ "((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S+\\s+\\S+))\\s+"

        /*
           year (for non-recent standard format)
           or time (for numeric or recent standard format
        */
                    + "(\\d+(?::\\d+)?)\\s+"

                    + "(\\S*)(\\s*.*)";


    /**
     * The default constructor for a UnixFTPEntryParser object.
     *
     * @throws IllegalArgumentException
     *         Thrown if the regular expression is unparseable.  Should not be seen
     *         under normal conditions.  It it is seen, this is a sign that
     *         <code>REGEX</code> is  not a valid regular expression.
     */
    public UnixFTPEntryParser() {
        this(null);
    }

    /**
     * This constructor allows the creation of a UnixFTPEntryParser object with
     * something other than the default configuration.
     *
     * @param config
     *         The {@link FTPClientConfig configuration} object used to
     *         configure this parser.
     * @throws IllegalArgumentException
     *         Thrown if the regular expression is unparseable.  Should not be seen
     *         under normal conditions.  It it is seen, this is a sign that
     *         <code>REGEX</code> is  not a valid regular expression.
     * @since 1.4
     */
    public UnixFTPEntryParser(FTPClientConfig config) {
        super(REGEX);
        configure(config);
    }

    /**
     * Parses a line of a unix (standard) FTP server file listing and converts
     * it into a usable format in the form of an <code> FTPFile </code>
     * instance.  If the file listing line doesn't describe a file,
     * <code> null </code> is returned, otherwise a <code> FTPFile </code>
     * instance representing the files in the directory is returned.
     * <p>
     *
     * @param entry
     *         A line of text from the file listing
     * @return An FTPFile instance corresponding to the supplied entry
     */
    @Override
    public FTPFile parseFTPEntry(String entry) {
        FTPFile file = new FTPFile();
        file.setRawListing(entry);
        int type;
        boolean isDevice = false;
        if (matches(entry)) {
            String typeStr = group(1);
            String hardLinkCount = group(15);
            String usr = group(16);
            String grp = group(17);
            String filesize = group(18);
            String datestr = group(19) + " " + group(20);
            String name = group(21);
            String endtoken = group(22);
            try {
                //file.setTimestamp(super.parseTimestamp(datestr));
                FTPTimestampParserImplExZH Zh2En = new FTPTimestampParserImplExZH();
                file.setTimestamp(Zh2En.parseTimestamp(datestr));
            } catch (ParseException e) {
                //logger.error(e, e);
                //return null;  // this is a parsing failure too.
                //logger.info(entry+":修改日期重置为当前时间");
                file.setTimestamp(Calendar.getInstance());
            }
            // bcdlfmpSs-
            switch (typeStr.charAt(0)) {
                case 'd':
                    type = FTPFile.DIRECTORY_TYPE;
                    break;
                case 'l':
                    type = FTPFile.SYMBOLIC_LINK_TYPE;
                    break;
                case 'b':
                case 'c':
                    isDevice = true;
                    // break; - fall through
                case 'f':
                case '-':
                    type = FTPFile.FILE_TYPE;
                    break;
                default:
                    type = FTPFile.UNKNOWN_TYPE;
            }
            file.setType(type);
            int g = 4;
            for (int access = 0; access < 3; access++, g += 4) {
                // Use != '-' to avoid having to check for suid and sticky bits
                file.setPermission(access, FTPFile.READ_PERMISSION,
                        (!"-".equals(group(g))));
                file.setPermission(access, FTPFile.WRITE_PERMISSION,
                        (!"-".equals(group(g + 1))));

                String execPerm = group(g + 2);
                if (!"-".equals(execPerm) && !Character.isUpperCase(execPerm.charAt(0))) {
                    file.setPermission(access, FTPFile.EXECUTE_PERMISSION, true);
                } else {
                    file.setPermission(access, FTPFile.EXECUTE_PERMISSION, false);
                }
            }
            if (!isDevice) {
                try {
                    file.setHardLinkCount(Integer.parseInt(hardLinkCount));
                } catch (NumberFormatException e) {
                    // intentionally do nothing
                }
            }
            file.setUser(usr);
            file.setGroup(grp);
            try {
                file.setSize(Long.parseLong(filesize));
            } catch (NumberFormatException e) {
                // intentionally do nothing
            }
            if (null == endtoken) {
                file.setName(name);
            } else {
                // oddball cases like symbolic links, file names
                // with spaces in them.
                name += endtoken;
                if (type == FTPFile.SYMBOLIC_LINK_TYPE) {

                    int end = name.indexOf(" -> ");
                    // Give up if no link indicator is present
                    if (end == -1) {
                        file.setName(name);
                    } else {
                        file.setName(name.substring(0, end));
                        file.setLink(name.substring(end + 4));
                    }
                } else {
                    file.setName(name);
                }
            }
            return file;
        } else {
            logger.info("matches(entry) failure:" + entry);
        }
        return null;
    }
    /**
     * Defines a default configuration to be used when this class is
     * instantiated without a {@link  FTPClientConfig  FTPClientConfig}
     * parameter being specified.
     *
     * @return the default configuration for this parser.
     */
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(
                FTPClientConfig.SYST_UNIX,
                DEFAULT_DATE_FORMAT,
                DEFAULT_RECENT_DATE_FORMAT,
                null, null, null);
    }

}