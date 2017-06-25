package pl.jcubic.leash;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReflectService extends CordovaPlugin {

    protected Method getMethod(String name) {
        Class aClass = this.getClass();
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public String[] getMethods() {
        Class aClass = this.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        List<String> list = new ArrayList<String>();
        int len = methods.length;
        String[] result = new String[len];
        for (Method method : methods) {
            String name = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && !name.equals("initialize")) {
                list.add(name);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected String[] jsonArrayToString(JSONArray args) {
        String[] result = {};
        try {
            int len = args.length();
            result = new String[len];
            for (int i=0; i<len; ++i) {
                result[i] = args.getString(i);
            }
        } catch (JSONException e) {}
        return result;
    }

    protected String[] getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().split("\n");
    }

    @Override
    public boolean execute(String action,
                           JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {
        if (!action.equals("execute")) {
            final Method method = this.getMethod(action);
            if (method == null) {
                return false;
            }
            final Object[] arguments = this.jsonArrayToString(args);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    JSONObject json;
                    Object result;
                    try {
                        result = method.invoke(ReflectService.this, arguments);
                        json = new JSONObject();
                        if (result instanceof Object[] ||
                            result instanceof Collection<?>) {
                            json.put("result", new JSONArray(result));
                        } else if (result instanceof Map<?,?>) {
                            json.put("result", new JSONObject((Map<?,?>)result));
                        } else {
                            json.put("result", result);
                        }
                        json.put("error", null);
                        callbackContext.success(json);
                    } catch(JSONException e) {
                        callbackContext.success();
                    } catch(Exception e) {
                        try {
                            json = new JSONObject();
                            JSONObject error = new JSONObject();
                            error.put("error", "Exception");
                            error.put("code", 200);
                            error.put("message", e.getMessage());
                            String[] trace = ReflectService.this.getStackTrace(e);
                            error.put("trace", new JSONArray(trace));
                            json.put("error", error);
                            json.put("result", null);
                            callbackContext.success(json);
                        } catch(JSONException ee) {
                            callbackContext.success();
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }
}
