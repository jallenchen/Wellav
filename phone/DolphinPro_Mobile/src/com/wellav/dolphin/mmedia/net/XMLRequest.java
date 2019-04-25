package com.wellav.dolphin.mmedia.net;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class XMLRequest extends Request<XmlPullParser> {
	/** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("text/xml; charset=%s", PROTOCOL_CHARSET);
    private final String mRequestBody;
	private final Listener<XmlPullParser> mListener;

	public XMLRequest(int method, String url,String requestBody, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mRequestBody=requestBody;
	}

	public XMLRequest(String url,String requestBody, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		this(Method.POST, url,requestBody, listener, errorListener);
		
	}

	
	
	@Override
	protected Response<XmlPullParser> parseNetworkResponse(
			NetworkResponse response) {
		try {
			String xmlString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlString));
			return Response.success(xmlPullParser,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (XmlPullParserException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(XmlPullParser response) {
		mListener.onResponse(response);
	}
	/**
    * @deprecated Use {@link #getBodyContentType()}.
    */
   @Override
   public String getPostBodyContentType() {
       return getBodyContentType();
   }

   /**
    * @deprecated Use {@link #getBody()}.
    */
   @Override
   public byte[] getPostBody() {
       return getBody();
   }
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
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
    	Map<String, String> headers = new HashMap<String, String>();  
    	headers.put("Charset", "UTF-8");  
    	headers.put("Content-Type", "text/xml");  
    	headers.put("Accept-Encoding", "*/*"); 
    	headers.put("Connection", "close");
    	return headers;
    }
    
}
