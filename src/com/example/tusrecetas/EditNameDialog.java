package com.example.tusrecetas;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class EditNameDialog extends DialogFragment implements OnClickListener {

	
	public static final String DIALOG_TAG = "InputTextDialog";

	private static final String DIALOG_LIST_BUNDLE= "dialogList";
    private static final String DIALOG_TITLE_BUNDLE= "dialogTitle";
    private static final String DIALOG_TEXT_BUNDLE= "dialogText";
    private static final String DIALOG_TEXT_SELECTION_START_BUNDLE = "dialogTextSelectionStart";
    private static final String DIALOG_TEXT_SELECTION_END_BUNDLE = "dialogTextSelectionEnd";

    private EditText input;
    private String initialText;
    private String title;
    private MyArrayList<String> lista;
    private int index;
    InputTextDialogListener inputTextDialogListener = null;
    
    public interface InputTextDialogListener {
        void onChangeText(String text);
    }

    public void setInputTextDialogListener(InputTextDialogListener listener) {
        inputTextDialogListener = listener;
    }

    public void setInitialText(String text) {
        initialText = text;
    }
    public void setList(MyArrayList<String> lista){
    	this.lista = lista;
    }
    public void setIndex(int index){
    	this.index = index;
    }
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        input = new EditText(getActivity());
        input.setHint("wtf");

        if (savedInstanceState != null) {
            String text = savedInstanceState.getString(DIALOG_TEXT_BUNDLE);
            title = savedInstanceState.getString(DIALOG_TITLE_BUNDLE);
            int selectionStart = savedInstanceState.getInt(DIALOG_TEXT_SELECTION_START_BUNDLE);
            int selectionEnd = savedInstanceState.getInt(DIALOG_TEXT_SELECTION_END_BUNDLE);
            
            input.setText(text);
            input.setSelection(selectionStart, selectionEnd);

        }
        else
        {
            if (initialText != null) {
                input.setText(initialText);
            } else {
                input.setText("");
            }
        }
        input.requestFocus();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
        .setTitle(title).setPositiveButton("Apply", this)
        .setNegativeButton("Cancel", this).setView(input);
        AlertDialog a = adb.create();
        a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return a;
    }

    @Override
    public void onSaveInstanceState(Bundle saved) {
        super.onSaveInstanceState(saved);

        saved.putString(DIALOG_TEXT_BUNDLE, input.getText().toString());
        saved.putString(DIALOG_TITLE_BUNDLE, title);
        saved.putInt(DIALOG_TEXT_SELECTION_START_BUNDLE, input.getSelectionStart());
        saved.putInt(DIALOG_TEXT_SELECTION_END_BUNDLE, input.getSelectionEnd());
        saved.putStringArrayList(DIALOG_LIST_BUNDLE, lista);
        
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
    	Log.d("onclick", "va a entrar en el if");
        if (which == Dialog.BUTTON_POSITIVE && inputTextDialogListener != null) {
        	Log.d("onclick", "dentro del if");
        	String text = input.getText().toString();
            inputTextDialogListener.onChangeText(text);
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            Log.d("onclick", "sale del if");
        }
    }

    
    
    public String getTitle() {
        return title;
    }
}
