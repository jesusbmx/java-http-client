package juno.http.convert.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import juno.http.RequestBody;
import juno.http.convert.RequestBodyConvert;

public class GsonRequestBodyConvert<T> implements RequestBodyConvert<T> {
    private static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    //private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public final Gson gson;
    public final TypeAdapter<T> adapter;

    public GsonRequestBodyConvert(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
    
    @Override
    public RequestBody parse(T value) throws Exception {
//        ByteArrayOutputStream bytes = IOUtils.arrayOutputStream();
//        Writer writer = new OutputStreamWriter(bytes, UTF_8);
//        JsonWriter jsonWriter = gson.newJsonWriter(writer);
//        adapter.write(jsonWriter, value);
//        jsonWriter.close();
//        return RequestBody.create(MEDIA_TYPE, bytes.toByteArray(), true);

        String json = adapter.toJson(value);
        return RequestBody.create(MEDIA_TYPE, json);
    }
      
}
