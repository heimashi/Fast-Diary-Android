package cn.swang.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;


/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public class GsonRequest<T> extends Request<T> {

    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Response.Listener<T> mListener;
    private final String mRequestBody;
    private Type mType = null;
    private Gson mGson = null;

    public GsonRequest(int method, String url, String requestBody, Type type, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mType = type;
        mListener = listener;
        mRequestBody = requestBody;

        mGson = new Gson();
    }

    public GsonRequest(String url, String requestBody, Type type, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, url, requestBody, type, listener, errorListener);
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success((T) mGson.fromJson(jsonString, mType), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError(e));
        }
    }

    ;

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
