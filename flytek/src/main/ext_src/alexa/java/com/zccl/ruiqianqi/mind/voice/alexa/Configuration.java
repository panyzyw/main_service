/**
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * You may not use this file except in compliance with the License. A copy of the License is located the "LICENSE.txt"
 * file accompanying this source. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.zccl.ruiqianqi.mind.voice.alexa;

import okhttp3.Request;

public class Configuration {

    /**
     * 云端发音人 aisxa catherine
     * 本地发音人 jiajia xiaofeng xiaoyan nannan
     * 语记发音人 ""
     */
    public static final String SPEAKER_NAME = "jiajia";

    // 设备编号
    // INSERT YOUR PRODUCT ID FROM AMAZON DEVELOPER CONSOLE
    public static final String PRODUCT_ID = "my_demo";

    /**
     * AVS exposes an HTTP/2 service and expects multipart messages encoded for HTTP/2. The following endpoints are supported:
     * North America	US	             https://avs-alexa-na.amazon.com
     * Europe	        UK, Germany	     https://avs-alexa-eu.amazon.com
     */
    protected static final String END_POINT = "https://avs-alexa-na.amazon.com";

    /**
     * Maintaining a connection with AVS requires two things:
     * A、Establishing the downchannel stream
     * B、Synchronizing your product’s component states with AVS
     * (SpeechRecognizer, AudioPlayer, Alerts, Speaker, SpeechSynthesizer)
     */
    /**
     * To establish a downchannel stream your client must make a GET request to /{{API version}}/directives
     * within 10 seconds of opening the connection with AVS. The request should look like this:
     *
     * :method = GET
     * :scheme = https
     * :path = /{{API version}}/directives
     * authorization = Bearer {{YOUR_ACCESS_TOKEN}}
     *
     * Following a successful request, the downchannel stream will remain open in a half-closed state from the client and
     * open from AVS for the life of the connection. It is not uncommon for there to be long pauses between cloud-initiated directives.
     */
    public static final  String DIRECTIVE_URL = Configuration.END_POINT + "/v20160207/directives";

    /**
     * After establishing the downchannel stream, your client must synchronize it’s components’ states with AVS.
     * This requires making a POST request to /{{API version}}/events on a new event stream on the existing connection (Note: Do not open a new connection).
     * This event stream should be closed when your client receives a response (directive).
     * The following is an example SynchronizeState event
     */
    public static final  String EVENT_URL = Configuration.END_POINT + "/v20160207/events";


    /**
     * 统一为请求添加头信息
     * @param access_token   TOKEN
     * @return
     */
    public static Request.Builder addGetHeaders(String access_token) {
        Request.Builder builder = new Request.Builder()
                .addHeader("authorization", "Bearer " + access_token);
        return builder;
    }

    /**
     * 统一为请求添加头信息
     * @param access_token   TOKEN
     * @param boundary_term  分隔线
     * @return
     */
    public static Request.Builder addPostHeaders(String access_token, String boundary_term) {
        Request.Builder builder = new Request.Builder()
                .addHeader("authorization", "Bearer " + access_token)
                .addHeader("content-type", "multipart/form-data; boundary=" + boundary_term);
        return builder;
    }
}
