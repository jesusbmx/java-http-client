package juno.http.convert.generic;

import java.io.File;
import juno.http.HttpResponse;
import juno.http.convert.ConverterFactory;
import juno.http.convert.RequestBodyConverter;
import juno.http.convert.ResponseBodyConverter;

/**
 * GenericConvertFactory es una implementación concreta de ConvertFactory.
 * Proporciona una fábrica de convertidores genéricos para manejar conversiones
 * de cuerpos de solicitudes y respuestas en una aplicación HTTP.
 */
public class GenericConverterFactory implements ConverterFactory {

    /**
     * Crea un convertidor de cuerpo de solicitud auxiliar para una clase dada.
     * Si no se encuentra un convertidor, lanza una RuntimeException.
     * 
     * @param <V> El tipo del objeto.
     * @param type La clase del objeto.
     * @return El convertidor creado.
     */
    @Override
    public <V> RequestBodyConverter<V> requestBodyConverter(Class<V> type) {
        if (type == String.class) {
           return (RequestBodyConverter<V>) StringRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    /**
     * Crea un convertidor de cuerpo de respuesta auxiliar para una clase dada.
     * Si no se encuentra un convertidor, lanza una RuntimeException.
     * 
     * @param <V> El tipo del objeto.
     * @param type La clase del objeto.
     * @return El convertidor creado.
     */
    @Override
    public <V> ResponseBodyConverter<V> responseBodyConverter(Class<V> type) {
        if (type == byte[].class) 
            return (ResponseBodyConverter<V>) BytesResponseBodyConverter.INSTANCE;
        if (type == File.class)
            return (ResponseBodyConverter<V>) new FileResponseBodyConverter();
        if (type == HttpResponse.class)
            return (ResponseBodyConverter<V>) SimpleResponseBodyConverter.INSTANCE;
        if (type == String.class)
            return (ResponseBodyConverter<V>) StringResponseBodyConverter.INSTANCE;
        
        return null;
    }
}
