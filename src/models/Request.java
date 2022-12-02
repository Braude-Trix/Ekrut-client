package models;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable, IRequest {

<<<<<<< HEAD
    private static final long serialVersionUID = 1L;

    private String path;
    private Method method;
    private List<Object> body;
=======
private static final long serialVersionUID = 1L;
    
    private String path;
    private Method method;
    private Object[] body;
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
<<<<<<< HEAD
    public List<Object> getBody() {
        return body;
    }
    public void setBody(List<Object> body) {
=======
    public Object[] getBody() {
        return body;
    }
    public void setBody(Object[] body) {
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
        this.body = body;
    }
}
