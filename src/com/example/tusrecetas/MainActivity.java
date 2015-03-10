package com.example.tusrecetas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity {

	ArrayList<Receta> recetas;
	ListView listaRecetas;
	SelectionAdapter datosAdapter;
	ArrayList<Integer> selected;
	private static final int EDITAR_RECETA = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			
			recetas = new ArrayList<Receta>();
			selected = new ArrayList<Integer>();
			cargarDatos();
			listaRecetas = (ListView) findViewById(R.id.lvListaRecetas);
			String[] values = new String[recetas.size()];
			for(int i=0; i<recetas.size();i++){
				values[i] = recetas.get(i).toString();
			}
			datosAdapter = new SelectionAdapter(this, R.layout.receta_rowlayout, recetas);
			listaRecetas.setAdapter(datosAdapter);
			listaRecetas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			
			listaRecetas.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int posicion,long arg3) {
					Intent intent = new Intent(MainActivity.this,EditRecetaActivity.class);
					Receta receta = recetas.get(posicion);
					Gson gson = new Gson();
					String recetaSerializada = gson.toJson(receta);
					intent.putExtra("receta", recetaSerializada);
					intent.putExtra("posicion", posicion);
					startActivityForResult(intent,EDITAR_RECETA);
				}
			});
			
			
