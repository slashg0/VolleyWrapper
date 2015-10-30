package com.slashg.volleywrapper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by SlashG on 28-10-2015.
 */
public class EasyRequest {

    /**
     * ==============
     * Easy Request
     * =============
     * <p/>
     * This class extends JsonRequest and simplifies usage and improves parameterization
     * by adding standard request params (if any) configured in 'Spine'.
     * <p/>
     * Also, this class ensures that every request contains a url, an onResponse callback,
     * params and an onError callback
     * <p/>
     * If any of these are found to be null, the request is discarded
     */

    String url;
    HashMap<String, String> params;
    JSONParamRunnable onResponseCallback, onErrorCallback;
    ResponseListenerExt responseListener;
    Response.ErrorListener errorListener;
    int requestMethod;


    public static final int TIMEOUT_IN_MS = 3000, REQUEST_RETRIES = 2;

    private boolean isDiscarded = false;

    public EasyRequest(int requestMethod, String url, HashMap<String, String> params, final JSONParamRunnable onResponseCallback, final JSONParamRunnable onErrorCallback) {
        /**
         * Contructor inits necessary params for the request
         *
         * If any of the passed params is null, the contructor marks the request to be discarded
         */

        if (url == null || url.length() <= 0) {
            Spine.log("EasyRequest::~constructor() url passed is null/zero-length. discarding request");
            discardRequest();
            return;
        }

        if (params == null || params.size() <= 0) {
            Spine.log("EasyRequest::~constructor() params passed is null/zero-length. discarding request");
            discardRequest();
            return;
        }

        if (onResponseCallback == null) {
            Spine.log("EasyRequest::~constructor() onResultCallback passed is null/zero-length. discarding request");
            discardRequest();
            return;
        }

        if (onErrorCallback == null) {
            Spine.log("EasyRequest::~constructor() onErrorCallback passed is null/zero-length. discarding request");
            discardRequest();
            return;
        }

        this.requestMethod = requestMethod;
        this.url = url;
        this.params = params;
        this.onResponseCallback = onResponseCallback;
        this.onErrorCallback = onErrorCallback;

        responseListener = new ResponseListenerExt(this.onResponseCallback, this.onErrorCallback);

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error JSON Body to be generated here
                onErrorCallback.run(JSONFactory.getJSONFromErrorCode(JSONFactory.ERROR_CODE_UNKNOWN));
                error.printStackTrace();
            }
        };

    }

    private void discardRequest() {
        /**
         * Method marks the flag isDiscarded true
         *
         * it denotes that the request has incomplete params and needn't be processed
         */
        isDiscarded = true;
    }

    public JsonRequest getProcessedRequest() {
        /**
         * method used to generate a JsonRequest from the params init'ed in this object
         * if this request was discarded, the method returns null
         */

        if (!isDiscarded) {
            JsonRequest req = new JsonRequestDef(requestMethod, url, null, responseListener, errorListener);
            return req;
        }
        return null;

    }
}

class JsonRequestDef extends JsonRequest<JSONObject> {
    public JsonRequestDef(int arg0, String arg1, String arg2, Response.Listener arg3, Response.ErrorListener arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
        // Configure RetryPolicy with timeout and retry count constants defined atop this file
        setRetryPolicy(new DefaultRetryPolicy(EasyRequest.TIMEOUT_IN_MS, EasyRequest.REQUEST_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        /**
         * Customized NetworkResponse parser converts byte[] data to JSONString,
         * This JSONString to JSONObject which is returned as result
         */

        Response<JSONObject> result = null;
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Spine.log("JsonRequestDef::parseNetworkResponse() data = " + jsonString);

            JSONObject json = new JSONObject(jsonString);
            result = Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            e.printStackTrace();
            JSONObject json = JSONFactory.getJSONFromErrorCode(JSONFactory.ERROR_CODE_CORRUPT_JSON);
            result = Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException uee)
        {
            uee.printStackTrace();
            JSONObject json = JSONFactory.getJSONFromErrorCode(JSONFactory.ERROR_CODE_UNKNOWN);
            result = Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        }
        return result;
    }

}

class ResponseListenerExt implements Response.Listener<JSONObject> {

    /**
     * Extension class for Reponse Listener for Volley requests
     * <p/>
     * This extension handles additional invalid responses (null, empty JSON, etc.)
     * and returns apt JSON Objects with errors
     * <p/>
     * NOTE : This class expects not-null response and error listneners
     *
     * @param response
     */

    JSONParamRunnable responseListener, errorListner;

    ResponseListenerExt(JSONParamRunnable responseListener, JSONParamRunnable errorListener) {
        /**
         * constructor fetching references for both response and error listeners
         */


        if (errorListener == null) {
            throw new IllegalArgumentException("Error Listener can't be null");
        }
        if (responseListener == null) {
            throw new IllegalArgumentException("Response Listener can't be null");
        }

        this.errorListner = errorListener;
        this.responseListener = responseListener;

    }

    @Override
    public void onResponse(JSONObject response) {
        if (response == null) {
            //Send null response JSON to errorListener
            errorListner.run(JSONFactory.getJSONFromErrorCode(JSONFactory.ERROR_CODE_NULL_RESPONSE));
        } else if (response.length() <= 0) {
            //Send empty response JSON to errorListener
            errorListner.run(JSONFactory.getJSONFromErrorCode(JSONFactory.ERROR_CODE_EMPTY_JSON));
        } else {
            // Send actual (valid) JSON to responseListener
            // Add response code -200
            try {
                JSONObject responseWithCode = response;
                responseWithCode.put(JSONKeys.KEY_JSON_RESPONSE_CODE, JSONFactory.SUCCESS_CODE);
                responseListener.run(responseWithCode);
            } catch (JSONException je) {
                // If adding success code to JSON response fails, send original response as is
                je.printStackTrace();
                responseListener.run(response);
            }
        }
    }
}