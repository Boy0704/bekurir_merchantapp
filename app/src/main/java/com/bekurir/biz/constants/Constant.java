package com.bekurir.biz.constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ourdevelops Team on 10/23/2020.
 */

public class Constant {

    private static final String BASE_URL = "http://demo.ayogolive.com/bekuriraja/";
    public static final String FCM_KEY = "AAAAjRCcj98:APA91bELUvC5mFTq2XC17aru33KsYtsYM8rCrpwEyClbUQQF6DYPJkGEtiJ5BfsyBryNDsgwMsT_ToaE8hzBHvCca9zZsz48_19Onm3qf61aN0oFChRhuFBV-elRDlHNKkFS4ajxUKdP";
    public static final String CONNECTION = BASE_URL + "api/";
    public static final String IMAGESDRIVER = BASE_URL + "images/driverphoto/";
    public static final String IMAGESMERCHANT = BASE_URL + "images/merchant/";
    public static final String IMAGESBANK = BASE_URL + "images/bank/";
    public static final String IMAGESITEM = BASE_URL + "images/itemphoto/";
    public static final String IMAGESPELANGGAN = BASE_URL + "images/customer/";


    public static String USERID = "uid";

    public static String PREF_NAME = "pref_name";

    public static int permission_camera_code = 786;
    public static int permission_write_data = 788;
    public static int permission_Read_data = 789;
    public static int permission_Recording_audio = 790;

    public static SimpleDateFormat df =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static String versionname = "1.0";

}
