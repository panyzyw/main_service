package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class ExceptionEncounteredMsg extends BaseEvent{

    public Payload payload = new Payload();

    public ExceptionEncounteredMsg(){
        header.namespace = "System";
        header.name = "ExceptionEncountered";
    }

    public class Payload{
        /**
         * When unable to execute a directive,
         * your client must return the directive to AVS as a string.
         */
        public String unparsedDirective;
        public Error error = new Error();
    }

    public class Error{
        /**
         * An error your client must return to AVS when unable to execute a directive
         *
         * UNEXPECTED_INFORMATION_RECEIVED	The directive sent to your client was malformed or the payload does not conform to the directive specification.
         * INTERNAL_ERROR	An error occurred while the device was handling the directive and the error does not fall into the specified categories
         */
        public String type;

        /**
         * Additional error details for logging and troubleshooting
         */
        public String message;
    }

}
