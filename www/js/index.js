/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        if (id == 'deviceready') {
            keyboardeventKeyPolyfill.polyfill();
            var token = 'TOKEN';
            var defer = $.Deferred();
            var options = {
                leash: {
                    service: defer.promise(),
                    disable: ['python']
                }
            };
            function always(value) {
                return function() {
                    return function(callback) {
                        if (callback) {
                            callback(null, value);
                        }
                    };
                };
            }
            window.Service(function(service) {
                service.get_settings = always({
                    show_messages: false,
                    home: '/',
                    server: 'android'
                });
                service.dir = always({
                    'files': [],
                    'dirs': [],
                    'execs': []
                });
                service.valid_token = always(true);
                service.installed = always(true);
                defer.resolve(service);
                service.jargon_list = always([]);
                $('body').leash(options).then(function(leash) {
                    var username = 'leash';
                    leash.terminal.autologin(username, token);
                    leash.set_login(username);
                });
            });
        }
        console.log('Received Event: ' + id);
    }
};

app.initialize();
