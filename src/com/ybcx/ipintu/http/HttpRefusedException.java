package com.ybcx.ipintu.http;



/**
 * HTTP StatusCode is 403, Server refuse the request
 */
public class HttpRefusedException extends HttpException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -93259665832389153L;
	/**
     * �������������Ĵ�����Ϣ
     */
    private RefuseError mError;

    public HttpRefusedException(Exception cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public HttpRefusedException(String msg, Exception cause, int statusCode) {
        super(msg, cause, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpRefusedException(String msg, Exception cause) {
        super(msg, cause);
        // TODO Auto-generated constructor stub
    }

    public HttpRefusedException(String msg, int statusCode) {
        super(msg, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpRefusedException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }

    public RefuseError getError() {
        return mError;
    }
    
    public HttpRefusedException setError(RefuseError error) {
        mError = error;
        return this;
    }

}
