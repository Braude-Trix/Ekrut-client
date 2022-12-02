package models;

import java.util.List;

public interface IResponse {
	public List<Object> getBody();
<<<<<<< HEAD
	public ResponseCode getCode();
=======
	public Integer getCode();
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
	public String getDescription();
}