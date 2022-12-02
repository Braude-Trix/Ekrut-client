package models;

<<<<<<< HEAD
import java.util.List;

public interface IRequest {
    public String getPath();
    public Method getMethod();
    public List<Object> getBody();
=======
public interface IRequest {
    public String getPath();
    public Method getMethod();
    public Object[] getBody();
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
}
