package com.wellav.dolphin.mmedia.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;

public class CreateDialog {
	 private DolphinDialogLisenter listener;
	 private AlertDialog alertDialog;
	 private LayoutInflater mInflater;
	 private Context context;
    public CreateDialog(Context c, DolphinDialogLisenter l) {
		// TODO Auto-generated constructor stub
    	 context = c;
         listener = l;
         
         mInflater = LayoutInflater.from(context);
	}
    
    private OnItemClickListener m_item_click_listener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
//            CheckedTextView cT = (CheckedTextView)view.findViewById(R.id.text1);
//            String [] params = { title,cT.getText().toString() }; 
            listener.onDialogDismiss(position);
        	
            
            alertDialog.dismiss();
        }
    }; 

	public AlertDialog singleChoiceDialog(String[] item, int checked_position) {
        
    	   
        View singleChoiceView = mInflater.inflate(R.layout.dialog_watchdolpih, null);                     
        ListView listView = (ListView) singleChoiceView.findViewById(R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(new ArrayAdapter<String>(context, R.layout.dialog_watchdolpih_item, item));
        listView.setItemChecked(checked_position, true);
        listView.setOnItemClickListener(m_item_click_listener);
   
        alertDialog = new AlertDialog.Builder(context)
            .setView(singleChoiceView)
            .create();
        
        return alertDialog;
    }

}
