package com.wellav.dolphin.setting;

import java.util.Observable;

public class SettingObservable extends Observable {
	
	private String data = "";
	public String getData() {
		return data;
	}
	public void setData(String i) {
		if (!(this.data.equals("i"))) {
			this.data = i;
			setChanged();
		}
		// 只有在setChange()被调用后，notifyObservers()才会去调用update()，否则什么都不干。
		notifyObservers();
	}
}