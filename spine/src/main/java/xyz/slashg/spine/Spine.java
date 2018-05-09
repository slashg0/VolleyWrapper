package xyz.slashg.spine;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ================
 * Volley Wrapper
 * ================
 * <p>
 * This package contains a wrapper class structure for Volley requests
 * This provides simplified end points to queue HTTP requests in a single request queue
 * The methods can be statically accessed throughout the app importing it
 * <p>
 * Additional functionality of tags and priorities are provided for better control
 * <p>
 * --
 * Created by SlashG on 27-10-2015.
 */

public class Spine
{

	public static final int PRIORITY_TIER_1 = 1, PRIORITY_TIER_2 = 2, PRIORITY_TIER_3 = 3;
	private static final String TAG = Spine.class.getSimpleName();
	private static ArrayList<String> tags;
	private static RequestQueue requestQueue;
	private static boolean isLogEnabled = true;
	private static boolean isStarted;

	/**
	 * Method to initialize all elements of the Volley Wrapper Spine
	 * All initializations are carried out here, without fail.
	 * <p>
	 *
	 * @param context Context instance
	 *                This ensures that no member object is null in the Spine
	 */

	public static void initialize(Context context) throws CouldNotInitException
	{
		try
		{
			initTagList();                              //Initialize container ArrayList for tags
			initJobQueue(context);                             //Initialize request queue
			setIsStarted(false);                        //Initialize isStarted flag to false
		} catch (Exception e)
		{
			CouldNotInitException couldNotInitException = new CouldNotInitException();
			couldNotInitException.initCause(e);
			throw couldNotInitException;
		}
	}

	/**
	 * Method to initialize all elements of the Volley Wrapper Spine
	 * All initializations are carried out here, without fail.
	 * <p>
	 * This ensures that no member object is null in the Spine
	 *
	 * @param context   Context instance
	 * @param hurlStack To allow custom {@code HurlStack} (to overcome error on OS below API19)
	 */
	public static void initialize(Context context, HurlStack hurlStack) throws CouldNotInitException
	{

		try
		{
			initTagList();                              //Initialize container ArrayList for tags
			initJobQueue(context, hurlStack);                             //Initialize request queue
			setIsStarted(false);                        //Initialize isStarted flag to false
		} catch (Exception e)
		{
			CouldNotInitException couldNotInitException = new CouldNotInitException();
			couldNotInitException.initCause(e);
			throw couldNotInitException;
		}
	}

	/**
	 * Initializes ArrayList of tags
	 */
	private static void initTagList()
	{


		tags = new ArrayList<>();
	}

	/**
	 * Initializes requestQueue if necessary
	 *
	 * @param context Context instance
	 */
	private static void initJobQueue(Context context)
	{


		requestQueue = Volley.newRequestQueue(context);
	}

	/**
	 * Initializes requestQueue if necessary
	 *
	 * @param context   Context instance
	 * @param hurlStack To allow custom {@code HurlStack} (to overcome error on OS below API19)
	 */
	private static void initJobQueue(Context context, HurlStack hurlStack)
	{


		requestQueue = Volley.newRequestQueue(context, hurlStack);
	}

	/**
	 * Method starts requestQueue whether or not it is running
	 */
	private static void startRequests()
	{

		requestQueue.start();
		setIsStarted(true);
	}

	/**
	 * Method stops requestQueue if it is running
	 */
	private static void stopAllRequests()
	{


		if (isStarted())
		{
			requestQueue.stop();
			setIsStarted(false);
		}
	}

	/**
	 * Method starts requestQueue if it isn't already running
	 */
	private static void startRequestsIfNeeded()
	{


		if (!isStarted())
		{
			startRequests();
		}
	}

	/**
	 * This method is a standard method for logging events
	 * <p>
	 * the log calls are discarded if boolean flag 'isLogEnabled' is false
	 *
	 * @param message The message to log
	 */
	public static void log(String message)
	{


		if (isLogEnabled)
		{
			Log.d(TAG, "log: " + message);
		}
	}

	/**
	 * Method sets value for flag 'isLogEnabled' to 'enable'
	 *
	 * @param enable The value to set
	 */
	public static void enableOrDisableLog(boolean enable)
	{

		isLogEnabled = enable;

	}

	/**
	 * Method to check if the requestQueue is started or not
	 *
	 * @return Whether Spine is started or not
	 */
	public static boolean isStarted()
	{


		return isStarted;
	}

	/**
	 * Method sets isStarted flag to the passed value
	 *
	 * @param isStarted The value to set
	 */
	private static void setIsStarted(boolean isStarted)
	{


		Spine.isStarted = isStarted;
	}

	/**
	 * Method to register a new tag if it doesn't already exist.
	 * All tags are invariably in upper-case
	 * <p>
	 * NOTE: Tags are an optional parameter that can provide greater grip on the requests
	 *
	 * @param tag The string to register if not already registered. Will be forced to Upper Case
	 * @return 'true' if tag successfully registered. If  the tag is already there in the list, this method returns 'false'.
	 */
	public static boolean registerTag(String tag)
	{


		if (tag != null && tag.length() > 0)
		{   // Check the validity of the input

			String tagUpperCase = tag.toUpperCase(Locale.US);
			if (tags.contains(tagUpperCase))
			{
				// Check if the tag already exists
				return false;
			}

			tags.add(tagUpperCase);
		}
		return true;
	}

	/**
	 * Method used to an {@link EasyRequest} to the request queue
	 *
	 * @param newRequest The request to queue
	 */
	public static void queueRequest(EasyRequest newRequest)
	{
		JsonRequest req = newRequest.getProcessedRequest();
		if (req != null)
		{
			requestQueue.add(newRequest.getProcessedRequest());
			startRequestsIfNeeded();

		}
		else
		{
			log("Spine::queueRequest() processed request is null");
		}
	}

	/**
	 * Method used to add requests to the request queue
	 * and register callback
	 *
	 * @param newRequest            The request to queue
	 * @param requestCompleteAction A simple no-parameter callback to notify completion of the request. This is apart from the callback registered in newRequest itself
	 */
	public static void queueRequest(final Request newRequest, final Runnable requestCompleteAction)
	{

		requestQueue.add(newRequest);
		requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>()
		{
			@Override
			public void onRequestFinished(Request<Object> request)
			{
				if (request.equals(newRequest))
				{
					/**
					 * When the request queued in this call is completed, execute requestCompleteAction
					 */
					requestCompleteAction.run();
				}
			}
		});
		startRequestsIfNeeded();
	}

	/**
	 * Method used to add requests to the request queue
	 * without registering a callback
	 *
	 * @param newRequest The request to queue
	 */
	public static void queueRequest(final Request newRequest)
	{

		requestQueue.add(newRequest);
		startRequestsIfNeeded();
	}

	/**
	 * Exception thrown if Spine init fails.
	 *
	 * @author SlashG
	 * @since <nextVersion/>
	 */
	public static class CouldNotInitException extends Exception
	{

	}

}