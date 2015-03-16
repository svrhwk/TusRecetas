package com.example.tusrecetas;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

public class Prueba extends Activity {

	private static final String LISTA_ING_VISIBILITY = "lista_ing_visibility";
	private static final String LISTA_PASOS_VISIBILITY = "lista_pasos_visibility";
	private static final String RECETA_POSICION_BUNDLE = "posicion";
	private static final String RECETA_BUNDLE = "receta";
	private static final String FOCUSED_ID_BUNDLE = "focusedId";
	
	private static final int ING_INTERNAL_ID = 10000;
	private static final int PASOS_INTERNAL_ID = 20000;
	
	private static final String TEXT_SELECTION_START_BUNDLE = "TextSelectionStart";
	private static final String TEXT_SELECTION_END_BUNDLE = "TextSelectionEnd";
	private static final int ROW_RESOURCE = R.layout.receta_item_rowlayout_prueba;
	
	LinearLayout headerIng;
	LinearLayout headerPasos;
	LinearLayout llListaIng;
	LinearLayout llListaPasos;
	Button btnAddIng;
	Button btnAddPasos;
	boolean llListaIng_visible=true;
	boolean llListaPasos_visible=true;
	int posicion;
	int focused_pos=-1;
	int focused_text_selection_start=-1;
	int focused_text_selection_end=-1;
	int ing_id_cont=0;
	int pasos_id_cont=0;
	Receta receta;
	MyArrayList<String> listaIng;
	MyArrayList<String> listaPasos;
	MyArrayList<EditText> listaET_Ing;
	MyArrayList<EditText> listaET_Pasos;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receta3);
		try {
			inicializar();
			
			setListeners();
			
			if(savedInstanceState!=null){
				restaurar(savedInstanceState);
			}else{
				cargarReceta();
			}
			datosToLayout();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Prueba", "onCreate(): "+e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prueba, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		
		MyArrayList<String> ingredientes = new MyArrayList<String>();
		MyArrayList<String> pasos = new MyArrayList<String>();
		
		
		for(int i=0; i<listaET_Ing.size();i++){
			EditText etIng = listaET_Ing.get(i);
			if(etIng.hasFocus()){
				focused_pos = (Integer) etIng.getTag();
				focused_text_selection_start = etIng.getSelectionStart();
				focused_text_selection_end = etIng.getSelectionEnd();
			}
			ingredientes.add(etIng.getText().toString());
			Log.d("onSave()","ing("+i+")="+listaET_Ing.get(i).getText().toString());
			
		}
		for(int i=0; i<listaET_Pasos.size();i++){
			EditText etPasos = listaET_Ing.get(i);
			if(etPasos.hasFocus()){
				focused_pos = (Integer) etPasos.getTag();
				focused_text_selection_start = etPasos.getSelectionStart();
				focused_text_selection_end = etPasos.getSelectionEnd();
			}
			pasos.add(etPasos.getText().toString());
			Log.d("onSave()","paso("+i+")="+listaET_Pasos.get(i).getText().toString());
		}
		
		
		receta.setIngredientes(ingredientes);
		receta.setPasos(pasos);
		Gson gson = new Gson();
		String recetaSerializada = gson.toJson(receta);
		
		outState.putString(RECETA_BUNDLE, recetaSerializada);
		outState.putInt(RECETA_POSICION_BUNDLE, posicion);
		outState.putBoolean(LISTA_ING_VISIBILITY, llListaIng_visible);
		outState.putBoolean(LISTA_PASOS_VISIBILITY, llListaPasos_visible);
	}
	
	private void cargarReceta() {
		try {
			// Obtenemos los datos a mostrar:
			Bundle extras = getIntent().getExtras();
			String serialized = extras.getString(RECETA_BUNDLE);
			posicion = extras.getInt(RECETA_POSICION_BUNDLE);
			Gson gson = new Gson();
			receta = gson.fromJson(serialized, Receta.class);
			focused_pos=-1;
		} catch (Exception e) {
			Log.e("RecetaActivity", "cargarReceta: " + e.getMessage());
		}
	}
	private void addRowIng(){
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(ROW_RESOURCE, null);
		
		Random r = new Random();
		Integer randomID = r.nextInt();
		Log.d("addRowIng()", "random: "+randomID+"     i: "+listaET_Ing.size());
		EditText et = (EditText) rowView.findViewById(R.id.etRecetaRow2);
		et.setText(""+randomID);
		Button btn = (Button) rowView.findViewById(R.id.btnEliminarOK2);
		et.setTag(randomID);
		listaET_Ing.add(et);
		btn.setTag(randomID);
		btn.setOnClickListener(new OnClickListener() {
			
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//remove from lista
				for(int i=0; i<listaET_Ing.size(); i++){
					EditText etLista = listaET_Ing.get(i);
					int etListaTag = (Integer) etLista.getTag();
					int etTag = (Integer) v.getTag();
					if(etListaTag == etTag){
						Log.d("addRowIng", "match: ");
						Log.d("addRowIng", "     btnTag: "+etTag);
						Log.d("addRowIng", "     lista: "+etListaTag);
						Log.d("addRowIng", "     i: "+i);
						listaET_Ing.remove(i);
						llListaIng.removeViewAt(i);
						break;
					}
				}
			}
		});
		llListaIng.addView(rowView);
	}
	private void addRowIng(String text){
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(ROW_RESOURCE, null);
		Random r = new Random();
		Integer randomID = r.nextInt();
		Log.d("addRowIng(text)", "text="+text);
		EditText et = (EditText) rowView.findViewById(R.id.etRecetaRow2);
		et.setText(text);
		Button btn = (Button) rowView.findViewById(R.id.btnEliminarOK2);
		et.setTag(randomID);
		listaET_Ing.add(et);
		btn.setTag(randomID);
		btn.setOnClickListener(new OnClickListener() {
			
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//remove from lista
				for(int i=0; i<listaET_Ing.size(); i++){
					EditText etLista = listaET_Ing.get(i);
					int etListaTag = (Integer) etLista.getTag();
					int etTag = (Integer) v.getTag();
					if(etListaTag == etTag){
						Log.d("addRowIng", "match: ");
						Log.d("addRowIng", "     btnTag: "+etTag);
						Log.d("addRowIng", "     lista: "+etListaTag);
						Log.d("addRowIng", "     i: "+i);
						listaET_Ing.remove(i);
						llListaIng.removeViewAt(i);
						break;
					}
				}
			}
		});
		Log.d("addRowIng(Text)", et.getText().toString());
		llListaIng.addView(rowView);
	}
	
	private void addRowPasos(){
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(ROW_RESOURCE, null);
		
		Random r = new Random();
		Integer randomID = r.nextInt();
		Log.d("addRowPasos()", "random: "+randomID+"     i: "+listaET_Pasos.size());
		EditText et = (EditText) rowView.findViewById(R.id.etRecetaRow2);
		et.setText(""+randomID);
		Button btn = (Button) rowView.findViewById(R.id.btnEliminarOK2);
		et.setTag(randomID);
		listaET_Pasos.add(et);
		btn.setTag(randomID);
		btn.setOnClickListener(new OnClickListener() {
			
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//remove from lista
				for(int i=0; i<listaET_Pasos.size(); i++){
					EditText etLista = listaET_Pasos.get(i);
					int etListaTag = (Integer) etLista.getTag();
					int etTag = (Integer) v.getTag();
					if(etListaTag == etTag){
						Log.d("addRowIng", "match: ");
						Log.d("addRowIng", "     btnTag: "+etTag);
						Log.d("addRowIng", "     lista: "+etListaTag);
						Log.d("addRowIng", "     i: "+i);
						listaET_Pasos.remove(i);
						llListaPasos.removeViewAt(i);
						break;
					}
				}
			}
		});
		llListaPasos.addView(rowView);
	}
	private void addRowPasos(String text){
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(ROW_RESOURCE, null);
		
		Random r = new Random();
		Integer randomID = r.nextInt();
		Log.d("addRowPasos(text)", "text="+text);
		EditText et = (EditText) rowView.findViewById(R.id.etRecetaRow2);
		et.setText(text);
		Button btn = (Button) rowView.findViewById(R.id.btnEliminarOK2);
		et.setTag(randomID);
		listaET_Pasos.add(et);
		btn.setTag(randomID);
		btn.setOnClickListener(new OnClickListener() {
			
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//remove from lista
				for(int i=0; i<listaET_Pasos.size(); i++){
					EditText etLista = listaET_Pasos.get(i);
					int etListaTag = (Integer) etLista.getTag();
					int etTag = (Integer) v.getTag();
					if(etListaTag == etTag){
						Log.d("addRowIng", "match: ");
						Log.d("addRowIng", "     btnTag: "+etTag);
						Log.d("addRowIng", "     lista: "+etListaTag);
						Log.d("addRowIng", "     i: "+i);
						listaET_Pasos.remove(i);
						llListaPasos.removeViewAt(i);
						break;
					}
				}
			}
		});
		llListaPasos.addView(rowView);
	}

	private void inicializar(){
		listaET_Ing = new MyArrayList<EditText>();
		listaET_Pasos = new MyArrayList<EditText>();
		
		headerIng = (LinearLayout) findViewById(R.id.headerIng);
		headerPasos = (LinearLayout) findViewById(R.id.headerPasos);
		llListaIng = (LinearLayout) findViewById(R.id.llListaIng);
		llListaPasos = (LinearLayout) findViewById(R.id.llListaPasos);
		btnAddIng = (Button) findViewById(R.id.btnAdd1);
		btnAddPasos = (Button) findViewById(R.id.btnAdd2);
	}
	private void setListeners(){

		headerIng.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llListaIng_visible=!llListaIng_visible;
				if(llListaIng_visible){
					llListaIng.setVisibility(View.VISIBLE);
				}else{
					llListaIng.setVisibility(View.GONE);
				}
			}
		});
		headerPasos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llListaPasos_visible=!llListaPasos_visible;
				if(llListaPasos_visible){
					llListaPasos.setVisibility(View.VISIBLE);
				}else{
					llListaPasos.setVisibility(View.GONE);
				}
			}
		});
		btnAddIng.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addRowIng();
			}
		});
		btnAddPasos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addRowPasos();
			}
		});
		
	}
	
	private void restaurar(Bundle savedInstanceState){
		llListaIng_visible=savedInstanceState.getBoolean(LISTA_ING_VISIBILITY);
		llListaPasos_visible=savedInstanceState.getBoolean(LISTA_PASOS_VISIBILITY);
		if(llListaIng_visible){
			llListaIng.setVisibility(View.VISIBLE);
		}else{
			llListaIng.setVisibility(View.GONE);
		}
		
		if(llListaPasos_visible){
			llListaPasos.setVisibility(View.VISIBLE);
		}else{
			llListaPasos.setVisibility(View.GONE);
		}
		String recetaSerialized = savedInstanceState.getString(RECETA_BUNDLE);
		Gson gson = new Gson();
		receta = gson.fromJson(recetaSerialized, Receta.class);
		MyArrayList<String> ings = receta.getIngredientes();
		for(int i=0;i<ings.size();i++){
			Log.d("restore()","ing("+i+")="+ings.get(i));
		}
		posicion = savedInstanceState.getInt(RECETA_POSICION_BUNDLE);
		focused_pos = savedInstanceState.getInt(FOCUSED_ID_BUNDLE);
	}
	private void datosToLayout(){
		MyArrayList<String> ingredientes = receta.getIngredientes();
		MyArrayList<String> pasos = receta.getPasos();
		for(int i=0;i<ingredientes.size();i++){
			addRowIng(ingredientes.get(i));
		}
		for(int i=0;i<pasos.size();i++){
			addRowPasos(pasos.get(i));
		}
	}

}
