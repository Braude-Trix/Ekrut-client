package models;

public interface IRequest {
    public String getPath();
    public Method getMethod();
    public Object[] getBody();
}
