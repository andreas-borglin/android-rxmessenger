/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aevi.android.rxmessenger.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.aevi.android.rxmessenger.ChannelServer;

import static com.aevi.android.rxmessenger.MessageConstants.CHANNEL_WEBSOCKET;

final class ChannelServerFactory {
    private ChannelServerFactory() {
    }

    @NonNull
    static ChannelServer getChannelServer(Context context, String channelType, String serviceComponentName) {
        ChannelServer channelServer;
        switch (channelType) {
            case CHANNEL_WEBSOCKET:
                channelServer = new WebSocketChannelServer(context, serviceComponentName);
                break;
            default:
                channelServer = new MessengerChannelServer(serviceComponentName);
                break;
        }
        return channelServer;
    }
}
