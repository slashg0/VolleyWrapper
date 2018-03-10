package xyz.slashg.spine;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * ================
 *  Volley Wrapper
 * ================
 *
 * This package contains a wrapper class structure for Volley requests
 * This provides simplified end points to queue HTTP requests in a single request queue
 * The methods can be statically accessed throughout the app importing it
 *
 * Additional functionality of tags and priorities are provided for better control
 *
 * --
 * Created by SlashG on 27-10-2015.
 */

public class Spine {

    public static final int PRIORITY_TIER_1 = 1, PRIORITY_TIER_2 = 2, PRIORITY_TIER_3 = 3;
    private static ArrayList<String> tags;
    private static RequestQueue requestQueue;

    private static boolean isLogEnabled = true;
    private static boolean isStarted;

    public static void initialize(Context context) {
        /**
         * Method to initialize all elements of the Volley Wrapper Spine
         * All initializations are carried out here, without fail.
         *
         * This ensures that no member object is null in the Spine
         */

        initTagList();                              //Initialize container ArrayList for tags
        initJobQueue(context);                             //Initialize request queue
        setIsStarted(false);                        //Initialize isStarted flag to false
    }

    private static void initTagList() {

        /**
         * Initializes ArrayList of tags
         */

        tags = new ArrayList<>();
    }

    private static void initJobQueue(Context context) {

        /**
         * Initializes requestQueue if necessary
         */

        requestQueue = Volley.newRequestQueue(context);
    }

    private static void startRequests()
    {
        /**
         * Method starts requestQueue whether or not it is running
         */

        requestQueue.start();
        setIsStarted(true);
    }

    private static void stopAllRequests() {

        /**
         * Method stops requestQueue if it is running
         */

        if (isStarted()) {
            requestQueue.stop();
            setIsStarted(false);
        }
    }

    private static void startRequestsIfNeeded()
    {
        /**
         * Method starts requestQueue if it isn't already running
         */

        if(!isStarted()) {
            startRequests();
        }
    }

    public static void log(String message){

        /**
         * This method is a standard method for logging events
         *
         * the log calls are discarded if boolean flag 'isLogEnabled' is false
         */

        if(isLogEnabled)
        {
            System.out.println(message);
        }
    }

    public static void enableOrDisableLog(boolean enable)
    {
        /**
         * Method sets value for flag 'isLogEnabled' to 'enable'
         */

        isLogEnabled = enable;

    }

    public static boolean isStarted() {

        /**
         * Method to check if the requestQueue is started or not
         */

        return isStarted;
    }

    private static void setIsStarted(boolean isStarted) {

        /**
         * Method sets isStarted flag to the passed value
         */

        Spine.isStarted = isStarted;
    }

    public static boolean registerTag(String tag) {

        /**
         * Method to register a new tag if it doesn't already exist.
         * All tags are invariably in uppe-case
         *
         * If a tag is already there in the list, this method returns false
         *
         * NOTE: Tags are an optional parameter that can provide greater grip on the requests
         */

        if (tag != null && tag.length() > 0) {   // Check the validity of the input

            String tagUpperCase = tag.toUpperCase(Locale.US);
            if (tags.contains(tagUpperCase)) {
                // Check if the tag already exists
                return false;
            }

            tags.add(tagUpperCase);
        }
        return true;
    }

    public static void queueRequest(EasyRequest newRequest)
    {
        /**
         * Method used to add requests to the request queue
         */
        JsonRequest req = newRequest.getProcessedRequest();
        if(req != null) {
            requestQueue.add(newRequest.getProcessedRequest());
            startRequestsIfNeeded();

        }
        else
        {
            log("Spine::queueRequest() processed request is null");
        }
    }

    public static void queueRequest(final Request newRequest, final Runnable requestCompleteAction)
    {
        /**
         * Method used to add requests to the request queue
         * and register callback
         */
        requestQueue.add(newRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if (request.equals(newRequest)) {
                    /**
                     * When the request queued in this call is completed, execute requestCompleteAction
                     */
                    requestCompleteAction.run();
                }
            }
        });
        startRequestsIfNeeded();
    }

}