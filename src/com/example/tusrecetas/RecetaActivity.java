package com.example.tusrecetas;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class RecetaActivity extends FragmentActivity {

	public static final String INGREDIENTES = "Ingredients";
	public static final String PASOS = "Pasos";
	public static final String RECETA_FRAGMENT = "recetaData";
	public static final String DIALOG_FRAGMENT = "dialogData";
	public static final int INGREDIENTES_ID = 100;
	public static final int PASOS_ID = 100;
	static boolean charged = false;
	MyArrayList<String> listaIngredientes;
	MyArrayList<String> listaPasos;
	Receta receta;
	int posicion;
	View focused;
	TextView tvNombreReceta;
	ExpandableListView elvIngredientes;
	ExpandableListView elvPasos;
	ExpandableListViewAdapter ingredientesAdapter;
	ExpandableListViewAdapter pasosAdapter;
	EditNameDialog dialog;
	private RetainedFragment<Receta> recetaDataFragment;
	private RetainedFragment<EditNameDialog> dialogDataFragment;
	MyArrayList<EditText> listaETIng = new MyArrayList<EditText>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receta2);
		try {
			
			// find the retained fragment on activity restarts
	        FragmentManager fm = getFragmentManager();
	        recetaDataFragment =  (RetainedFragment<Receta>) fm.findFragmentByTag(RECETA_FRAGMENT);
	        
	        dialogDataFragment =  (RetainedFragment<EditNameDialog>) fm.findFragmentByTag(DIALOG_FRAGMENT);
	        // create the fragment and data the first time
	        if (recetaDataFragment == null) {
	            // add the fragment
	            recetaDataFragment = new RetainedFragment<Receta>();
	            fm.beginTransaction().add(recetaDataFragment, RECETA_FRAGMENT).commit();
	            // load the data from the web
	            cargarReceta();
	            recetaDataFragment.setData(receta);
	        }
	        // the data is available in dataFragment.getData()
	        else{
	        	receta = (Receta) recetaDataFragment.getData();
	        }
	        
	        if(dialogDataFragment != null){
	        	dialog = (EditNameDialog) dialogDataFragment.getData();
	        }
	       
	        
			tvNombreReceta = (TextView)findViewById(R.id.tvNombreReceta2);
			tvNombreReceta.setText(receta.getNombre());
			
			elvIngredientes = (ExpandableListView)findViewById(R.id.elvIngredientes);
			elvPasos = (ExpandableListView)findViewById(R.id.elvPasos);
			
			ingredientesAdapter = new ExpandableListViewAdapter(this, receta.getIngredientes(), INGREDIENTES, INGREDIENTES_ID, R.layout.receta_item_rowlayout, R.layout.lista_parent);
			pasosAdapter = new ExpandableListViewAdapter(this, receta.getPasos(), PASOS, PASOS_ID, R.layout.receta_item_rowlayout, R.layout.lista_parent);
			
			elvIngredientes.setAdapter(ingredientesAdapter);
			elvPasos.setAdapter(pasosAdapter);
			
			elvIngredientes.setOnGroupClickListener(new OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					if(focused!=null){
						focused.clearFocus();
					}
					return false;
				}
			});
			
			elvPasos.setOnGroupClickListener(new OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					if(focused!=null){
						focused.clearFocus();
					}
					return false;
				}
			});
			
		} catch (Exception e) {
			Log.e("RecetaActivity","onCreate: "+e.getMessage());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receta, menu);
		return true;
	}
	
	private void cargarReceta(){
		try {
			//Obtenemos los datos a mostrar:
			Bundle extras = getIntent().getExtras();
			String serialized = extras.getString("receta");
			posicion = extras.getInt("posicion");
			Gson gson = new Gson();
			receta = gson.fromJson(serialized, Receta.class);
			
			
		} catch (Exception e) {
			Log.e("RecetaActivity","cargarReceta: "+e.getMessage());
		}
	}
	

	
	public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

		private MyArrayList<String> lista;
		private String listName;
		private int startingId;
		private int childResource;
		private int parentResource;
