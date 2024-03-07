## Juno Http Client
**HTTP client** for Java and Android, which facilitates the creation of **HTTP request**  such as: GET, POST, HEAD, OPTIONS, PUT, DELETE and TRACE.

Download [juno-http-client.jar](https://github.com/jesusbmx/HttpClient/raw/master/dist/juno-http-client.jar)

Download [juno.jar](https://github.com/jesusbmx/juno/raw/master/dist/juno.jar)

## Samples

```java
HttpClient client = HttpClient.getInstance()
    .setDebug(true);
```

#### GET
```java
String get() throws Exception {
  HttpRequest request = new HttpRequest(
       "GET", "https://api.github.com/users/defunkt");

  return client.execute(request, String.class);
}
```

#### POST
```java
String post(int id, String name, boolean active) throws Exception {
  // application-www-www-form-urlencoded
  FormBody reqBody = new FormBody()
          .add("id", id)
          .add("name", name)
          .add("active", active);
  
  HttpRequest request = new HttpRequest(
      "POST", "http://127.0.0.1/test.php", reqBody);

  return client.execute(request, String.class);
}
```

#### Download
```java
File download() throws Exception {
  HttpRequest request = new HttpRequest(
      "GET", "https://github.com/jesusbmx/HttpClient/raw/master/dist/juno-http-client.jar")
      .setTimeoutMs(20000);
  
  FileResponseBodyConvert convert = new FileResponseBodyConvert()
      .setDir("C:\\Users\\Jesus\\Downloads\\")
      //.setName("httpclient.jar");
  
  return client.execute(request, convert);
  //return client.execute(request, File.class);
}
```

#### Upload
```java
String upload(File file) throws Exception { 
  // multipart/form-data
  MultipartBody reqBody = new MultipartBody()
    .addParam("name", "John Doe")
    .addFile("img", file);

  HttpRequest request = new HttpRequest(
    "POST", "http://127.0.0.1/test.php", reqBody);

  return client.execute(request, String.class);
}
```

#### Response Body
```java
String run() throws Exception {
  HttpUrl url = new HttpUrl("https://api.github.com/users/defunkt");

  HttpRequest request = new HttpRequest("GET", url)
      .addHeader("Accept", "application/json");

  try (ResponseBody body = client.execute(request)) {
    //body.code {200}
    //body.headers {
    //    Keep-Alive: timeout=5, max=98
    //    Server: Apache/2.4.25 (Win32) OpenSSL/1.0.2j PHP/5.6.30
    //    Connection: Keep-Alive
    //}
    //body.in {InputStream}
    //body.bytes() {byte[]}  => body.close()
    return body.string(); // => body.close()
  }
}
```

## [Asynchronous and Synchronous Tasks] 

We prepare the request

```java
public Async<ResponseBody> insert(
    int id, String name, boolean active) {
    
  // application-www-www-form-urlencoded
  FormBody reqBody = new FormBody()
      .add("id", id)
      .add("name", name)
      .add("active", active);
  
  HttpRequest request = new HttpRequest(
      "POST", "http://127.0.0.1/test.php", reqBody);

  return client.newAsyncRequest(request, ResponseBody.class);
}
```

#### Asynchronous

Asynchronously send the request and notify your application with a callback when a response returns. Since this request is asynchronous, Restlight handles the execution in the background thread so that the
Main UI is not blocked or interferes with it.

```java
Async<ResponseBody> insert = insert(
            22, "John Doe", true);
    
insert.execute((ResponseBody result) -> {
  String str = result.string();
  System.out.println(str);

  //JSONObject json = new JSONObject(str);
  //System.out.println(json);

}, (Exception e) ->  {
   e.printStackTrace();
});
```

#### Synchronous

Synchronously send the request and return your response.

```java
Async<ResponseBody> insert = insert(
            22, "John Doe", true);
    
try (ResponseBody body = insert.await()) {
    System.out.println(body.string());
    
} catch(Exception e) {
    e.printStackTrace();
}
```

## [JSON](https://github.com/stleary/JSON-java)
(https://www.json.org/json-en.html)

For android it is not necessary to download [org.json jar](https://github.com/stleary/JSON-java)
For other java platforms like java swing if needed.

```java
public Async<JSONObject> insert(
    String name, int age, boolean active) {
      
    // application-www-www-form-urlencoded
    FormBody reqBody = new FormBody()
        .add("name", name)
        .add("age", age)
        .add("active", active);

    HttpRequest request = new HttpRequest(
        "POST", "http://127.0.0.1/test.php", reqBody);
        
    return client.newAsyncRequest(request, JSONObject.class);
}
```

```java
Async<JSONObject> insert = insert(
    "John Doe", 22, true);
        
try {
    JSONObject json = insert.await();
    System.out.println(json.toString(1));
            
} catch (Exception e) {
    e.printStackTrace();
}
```

#### JSON request body
```java
String run() throws Exception {
  JSONObject data = new JSONObject();
  data.put("user_id", 7);
  data.put("name", "jesus");

  RequestBody reqBody = RequestBody.create(
        "application/json", data.toString());

  HttpRequest request = new HttpRequest(
        "GET", "http://127.0.0.1/test.php", reqBody);

  return client.execute(request, String.class);
}
```

## [JWT](https://jwt.io/)

Store, clear, transmit and automatically refresh JWT authentication tokens.

```java
public class MyApi implements JWT.OnAuth {
  public static final MyApi INSTANCE = new MyApi();  

  // Client with authentication
  public final HttpClient client;
  
  // Configuration for the client
  private MyApi() {
    // You can create you own storage
    File tokenStorage = new File(".../MyApi.jwt");
    JWTManager jwtManager = new JWTManager(tokenStorage, this);

    // Add the JWT Manager to interceptor
    client = new HttpClient()
        .setInterceptor(new AuthTokenInterceptor(jwtManager, "Authorization", "Bearer "))
        .setDebug(true);
  }

  // Define token auth function.
  @Override
  public JWT auth() throws Exception {        
    FormBody body = new FormBody()
        .add("email", "myMail@domain.com")
        .add("password", "myPassword");
        
    HttpRequest request = new HttpRequest(
        "POST", ".../auth/login", body);
        
    // Result
    HttpClient defaultClient = HttpClient.getInstance();
    JSONObject json = defaultClient.execute(request, JSONObject.class);
    String access_token = json.optString("token");

    return new JWT(access_token);
  }

  // Request with Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
  public Async<JSONObject> region(String countryIso) {
    final HttpRequest request = new HttpRequest(
            "GET", ".../region/" + countryIso));

    return client.newAsyncRequest(request, JSONObject.class);
  }
}
```

## [GSON](https://github.com/google/gson) 

In your **build.gradle** you will need to add the dependencies for  **GSON**:

```groovy
dependencies {
  ...
  compile 'com.google.code.gson:gson:2.4'
}
```


Now we are ready to start writing some code. The first thing we want to do is define our **Post** model.
Create a new file called **Post.java** and define the **Post** class like this:

```java
public class Post {
  
  @SerializedName("id")
  public long ID;
    
  @SerializedName("date")
  public Date dateCreated;
 
  public String title;
  public String author;
  public String url;
  public String body;
}
```


Let's create a new instance of **GSON** before calling request. We'll also need to set a custom date format on the **GSON** instance to handle the dates returned by the API:

We define the database interactions. They can include a variety of query methods:

```java
public class PostApi {
  HttpClient client = HttpClient.getInstance()
          .setDebug(true);  
    
  public PostApi() {
    Gson gson = new GsonBuilder()
            .setDateFormat("M/d/yy hh:mm a")
            .create();
    
    client.setFactory(new GsonConvertFactory(gson));
  }

  public Async<Post[]> getPosts() {
    HttpRequest request = new HttpRequest(
        "GET", "https://kylewbanks.com/rest/posts.json");

    return client.newAsyncRequest(request, Post[].class);
  }

  public Async<String> insert(Post p) {
    RequestBody reqBody = client.createRequestBody(p); // application/json
    // RequestBody reqBody = client.createFormBody(p); // application-www-www-form-urlencoded
    // RequestBody reqBody = client.createMultipartBody(p); // multipart/form-data
    
    HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);
    
    return client.newAsyncRequest(request, String.class);
  }
}
```

Prepares the request to be executed in the background. Ideal for android applications.
Asynchronously send the request and notify your application with a callback when a response returns.
```java
...
PostApi api = new PostApi();
    
Async<Post[]> async = api.getPosts(); 

async.execute((Post[] result) -> {
  List<Post> list = Arrays.asList(result);
  for (Post post : list) {
    System.out.println(post.title);
  }

}, (Exception e) -> {
  e.printStackTrace(System.out);
});
```

## [Jackson](https://github.com/FasterXML/jackson) 

Now we are ready to start writing some code. The first thing we want to do is define our **Post** model.
Create a new file called **Post.java** and define the **Post** class like this:

```java
public class Post {
    
  @JsonProperty("id")
  public long id;

  @JsonProperty("date")
  public Date dateCreated;

  @JsonProperty("title")
  public String title;
  
  @JsonProperty("author")
  public String author;
  
  @JsonProperty("url")
  public String url;
  
  @JsonProperty("body")
  public String body;
}
```


Let's create a new instance of **ObjectMapper** before calling request. We'll also need to set a custom date format on the **ObjectMapper** instance to handle the dates returned by the API:

We define the database interactions. They can include a variety of query methods:

```java
public class PostApi {
  
  HttpClient client = HttpClient.getInstance()
          .setDebug(true);  
    
  public PostApi() {
    ObjectMapper mapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("M/d/yy hh:mm a"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    client.setFactory(new JacksonConvertFactory(mapper));
  }

  public Async<Post[]> getPosts() {
    HttpRequest request = new HttpRequest(
        "GET", "https://kylewbanks.com/rest/posts.json");

    return client.newAsyncRequest(request, Post[].class);
  }
  
  public Async<String> insert(Post p) {
    // application/json
    RequestBody reqBody = client.createRequestBody(p);
    
    HttpRequest request = new HttpRequest(
            "POST", "http://127.0.0.1/test.php", reqBody);
    
    return client.newAsyncRequest(request, String.class);
  }
}
```

Prepares the request to be executed in the background. Ideal for android applications.
Asynchronously send the request and notify your application with a callback when a response returns.
```java
...
PostApi api = new PostApi();
    
Async<Post[]> async = api.getPosts(); 

async.execute((Post[] result) -> {
  List<Post> list = Arrays.asList(result);
  for (Post post : list) {
    System.out.println(post.title);
  }

}, (Exception e) -> {
  e.printStackTrace(System.out);
});
```

License
=======

    Copyright 2022 HttpClient, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
