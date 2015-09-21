package com.unimelb.serena;

/**
 * Created by xialeizhou on 9/21/15.
 */
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static BigInteger bigVal = null;
    private static int intVal = 0;

    public static BigInteger string2bigint(String str) {
        bigVal = new BigInteger(str);
        return bigVal;
    }
    public static int string2int(String str) {
        intVal = Integer.parseInt(str);
        return intVal;
    }

    public static String getCurrTime() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }
}