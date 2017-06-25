package pl.jcubic.leash;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.ArrayList;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.InputStream;

import java.sql.Timestamp;

import android.text.TextUtils;

public class Service extends ReflectService {

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }
    public String echo(String input) {
        if (input.equals("ping")) {
            return "pong";
        } else {
            return null;
        }
    }

    public String hash(String input) {
        byte[] bytes = input.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return this.hex(md.digest(bytes));
        }
        catch(NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String hex(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public String login(String username, String password) {
        if (username.equals("foo") && password.equals("bar")) {
            return this.hash("foobar");
        } else {
            return null;
        }
    }

    public HashMap shell(String token, String command, String path) throws IOException, InterruptedException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String marker = "XXXX" + this.hash(timestamp.toString());
        command = command.replace("&*$", " >/dev/null & echo $!");
        String pre = "cd " + path + ";";
        String post = "\necho -n \"" + marker + "\"; pwd";
        command = pre + command + post;
        String commandResult = this.exec(command);
        HashMap result = new HashMap();
        if (command.matches(".*>/dev/null & echo $!$")) {
            result.put("output", "[1] " + commandResult);
            result.put("cwd", path);
        } else {
            String[] output = commandResult.split(marker);
            String cwd;
            if (output.length == 2) {
                cwd = output[1].replace("\n$", "");
            } else {
                cwd = path;
            }
            result.put("cwd", cwd);
            result.put("output", output[0].replace(post, ""));
        }
        return result;
    }

    public HashMap command(String token, String command, String path) {
        HashMap result = new HashMap();
        result.put("Foo", 10);
        result.put("Bar", "Hello");
        return result;
    }

    private String[] read(BufferedReader reader) throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        String line = "";
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }
        return output.toArray(new String[output.size()]);
    }
    
    private String[] readStream(InputStream input) throws IOException {
        InputStreamReader is = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(is);
        return this.read(reader);
    }

    public String exec(String command) throws IOException, InterruptedException {
        Process bash = Runtime.getRuntime().exec("sh");
        DataOutputStream outputStream = new DataOutputStream(bash.getOutputStream());
        outputStream.writeBytes(command + "\n");
        outputStream.flush();
        outputStream.writeBytes("exit\n");
        outputStream.flush();
        bash.waitFor();
        String[] stdout = this.readStream(bash.getInputStream());
        String[] stderr = this.readStream(bash.getErrorStream());
        String output = TextUtils.join("\n", stderr) + TextUtils.join("\n", stdout);
        bash.destroy();
        return output;
    }
}
