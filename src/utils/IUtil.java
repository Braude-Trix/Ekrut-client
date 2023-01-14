package utils;

import javafx.collections.ObservableList;

public interface IUtil {
	public boolean isBlankString(String s);
    public String getTxtPassword();
	public String getTxtUsername();
	public String getErrorLabel();
	public void setErrorLabel(String msgError);
	public Integer getValueSubscriberSelected();
	public void setValueSubscriberSelected(Integer id);
	public String getErrorTouch();
	public void setErrorTouch(String msgError);
	public ObservableList<Integer> getAllSubscribers();
	public void setAllSubscribers(ObservableList<Integer> options);
}
