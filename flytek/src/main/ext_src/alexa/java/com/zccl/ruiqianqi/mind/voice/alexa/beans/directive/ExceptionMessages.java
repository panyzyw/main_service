package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class ExceptionMessages extends BaseDirective {
    public Payload payload;

    public ExceptionMessages(){
        header.namespace = "System";
        header.name = "Exception";
    }

    public class Payload{
        /**
         * An exception code returned when an error is encountered
         * Code	                         HTTP Status Code	    Description
         * INVALID_REQUEST_EXCEPTION	    400	                The request was malformed.
         * UNAUTHORIZED_REQUEST_EXCEPTION	403	                The request was not authorized.
         * THROTTLING_EXCEPTION	            429	                Too many requests to the Alexa Voice Service.
         * INTERNAL_SERVICE_EXCEPTION	    500	                Internal service exception.
         * N/A	                            503	                The Alexa Voice Service is unavailable
         */
        public String code;

        /**
         * Additional details for logging and troubleshooting. This is an optional parameter for AVS and
         * may not be included in the JSON (or appear as null)
         */
        public String description;
    }
}
