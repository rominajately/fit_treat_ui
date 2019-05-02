package code.utils;

import code.database.AppSettings;

/*
 *
 * API/Service endpoints being used in the application to communicate with the Server and the DB
 *
 * */

public class AppUrls {

    //Live URL
    //public static final String baseUrl = "https://fittreat-server.herokuapp.com/";
    public static final String baseUrl = "https://fitpythonserver.herokuapp.com/";
    public static final String login = baseUrl+"auth/login";
    public static final String register = baseUrl+"auth/register";
    public static final String logout = baseUrl+"Logout";
    public static final String inbox = baseUrl+"api/reloadMessages/"+AppSettings.getString(AppSettings.userId);
    public static final String readMessage = baseUrl+"api/readMessage/"+AppSettings.getString(AppSettings.userId)+"/";
    public static final String getMeals = baseUrl+"api/getMeals/"+AppSettings.getString(AppSettings.userId);
    public static final String initialSymptoms = baseUrl+"api/initialSymptoms";
    public static final String targetWeight = baseUrl+"api/targetWeight";
    public static final String updateProfile = baseUrl+"api/updateProfile";
    public static final String photoUpdate = baseUrl+"api/photoUpdate";
    public static final String searchSymptoms = baseUrl+"api/searchSymptoms/";
    public static final String getAppData = baseUrl+"api/getAppData";
    public static final String sendMsgToAdmin = baseUrl+"api/sendMsgToAdmin";
    public static final String filterMeals = baseUrl+"api/filterMeals/";
    public static final String forgotPassword = baseUrl+"api/changePassword/";

}
