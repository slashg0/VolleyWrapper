package com.slashg.volleywrapper;

import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 * ==============
 *  JSON Factory
 * ==============
 *
 * This class contains standard JSON Objects for use with Spine.
 *
 * For cases where the APIs do not send valid JSON objects, this class
 * may be referred to with the response/error code
 *
 * Created by SlashG on 29-10-2015.
 */

public class JSONFactory {

    public static final int SUCCESS_CODE = -200;
    public static final int ERROR_CODE_NULL_RESPONSE = -501, ERROR_CODE_NETWORK_FAILED = -502, ERROR_CODE_TIMEOUT = -503, ERROR_CODE_EMPTY_JSON = -504, ERROR_CODE_CORRUPT_JSON = -505, ERROR_CODE_UNKNOWN = -510;
    public static final String  JSON_NULL_RESPONSE = "{\"response_code\" : " + ERROR_CODE_NULL_RESPONSE + ",\"error_message\" : \"ERROR_CODE_NULL_RESPONSE | Server Sent 'null' response\"}",
                                    JSON_NETWORK_FAILED = "{\"response_code\" : " + ERROR_CODE_NETWORK_FAILED + ",\"error_message\" : \"ERROR_CODE_NETWORK_FAILED | Network failed\"}",
                                    JSON_TIMEOUT = "{\"response_code\" : " + ERROR_CODE_TIMEOUT + ",\"error_message\" : \"ERROR_CODE_TIMEOUT | Request timed out\"}",
                                    JSON_EMPTY_JSON = "{\"response_code\" : " + ERROR_CODE_EMPTY_JSON + ",\"error_message\" : \"ERROR_CODE_EMPTY_JSON | Server sent empty JSON\"}",
                                    JSON_CORRUPT_JSON = "{\"response_code\" : " + ERROR_CODE_CORRUPT_JSON + ",\"error_message\" : \"ERROR_CODE_CORRUPT_JSON | Server sent corrupt JSON\"}",
                                    JSON_UNKNOWN = "{\"response_code\" : " + ERROR_CODE_UNKNOWN + ",\"error_message\" : \"ERROR_CODE_UNKNOWN | Unknown error\"}";


    private static HashMap<Integer,JSONObject> hash;

    private static void initHashIfNeeded()
    {
        /**
         * Method maps error codes to corresponding responses
         * if hash is null/empty
         */

        if(hash == null || hash.size() <= 0)
        {
            try {
                // hash needs to be initialized
                hash = new HashMap<>();
                hash.put(ERROR_CODE_EMPTY_JSON, new JSONObject(JSON_EMPTY_JSON));
                hash.put(ERROR_CODE_NETWORK_FAILED, new JSONObject(JSON_NETWORK_FAILED));
                hash.put(ERROR_CODE_NULL_RESPONSE, new JSONObject(JSON_NULL_RESPONSE));
                hash.put(ERROR_CODE_TIMEOUT, new JSONObject(JSON_TIMEOUT));
            }catch(Exception e)
            {
                //TODO Handle exception better
                e.printStackTrace();
            }
        }
    }

    public static JSONObject getJSONFromErrorCode(int errorCode)
    {
        initHashIfNeeded();
        if(!hash.containsKey(errorCode))
        {
            errorCode = ERROR_CODE_UNKNOWN;
        }
        return hash.get(errorCode);
    }

}
