package com.example.tusrecetas;

import java.util.ArrayList;

public class Receta {
	private MyArrayList<String> ingredientes;
	private MyArrayList<String> pasos;
	private String nombre;
	
	public Receta(MyArrayList<String> ingredientes, MyArrayList<String> pasos, String nombre){
		this.ingredientes = ingredientes;
		this.pasos = pasos;
		this.nombre = nombre;
	}
	
	public Receta(){
		ingredientes = new MyArrayList<String>();
		pasos = new MyArrayList<String>();
		nombre = "";
	}
	
	public void addPaso(String paso){
		pasos.add(paso);
	}
	
	public void addIngrediente(String ingrediente){
		ingredientes.add(ingrediente);
	}
	
	public void removeUltimoPaso(){
		pasos.remove(pasos.size()-1);
	}
	
	public void removePaso(int index){
		pasos.remove(index);
	}
	
	public void removeUltimoIngrediente(){
		ingredientes.remove(ingredientes.size()-1);
	}
	
	public void removeIngrediente(int index){
		ingredientes.remove(index);
	}
	
	public MyArrayList<String> getPasos(){
		return pasos;
	}
	
	public MyArrayList<String> getIngredientes(){
		return ingredientes;
	}
	
	public void setPasos(MyArrayList<String> pasos){
		this.pasos = pasos;
	}
	public void setIngredientes(MyArrayList<String> ingredientes){
		this.ingredientes = ingredientes;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	@Override
	public String toString(){
		return nombre;
	}
	
}
