import java.io.File;
import java.io.InputStream;
import juno.http.FormBody;
import juno.http.HttpClient;
import juno.http.HttpRequest;
import juno.http.HttpUrl;
import juno.http.MultipartBody;
import juno.http.ResponseBody;
import juno.http.convert.ResponseBodyConvert;
import juno.http.convert.generic.FileResponseBodyConvert;

public class RestTest {
    
    HttpClient cli = HttpClient.getInstance()
            .setDebug(true)
    ;
    
    String get() throws Exception {
        HttpRequest request = new HttpRequest("GET", 
                "https://api.github.com/users/defunkt");

        return cli.execute(request, String.class);
    }
    
    String post(int id, String name, boolean active) throws Exception {
        FormBody reqBody = new FormBody()
                .add("id", id)
                .add("name", name)
                .add("active", active);
        
        HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);

        return cli.execute(request, String.class);
    }
    
    String delete(int id) throws Exception {
        FormBody body = new FormBody()
            .add("id", id);

        HttpRequest request = new HttpRequest(
            "DELETE", "http://127.0.0.1/test.php", body);

        return cli.execute(request, String.class);
    }
    
    File download() throws Exception {
        HttpRequest request = new HttpRequest(
            "GET", "https://github.com/jesusbmx/HttpCli/raw/master/dist/httpcli.jar")
            .setTimeoutMs(5000 * 2 * 2);
        
        ResponseBodyConvert<File> convert = new FileResponseBodyConvert()
                .setDir("C:\\Users\\Jesus\\Downloads\\");
        
        return cli.execute(request, convert);
        //return cli.execute(request, File.class);
    }
    
    String upload(File file) throws Exception { 
        MultipartBody body = new MultipartBody()
          .addParam("nombre", "Elizabéth Magaña")
          .addFile("img", file);
    
        HttpRequest request = new HttpRequest(
          "POST", "http://127.0.0.1/test.php", body);

        return cli.execute(request, String.class);
    }
    
    InputStream run_() throws Exception {
        HttpUrl url = new HttpUrl("https://api.github.com/users/defunkt");
        
        HttpRequest request = new HttpRequest("GET", url)
                .addHeader("Accept", "application/json");

        ResponseBody body = null;
        try {
            body = cli.execute(request);
//            body.code [200];
//            body.headers [
//               Keep-Alive: timeout=5, max=98
//                Server: Apache/2.4.25 (Win32) OpenSSL/1.0.2j PHP/5.6.30
//                Connection: Keep-Alive
//                Content-Length: 2245
//                Date: Sun, 13 Mar 2022 15:01:53 GMT
//                Content-Type: text/html; charset=UTF-8
//                X-Powered-By: PHP/5.6.30
//            ];
//            body.in [InputStream];
            return body.in;
        } finally {
            if (body != null) body.close();
        }
    }
    
    public static void main(String[] args) {
        RestTest api = new RestTest();
     
        try {
            
            String get = api.get();
            System.out.println(get);
            
            String post = api.post(1, "My name", true);
            System.out.println(post);
            
            String delete = api.delete(1);
            System.out.println(delete);
            
            File down = api.download();
            System.out.println(down);
            
            String up = api.upload(down);
            System.out.println(up);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
