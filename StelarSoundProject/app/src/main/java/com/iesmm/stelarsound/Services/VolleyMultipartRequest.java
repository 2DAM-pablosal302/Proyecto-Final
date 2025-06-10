package com.iesmm.stelarsound.Services;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Map<String, String> headers;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.headers = null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // Text params
            Map<String, String> params = getParams();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    bos.write(("--" + boundary + LINE_FEED).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_FEED).getBytes());
                    bos.write((LINE_FEED).getBytes());
                    bos.write((entry.getValue() + LINE_FEED).getBytes());
                }
            }

            // Binary data
            Map<String, DataPart> data = getByteData();
            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                    DataPart part = entry.getValue();

                    bos.write(("--" + boundary + LINE_FEED).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" +
                            entry.getKey() + "\"; filename=\"" +
                            part.getFileName() + "\"" + LINE_FEED).getBytes());
                    bos.write(("Content-Type: " + part.getType() + LINE_FEED).getBytes());
                    bos.write(LINE_FEED.getBytes());

                    bos.write(part.getContent());

                    bos.write(LINE_FEED.getBytes());
                }
            }

            bos.write(("--" + boundary + "--" + LINE_FEED).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    public abstract Map<String, String> getParams() throws AuthFailureError;

    public abstract Map<String, DataPart> getByteData() throws AuthFailureError;

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private static final String LINE_FEED = "\r\n";

    // Nested class to hold file data
    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
