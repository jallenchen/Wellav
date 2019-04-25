package com.wellav.dolphin.mmedia.interfaces;

public interface ICallingVideo {
	public void opt_Request(String parm);
	public void opt_Response(int action, String parm);
	public void ViewAction(int type);
	public void setViewUIVisible(int visible);
}
