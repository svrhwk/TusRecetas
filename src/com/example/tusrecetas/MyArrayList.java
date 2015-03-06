package com.example.tusrecetas;

import java.util.ArrayList;

public class MyArrayList<E> extends ArrayList<E> {

	
	public E replace(int index, E element){
		E aux = remove(index);
		add(index, element);
		return aux;
	}
	
	public void removeAll(){
		while(size()>0){
			remove(0);
		}
	}
}
