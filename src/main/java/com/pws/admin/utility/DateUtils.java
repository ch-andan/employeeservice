package com.pws.admin.utility;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Vinayak M
 * @Date 09/01/23
 */
public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static Date getUtilDateFromString(String strDate) {
        if (!StringUtils.isEmpty(strDate))
            try {
                return DATE_FORMAT.parse(strDate);
            } catch (ParseException e) {

            }
        return null;
    }




}
