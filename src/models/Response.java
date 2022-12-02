package models;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable, IResponse{
	private static final long serialVersionUID = 1L;
<<<<<<< HEAD
	private ResponseCode code;
=======
	private Integer code;
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
	private String description;
	private List<Object> body;

	@Override
<<<<<<< HEAD
	public ResponseCode getCode() {
		return code;
	}
	public void setCode(ResponseCode code) {
		this.code = code;
	}

=======
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
<<<<<<< HEAD

=======
	
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
	@Override
	public List<Object> getBody() {
		return body;
	}

	public void setBody(List<Object> body) {
		this.body = body;
	}
}
