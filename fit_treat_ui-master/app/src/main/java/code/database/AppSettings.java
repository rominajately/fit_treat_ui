package code.database;

import android.app.Activity;

public final class AppSettings extends OSettings {
    public static final String PREFS_MAIN_FILE = "PREFS_FITTREAT_FILE";
    public static final String accessToken = "accessToken";
    public static final String userId = "userId";
    public static final String from = "from";
    public static final String otcName = "otcName";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    public static final String email = "email";
    public static final String dateOfBirth = "dateOfBirth";
    public static final String weight = "weight";
    public static final String height = "height";
    public static final String bmi = "bmi";
    public static final String medicalCondition = "medicalCondition";
    public static final String foodPreference = "foodPreference";
    public static final String targetWeight = "targetWeight";
    public static final String targetDate = "targetDate";
    public static final String targetCalories = "targetCalories";
    public static final String userPhoto = "userPhoto";
    public static final String role = "role";
    public static final String age = "age";
    public static final String gender = "gender";
    public static final String weightUnit = "weightUnit";
    public static final String heightUnit = "heightUnit";
    public static final String msgCount = "msgCount";
    public static final String unreadCount = "unreadCount";
    public static final String json = "json";
    public static final String profile = "profile";

    public AppSettings(Activity mActivity) {
        super(mActivity);
    }
}
