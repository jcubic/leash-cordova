window.Service = function Service(callback) {
    function call(method, args, callback) {
        return cordova.exec(function(response) {
            callback(null, response);
        }, function(error) {
            callback(error);
        }, "Service", method, args || []);
    }
    var service = {};
    call("getMethods", [], function(err, response) {
        if (response.result instanceof Array) {
            response.result.forEach(function(method) {
                service[method] = function() {
                    var args = [].slice.call(arguments);
                    return function(callback) {
                        return call(method, args, function(err, response) {
                            err = err || response.error;
                            if (err) {
                                callback(err);
                            } else {
                                callback(null, response.result);
                            }
                        });
                    };
                };
            });
            callback(service);
        }
    });
};
