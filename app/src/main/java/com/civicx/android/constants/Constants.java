package com.civicx.android.constants;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Constants {

    public final static String OBJECT = "object";
    public final static String OBJECT_ID = "objectID";
    public final static String PRIVACY_POLICY = "https://civicx.com/privacy-policy/";
    public final static int POST_SPOTLIGHT = 0;
    public final static int POST_BILL = 1;
    public final static int POST_EVENT = 2;
    public final static int POST_NOTICE = 3;
    public final static int POST_SURVEY = 4;
    public final static String KEY_STORE_REFERENCE = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    public final static String LOGO_PICTURE = "https://firebasestorage.googleapis.com/v0/b/civic-voices.appspot.com/o/users%2Flogo.png?alt=media&token=00c7b5a3-c986-40c7-9939-d0f53c441f82";
    public final static String PIC = "pic";
    public final static String NAME = "name";
    public final static String EMAIL = "email";
    public final static String AGE = "AGE";
    public final static String COUNTY = "County";
    public final static String SUB_COUNTY = "Sub County";
    public final static String CONSTITUENCY = "Constituency";
    public final static String WARD = "Ward";
    public final static String TOKEN = "token";
    public final static String GENDER = "gender";
    public final static String STARS = "stars";
    public final static String COUNTRY = "country";
    public final static String VERIFICATION = "verification";
    public final static String DISABILITY = "disability";
    public final static String GREEN = "#78ca28";
    public final static String NATIONAL_ASSEMBLY = "National Assembly";
    public final static String COUNTY_ASSEMBLY = "County Assembly";
   public final static String SENATE = "Senate";

    public final static String BILL = "BILL";
    public final static String NOTICE = "NOTICE";
    public final static String TRACK_RECORD = "TRACK_RECORD";
    public final static String EVENT = "EVENT";
    public final static String SURVEY = "SURVEY";
    public final static String ACTIVITY = "ACTIVITY";

    public final static String TYPE = "Type";

    public final static String NGO = "NGO";
    public final static String NATIONAL = "National";
    public final static String MINISTRY = "Ministry";

   public final static String TOKEN_NOTIFICATION = "notifications";
    public final static String GROUPS_TOKENS = "grouptokens";
    public final static String USERS = "users";
    public final static String IMPACT = "impact";
    public final static String REPORTED = "reported";
    public final static String UPVOTES = "upvotes";
    public final static String DOWNVOTES = "downvotes";
    public final static String CATEGORIES = "categories";
    public static final String COMMENTS = "comments";

    public static final String SUBMISSIONS = "submissions";

    public static final String SURVEYS = "surveys";

    public static final String FORM = "form";

    public static final String RADIO = "radio";
    public static final String QUERIES = "queries";
    public static final String SUMMARY = "summary";
    public static final String APPROVED = "approved";
    public final static String POSTS = "posts";
    public final static String REPLY = "reply";
    public final static String BRANDS = "brands";
    public final static String SUBSCRIBERS = "subscribers";


}
