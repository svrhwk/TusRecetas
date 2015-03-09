package com.example.tusrecetas;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class EditRecetaActivity extends Activity {

	private static final String LISTA_ING = "lista de ingredientes";
	private static final String LISTA_PASOS = "lista de pasos";

	private static final String LISTA_ET_ING = "lista de ingredientes";
	private static final String LISTA_ET_PASOS = "lista de pasos";

	private static final int ING_INTERNAL_ID = 10000;
	private static final int PASOS_INTERNAL_ID = 20000;
	private static final int ING_ID = 1;
	private static final int PASOS_ID = 2;

	private static final String RECETA_POSICION_BUNDLE = "posicion";
	private static final String RECETA_BUNDLE = "receta";
	private static final String FOCUSED_ID_BUNDLE = "focusedId";
	private static final String TEXT_SELECTION_START_BUNDLE = "TextSelectionStart";
	private static final String TEXT_SELECTION_END_BUNDLE = "TextSelectionEnd";

	private MyArrayList<String> listaIngredientes;
	private MyArrayList<String> listaPasos;

	MyArrayList<EditText> lista_et_ing;
	MyArrayList<EditText> lista_et_pasos;

	Receta receta;
	int posicion;
	int focused_pos;
	int focused_text_selection_start;
	int focused_text_selection_end;
	View focused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receta2);
		// setContentView(R.layout.activity_edit_receta);
		try {
			if (savedInstanceState != null) {
				String recetaSerialized = savedInstanceState
						.getString(RECETA_BUNDLE);
				Gson gson = new Gson();
				receta = gson.fromJson(recetaSerialized, Receta.class);
				posicion = savedInstanceState.getInt(RECETA_POSICION_BUNDLE);
				focused_pos = savedInstanceState.getInt(FOCUSED_ID_BUNDLE);
				// focused_text_selection_start =
				// savedInstanceState.getInt(TEXT_SELECTION_START_BUNDLE);
				// focused_text_selection_end =
				// savedInstanceState.getInt(TEXT_SELECTION_END_BUNDLE);

			} else {
				cargarReceta();
			}
			Log.d("EditRecetaActivity", "onCreate, ingSize:"
					+ receta.getIngredientes().size());
			lista_et_ing = new MyArrayList<EditText>();
			lista_et_pasos = new MyArrayList<EditText>();

			TextView tvNombreReceta = (TextView) findViewById(R.id.tvNombreReceta2);
			tvNombreReceta.setText(receta.getNombre());

			ExpandableListView elvIngredientes = (ExpandableListView) findViewById(R.id.elvIngredientes);
			ExpandableListView elvPasos = (ExpandableListView) findViewById(R.id.elvPasos);
			ExpandableListViewAdapter ingredientesAdapter = new ExpandableListViewAdapter(this, receta.getIngredientes(), lista_et_ing, ING_INTERNAL_ID, LISTA_ING,	R.layout.receta_item_rowlayout_prueba, R.layout.lista_parent);
			ExpandableListViewAdapter pasosAdapter = new ExpandableListViewAdapter( this, receta.getPasos(), lista_et_pasos, PASOS_INTERNAL_ID,	LISTA_PASOS, R.layout.receta_item_rowlayout_prueba,	R.layout.lista_parent);
			elvIngredientes.setAdapter(ingredientesAdapter);
			elvPasos.setAdapter(pasosAdapter);

//			LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View rowView = inflater.inflate(R.layout.receta_item_rowlayout_prueba, null);

//			if (savedInstanceState != null) {
//
//				for (int i = 0; i < lista_et_ing.size(); i++) {
//					EditText et = lista_et_ing.get(i);
//					int tag = (Integer) et.getTag();
//					if (tag == focused_pos) {
//						et.requestFocus();
//
//					}
//					// TODO seleccion del texto
//				}
//
//			}

			elvIngredientes.setOnGroupClickListener(new OnGroupClickListener() {

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					if (focused != null) {
						focused.clearFocus();
					}
					return false;
				}
			});

			elvPasos.setOnGroupClickListener(new OnGroupClickListener() {

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					if (focused != null) {
						focused.clearFocus();
					}
					return false;
				}
			});

			// ListView lvIngredientes =
			// (ListView)findViewById(R.id.lvIngredientes);
			// ListView lvPasos = (ListView)findViewById(R.id.lvPasos);
			// RecetaAdapter ingredientesAdapter = new
			// RecetaAdapter(this,R.layout.receta_item_rowlayout,receta.getIngredientes());
			// RecetaAdapter pasosAdapter = new
			// RecetaAdapter(this,R.layout.receta_item_rowlayout,receta.getPasos());
			// lvIngredientes.setAdapter(ingredientesAdapter);
			// lvPasos.setAdapter(pasosAdapter);

		} catch (Exception e) {
			Log.e("RecetaActivity", "onCreate: " + e.getMessage());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receta, menu);
		return true;
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

	public class GroupHolder {
		public ImageView mIV;
		public TextView mtv;

		public Button mBtn;
	}

	public class ViewHolder {
		public EditText mEditText;
		public Button mBtnEdit;
		public Button mBtnEliminar;
	}

	public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

		private MyArrayList<String> lista;
		private MyArrayList<EditText> lista_et;
		private String listName;
		private int childResource;
		private int parentResource;
		private int internal_id;

		// private Context context;
		public ExpandableListViewAdapter(Context context,
				MyArrayList<String> lista, MyArrayList<EditText> lista_et,
				int internal_id, String listName, int childResource,
				int parentResource) {
			// this.context = context;
			this.lista = lista;
			this.listName = listName;
			this.childResource = childResource;
			this.parentResource = parentResource;
			this.lista_et = lista_et;
			this.internal_id = internal_id;

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
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, final ViewGroup parent) {
			View rowView = convertView;
			try {
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(childResource, null);
					ViewHolder viewHolder = new ViewHolder();
					viewHolder.mEditText = (EditText) rowView.findViewById(R.id.etRecetaRow2);
					viewHolder.mBtnEdit = (Button) rowView.findViewById(R.id.btnEditarOK2);
					viewHolder.mBtnEliminar = (Button) rowView.findViewById(R.id.btnEliminarOK2);

					rowView.setTag(viewHolder);
				}

				final ViewHolder holder = (ViewHolder) rowView.getTag();

				// final EditText edittext = (EditText)
				// convertView.findViewById(R.id.etRecetaRow2);
				// Button btnEdit = (Button)
				// convertView.findViewById(R.id.btnEditarOK2);
				// Button btnEliminar = (Button)
				// convertView.findViewById(R.id.btnEliminarOK2);
				holder.mEditText.setText(lista.get(childPosition));
				holder.mEditText.setTag(Integer.valueOf(childPosition
						+ internal_id));
				holder.mBtnEliminar.setTag(childPosition);

				// if(childPosition==0){
				// lista_et.removeAll();
				// }
				// lista_et.add(edittext);

				

				holder.mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						try {

							int index = (Integer) v.getTag();
							if (!hasFocus) {

								lista.remove(index);
								lista.add(index, holder.mEditText.getText().toString());
								// btnEdit.setVisibility(View.GONE);

							} else {
								// btnEdit.setVisibility(View.VISIBLE);
								focused = v;
							}
						} catch (Exception e) {
							Log.e("getView", "onFocusChange(): "+ e.getMessage());
						}
					}
				});

				holder.mBtnEliminar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO puede que haya algo que hacer aquí si revienta
						// algo
						int p = (Integer) v.getTag();
						// lista_et.remove(p);
						lista.remove(p);
						notifyDataSetChanged();
					}
				});

			} catch (Exception e) {
				Log.e("getChildView", "" + e.getMessage());
			}
			return rowView;

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
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View rowView = convertView;
			try {
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) getBaseContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(parentResource, null);
					GroupHolder viewHolder = new GroupHolder();
					viewHolder.mBtn = (Button) rowView
							.findViewById(R.id.btnAdd);
					viewHolder.mtv = (TextView) rowView
							.findViewById(R.id.tvCategoria);
					viewHolder.mIV = (ImageView) rowView
							.findViewById(R.id.imageView1);

					rowView.setTag(viewHolder);
				}
				GroupHolder holder = (GroupHolder) rowView.getTag();
				holder.mBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO puede que haya algo que hacer aquí si revienta
						// algo

						lista.add("");

						notifyDataSetChanged();
					}
				});
				holder.mtv.setText(listName);
				holder.mIV.setImageResource(R.drawable.ic_list_parent);
			} catch (Exception e) {
				Log.e("MainActivity", "getGroupView: " + e.getMessage());
			}
			return rowView;
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

	
//	@Override
//	public void finish() { // //el clearfocus es para que guarde los ultimos
//							// cambios que se han hecho en la receta (se guardan
//							// cuando se cambia el focus)
//		try {
//			focused.clearFocus();
//		} catch (Exception e) {
//			Log.e("MainActivity", "finish(): " + e.getMessage());
//		}
//		listaIngredientes = new MyArrayList<String>();
//		listaPasos = new MyArrayList<String>();
//		for (int i = 0; i < lista_et_ing.size(); i++) {
//			EditText et = lista_et_ing.get(i);
//			if (et.hasFocus()) {
//				focused_pos = (Integer) et.getTag();
//				focused_text_selection_start = et.getSelectionStart();
//				focused_text_selection_end = et.getSelectionEnd();
//			}
//			listaIngredientes.add(et.getText().toString());
//		}
//		for (int i = 0; i < lista_et_pasos.size(); i++) {
//			EditText et = lista_et_pasos.get(i);
//			if (et.hasFocus()) {
//				focused_pos = (Integer) et.getTag(); //
//				focused_text_selection_start = et.getSelectionStart(); //
//				focused_text_selection_end = et.getSelectionEnd();
//			}
//			listaPasos.add(et.getText().toString());
//		}
//		receta.setIngredientes(listaIngredientes);
//		receta.setPasos(listaPasos);
//
//		Gson gson = new Gson();
//		String recetaSerializada = gson.toJson(receta);
//		Intent intent = new Intent().putExtra(RECETA_BUNDLE, recetaSerializada)
//				.putExtra(RECETA_POSICION_BUNDLE, posicion);
//		setResult(Activity.RESULT_OK, intent);
//		Toast.makeText(this, "receta editada, se supone", Toast.LENGTH_LONG)
//				.show();
//		super.finish();
//	}
	 

	public void clickbtn(View v) {
		listaIngredientes = receta.getIngredientes();
		String str = "";
		for (int i = 0; i < listaIngredientes.size(); i++) {
			str = str + listaIngredientes.get(i) + "\n";
		}
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_edit_receta2);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

