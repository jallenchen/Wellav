package com.wellav.dolphin.fragment;

import com.wellav.dolphin.launcher.R;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class MessageDeleteDialogFragment extends DialogFragment  implements android.view.View.OnClickListener {
	
	private Context context;
    private Button cancleBtn;
    private Button okBtn;
    private MessageDeleteDialogListener listener;

    public interface MessageDeleteDialogListener{   
        public void onClick(View view);   
    }   
    
    public MessageDeleteDialogFragment(Context context,MessageDeleteDialogListener listener) {
        this.context = context;
        this.listener = listener;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View view = inflater.inflate(R.layout.fragment_message_delete, container, false);
        cancleBtn = (Button) view.findViewById(R.id.cancle_btn);
        okBtn = (Button) view.findViewById(R.id.ok_btn);
        cancleBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

	@Override
	public void onClick(View v) {
		listener.onClick(v);
		dismiss();
	}
}