//			listaRecetas.setOnItemClickListener(new OnItemClickListener() {
//
//			      @Override
//			      public void onItemClick(AdapterView<?> parent, final View view,
//			          int position, long id) {
//			        final int item =  position;
//			        view.animate().setDuration(2000).alpha(0)
//			            .withEndAction(new Runnable() {
//			              @Override
//			              public void run() {
//			                recetas.remove(item);
//			                datosAdapter.notifyDataSetChanged();
//			                view.setAlpha(1);
//			              }
//			            });
//			      }
//			});
			      
			listaRecetas.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				private int nr = 0;

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					if(nr == 1){
						MenuItem item = menu.findItem(R.id.item_edit);
						item.setVisible(true);
						return true;
					} else {
						MenuItem item = menu.findItem(R.id.item_edit);
						item.setVisible(false);
						return true;
					}
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					datosAdapter.clearSelection();
					nr=0;
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					nr = 0;
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.contextual_menu, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
							//EDITAR
							case R.id.item_edit:
								Intent intent = new Intent(MainActivity.this,
										EditRecetaActivity.class);
								Gson gson = new Gson();
								selected = new ArrayList<Integer>();
								selected.addAll(datosAdapter
										.getCurrentCheckedPosition());
								nr = 0;
								datosAdapter.clearSelection();
								mode.finish();
								int posicion = selected.get(0);
								Receta receta = recetas.get(posicion);
								String recetaSerializada = gson.toJson(receta);
								intent.putExtra("receta", recetaSerializada);
								intent.putExtra("posicion", posicion);
								startActivityForResult(intent,EDITAR_RECETA);
								return true;
							//BORRAR ITEMS
							case R.id.item_delete:
								selected = new ArrayList<Integer>();
								selected.addAll(datosAdapter
										.getCurrentCheckedPosition());

								for (int i = selected.size() - 1; i >= 0; i--) {
									int p = selected.get(i);
									recetas.remove(p);
								}
								nr = 0;
								datosAdapter.clearSelection();
								mode.finish();
								return true;
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					// TODO Auto-generated method stub
					if (checked) {
						nr++;
						datosAdapter.setNewSelection(position, checked);
					} else {
						nr--;
						datosAdapter.removeSelection(position);
					}
					mode.setTitle(nr + " selected");
					mode.invalidate();

				}
			});
			
			listaRecetas.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub

					listaRecetas.setItemChecked(position,
							!datosAdapter.isPositionChecked(position));
					return false;
				}
			});
			
		} catch (Exception e) {
			Log.e("MainActivity","onCreate: "+e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class SelectionAdapter extends ArrayAdapter<Receta> {
		private Map<Integer, Boolean> mSelection = new TreeMap<Integer, Boolean>();
		private Context context;
		private int resource;
		
		public SelectionAdapter(Context context, int resource, List<Receta> list){
			super(context, resource, list);
			this.context = context;
			this.resource = resource;
		}
		
		
		public void setNewSelection(int position, boolean value) {
			mSelection.put(position, value);
			notifyDataSetChanged();
		}

		public boolean isPositionChecked(int position) {
			Boolean result = mSelection.get(position);
			return result == null ? false : result;
		}

		public Set<Integer> getCurrentCheckedPosition() {
			return mSelection.keySet();
		}

		public void removeSelection(int position) {
			mSelection.remove(position);
			notifyDataSetChanged();
		}
		public void clearSelection() {
			mSelection = new TreeMap<Integer, Boolean>();
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(resource, parent, false);
			try {
				
				TextView tvRecetaRow = (TextView) rowView.findViewById(R.id.tvReceta);
				
				final String nombreReceta = recetas.get(position).toString();
				tvRecetaRow.setText(nombreReceta);
				rowView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
				if (mSelection.get(position) != null) {
					// this is a selected position so make it red					
					rowView.setBackgroundColor(getResources().getColor(R.color.blue));
				}

			} catch (Exception e) {
				Log.e("getView",""+e.getMessage());
			}
			return rowView;
		}
		
	}
	
	/*
  	private void LoadXML(){
  		ArrayList<String> ingredientes = new ArrayList<String>();
  		ArrayList<String> pasos = new ArrayList<String>();
  		String nombreReceta="";
  		String tagName;

  		try {
  			//Creamos un arraylist con los productos, lo cargamos desde el archivo XML:
  			recetas = new ArrayList<Receta>();
  			
  			//Obtenemos un parser XmlPull asociado a nuestro XML
  			XmlPullParser parser=getResources().getXml(R.xml.recetas);

  			//Mediante el objeto parser y sus funciones, leemos el archivo XML e identificamos nodos y atributos:
  			int eventType = parser.getEventType();
  			
  			//Recorremos el xml:
  			while(eventType != XmlPullParser.END_DOCUMENT) {
  				switch(eventType) {
  					//Inicio del documento: START_DOCUMENT
  					case XmlPullParser.START_DOCUMENT:
  						//No hago nada
  						break;
  						
  					//Inicio de un TAG: START_TAG
  					case XmlPullParser.START_TAG:
  						//Consigo el nombre del TAG:
  						tagName = parser.getName();
  						
  						//Comprobamos el tipo de nodo:
  						if(tagName.equalsIgnoreCase("receta")){
  							nombreReceta = parser.getAttributeValue(null, "nombre");
  						}
  						
  						if(tagName.equalsIgnoreCase("ingredientes")) {
  							ingredientes = new ArrayList<String>();
  						}
  						
  						if(tagName.equalsIgnoreCase("ingrediente")) {
  							//Obtenemos el ingrediente, y lo añadimos a la lista de ingredientes
  							String ingrediente = parser.nextText();
  							ingredientes.add(ingrediente);
  						}
  						
  						if(tagName.equalsIgnoreCase("pasos")) {
  							pasos = new ArrayList<String>();
  						}
  						
  						if(tagName.equalsIgnoreCase("paso")) {
  							//Obtenemos el paso, y lo añadimos a la lista de pasos
  							String paso = parser.nextText();
  							pasos.add(paso);
  						}
  						
  						break;
  						
					//Fin de un TAG: END_TAG
  					case XmlPullParser.END_TAG:
  						//Nombre del TAG
  						tagName = parser.getName();
  						//Comprobamos el tipo:
  						if(tagName.equalsIgnoreCase("receta")){
  							//Creamos un nuevo objeto receta y lo añadimos al ArrayList:
  							recetas.add(new Receta(ingredientes, pasos, nombreReceta));
  						}
  						
  				}
  				// Siguiente evento:
  				eventType = parser.next();
  			}
  			//Ya hemos recorrido el xml, y tenemos listo el arraylist con la información
  		}catch (Exception e) {
  			Log.e("MainActivity","LoadXML: "+e.getMessage());
  		}
  	}
	*/
  	
	public void btnClick(View view){
  		//coger las recetas
  		//pasarlas a json
  		//escribirlas en el log
  		try {
  			
			salvarDatos();
			
		} catch (Exception e) {
			Log.e("MainActivity","btnClick: "+e.getMessage());
		}
  	}
	public void toPrueba(View view){
		Intent intent = new Intent(MainActivity.this,Prueba.class);
		Receta receta = recetas.get(0);
		Gson gson = new Gson();
		String recetaSerializada = gson.toJson(receta);
		intent.putExtra("receta", recetaSerializada);
		intent.putExtra("posicion", 0);
		startActivity(intent);
	}
  	
  	protected void salvarDatos(){
  		try {
  			Gson gson = new Gson();
			String PATH=String.format("%s/%s", Environment.getExternalStorageDirectory().getPath(), getString(R.string.app_preferences));
			File SDCardRoot = new File(PATH);
			//Si no existe aun la carpeta, la crea
			if(!SDCardRoot.exists()) SDCardRoot.mkdir();
			
			String filename = String.format("%s/recetas.xml", PATH);
			File file = new File(filename);
			if(!file.exists()) file.createNewFile();
			FileOutputStream fichero = new FileOutputStream(filename);
			XmlSerializer xmlSerializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8",true);
			xmlSerializer.startTag(null, "recetas");
			for(Receta receta: recetas){
				xmlSerializer.startTag(null, "receta");
				xmlSerializer.text(gson.toJson(receta));
				xmlSerializer.endTag(null, "receta");
			}
			xmlSerializer.endTag(null, "recetas");
			xmlSerializer.endDocument();
			xmlSerializer.flush();
			String dataWrite = writer.toString();
			fichero.write(dataWrite.getBytes());
			fichero.close();
		} catch (Exception e) {
			Log.e("MainActivity","salvarDatos: "+e.getMessage());
			e.printStackTrace();
		}
  	}
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		if(requestCode==EDITAR_RECETA && resultCode==Activity.RESULT_OK){
			try {
				Bundle extras = data.getExtras();
				String serialized = extras.getString("receta");
				int posicion = extras.getInt("posicion");
				//AQUIJODER
				Gson gson = new Gson();
				Receta receta = gson.fromJson(serialized, Receta.class);
				recetas.remove(posicion);
				recetas.add(posicion, receta);
				
				((SelectionAdapter)listaRecetas.getAdapter()).notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Receta editada", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(getApplicationContext(), "Bad luck!", Toast.LENGTH_SHORT).show();
				Log.e("MainActivity","onActivityResult"+e.getMessage());
			}
  		}else{
  			Toast.makeText(getApplicationContext(), "Bad luck!", Toast.LENGTH_SHORT).show();
  		}
  	}
  	protected void cargarDatos(){
  		String tagName;

  		try {
  			Gson gson = new Gson();
			//Creamos un arraylist con los productos, lo cargamos desde el archivo XML:
			recetas = new ArrayList<Receta>();
			String PATH=String.format("%s/%s", Environment.getExternalStorageDirectory().getPath(), getString(R.string.app_preferences));
			//Obtenemos un parser XmlPull asociado a nuestro XML
			 
			File SDCardRoot = new File(PATH);
			//Si no existe aun la carpeta, la crea
			if(!SDCardRoot.exists()) SDCardRoot.mkdir();
			String filename = String.format("%s/recetas.xml", PATH);
			
			File file = new File(filename);
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser=factory.newPullParser();
			FileInputStream fis = new FileInputStream(file);
			parser.setInput(new InputStreamReader(fis));

  			//Mediante el objeto parser y sus funciones, leemos el archivo XML e identificamos nodos y atributos:
  			int eventType = parser.getEventType();
  			
  			//Recorremos el xml:
  			while(eventType != XmlPullParser.END_DOCUMENT) {
  				switch(eventType) {
  					//Inicio del documento: START_DOCUMENT
  					case XmlPullParser.START_DOCUMENT:
  						//No hago nada
  						break;
  						
  					//Inicio de un TAG: START_TAG
  					case XmlPullParser.START_TAG:
  						//Consigo el nombre del TAG:
  						tagName = parser.getName();
  						if(tagName.equalsIgnoreCase("receta")) {
  							//Obtenemos el ingrediente, y lo añadimos a la lista de ingredientes
  							String serialized = parser.nextText();
  							Receta receta = gson.fromJson(serialized, Receta.class);
  							recetas.add(receta);
  						}
  						break;
  				}
  				// Siguiente evento:
  				eventType = parser.next();
  			}
  			//Ya hemos recorrido el xml, y tenemos listo el arraylist con la información
  		}catch (Exception e) {
  			Log.e("MainActivity","LoadXML: "+e.getMessage());
  		}
  	}
}
