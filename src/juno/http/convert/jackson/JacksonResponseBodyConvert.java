package juno.http.convert.jackson;

import com.fasterxml.jackson.databind.ObjectReader;
import java.io.InputStreamReader;
import java.io.Reader;
import juno.http.HttpResponse;
import juno.http.convert.ResponseBodyConvert;
import juno.io.IOUtils;

public class JacksonResponseBodyConvert<T> implements ResponseBodyConvert<T> {

    public final ObjectReader adapter;

    public JacksonResponseBodyConvert(ObjectReader adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public T parse(HttpResponse response) throws Exception {
        Reader reader = null;
        try {
            reader = new InputStreamReader(response.content, response.charset);
            return adapter.readValue(reader);
            
        } finally {
            IOUtils.closeQuietly(reader);
            response.close();
        }
    }
    
}