//		private Context context;
		public ExpandableListViewAdapter(Context context, MyArrayList<String> lista, String listName, int startingId, int childResource, int parentResource) {
//			this.context = context;
			this.lista = lista;
			this.listName = listName;
			this.startingId = startingId;
			this.childResource = childResource;
			this.parentResource = parentResource;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return lista.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return lista.size();
		}
		
		@Override
		public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
			try {
					if (convertView == null) {
						LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView = inflater.inflate(childResource, null);
					}
					
				final TextView textview = (TextView) convertView.findViewById(R.id.etRecetaRow);
//				final Button btnEdit = (Button) convertView.findViewById(R.id.btnEditarOK);
				final Button btnEliminar = (Button) convertView.findViewById(R.id.btnEliminarOK);
				final String theText = lista.get(childPosition);
				
				textview.setText(theText);
				textview.setTag(childPosition);
				btnEliminar.setTag(childPosition);
				
//				textview.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Log.d("RecetaActivity", "textview.onclick(): "+lista.hashCode());
//						dialog = new EditNameDialog();
//		                dialog.setInitialText(theText);
//		                dialog.setInputTextDialogListener(new InputTextDialogListener() {
//
//		                    @Override
//		                    public void onChangeText(String text) {
//		                    	//hacer replace de la lista
//		                    	if(listName==INGREDIENTES){
//		                    		listaIngredientes = receta.getIngredientes();
//		                    		listaIngredientes.replace(childPosition, text);
//		                    		Log.d("RecetaActivity", "onChangeText(): "+listaIngredientes.get(childPosition));
//		                    	}else{
//		                    		listaPasos = receta.getPasos();
//		                    		listaPasos.replace(childPosition, text);
//		                    		Log.d("RecetaActivity", "onChangeText(): "+listaPasos.get(childPosition));
//		                    	}
//		                    	lista.replace(childPosition, text);
//		                    	ExpandableListViewAdapter.this.notifyDataSetChanged();
//		                    }
//		                });
//		                FragmentManager fm = getFragmentManager();
//		                dialogDataFragment = new RetainedFragment<EditNameDialog>();
//		                fm.beginTransaction().add(dialogDataFragment, DIALOG_FRAGMENT).commit();
//			            // load the data from the web
//			            cargarReceta();
//			            recetaDataFragment.setData(receta);
//		                dialogDataFragment.setData(dialog);
//		                dialog.show(fm, EditNameDialog.DIALOG_TAG);
//					}
//				});
				
				btnEliminar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int p = (Integer) v.getTag();
						lista.remove(p);
						ExpandableListViewAdapter.this.notifyDataSetChanged();
					}
				});
				
			} catch (Exception e) {
				Log.e("getChildView",""+e.getMessage());
			}
			return convertView;
		
		}

		@Override
		public String getGroup(int groupPosition) {
			return listName;
		}

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public long getGroupId(final int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(parentResource, null);
				}
				TextView tvCategoria = (TextView)convertView.findViewById(R.id.tvCategoria);
				Button btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
				btnAdd.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						lista.add("");
						notifyDataSetChanged();
					}
				});
				tvCategoria.setText(listName);
			} catch (Exception e) {
				Log.e("MainActivity","getGroupView: "+e.getMessage());
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
	
	@Override
	public void finish() {

		Gson gson = new Gson();
		String recetaSerializada = gson.toJson(receta);
		Intent intent = new Intent().putExtra("receta", recetaSerializada).putExtra("posicion", posicion);
		setResult(Activity.RESULT_OK,intent);
		Toast.makeText(this, "receta editada, se supone", Toast.LENGTH_LONG).show();
		super.finish();
	}
	
	public void clickbtn(View v){
		listaIngredientes = receta.getIngredientes();
		String str ="get_nomod: "+listaIngredientes.size()+"\n";
//		for(int i=0;i<listaIngredientes.size();i++){
//			str=str+listaIngredientes.get(i)+"\n";
//		}
		listaIngredientes.add("trol");
		str = str + "add_lista: "+listaIngredientes.size()+"\n";
		listaIngredientes = receta.getIngredientes();
		str = str + "get_mod: "+listaIngredientes.size();
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_edit_receta2);
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Gson gson = new Gson();
		String serialized = gson.toJson(receta, Receta.class);
		
		
		
		outState.putString("receta", serialized);
		outState.putInt("posicion", posicion);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		String serialized = savedInstanceState.getString("receta");
		Gson gson = new Gson();
		receta = gson.fromJson(serialized, Receta.class);
		posicion = savedInstanceState.getInt("posicion");
	}
	
}