//		Log.d("EditRecetaActivity", "onSave, nomod, ingSize:" + receta.getIngredientes().size());
//		Log.d("EditRecetaActivity", "onSave, etSize:" + lista_et_ing.size());
//
//		
//		listaIngredientes = new MyArrayList<String>();
//		listaPasos = new MyArrayList<String>();
//		
//		for (int i = 0; i < lista_et_ing.size(); i++) {
//			EditText et = lista_et_ing.get(i);
//			if (et.hasFocus()) {
//				focused_pos = (Integer) et.getTag();
//				focused_text_selection_start = et.getSelectionStart();
//				focused_text_selection_end = et.getSelectionEnd();
//			}
//			listaIngredientes.add(et.getText().toString());
//		}
//
//		for (int i = 0; i < lista_et_pasos.size(); i++) {
//			EditText et = lista_et_pasos.get(i);
//			if (et.hasFocus()) {
//				focused_pos = (Integer) et.getTag();
//				focused_text_selection_start = et.getSelectionStart();
//				focused_text_selection_end = et.getSelectionEnd();
//			}
//			listaPasos.add(et.getText().toString());
//		}
//		receta.setIngredientes(listaIngredientes);
//		receta.setPasos(listaPasos);
//
//		Log.d("EditRecetaActivity", "onSave, ingSize:"				+ receta.getIngredientes().size());
//		
//		for (int i = 0; i < receta.getIngredientes().size(); i++) {
//			Log.d("EditRecetaActivity", "onSave, listaIng[" + i + "]: "	+ receta.getIngredientes().get(i));
//		}

		if (focused != null && (focused instanceof EditText)) {
			focused_pos = (Integer) focused.getTag();
			outState.putInt(FOCUSED_ID_BUNDLE, focused_pos);
		}
		Gson gson = new Gson();
		String recetaSerializada = gson.toJson(receta);

		outState.putString(RECETA_BUNDLE, recetaSerializada);
		outState.putInt(RECETA_POSICION_BUNDLE, posicion);
		// outState.putInt(TEXT_SELECTION_START_BUNDLE,
		// focused_text_selection_start);
		// outState.putInt(TEXT_SELECTION_END_BUNDLE,
		// focused_text_selection_end);
	}

	// @Override
	// protected void onRestoreInstanceState(Bundle inState)
	// {
	// super.onRestoreInstanceState(inState);
	//
	// posicion = inState.getInt(RECETA_POSICION_BUNDLE);
	// String serialized = inState.getString(RECETA_BUNDLE);
	// Gson gson = new Gson();
	// receta = gson.fromJson(serialized, Receta.class);
	// }
}
