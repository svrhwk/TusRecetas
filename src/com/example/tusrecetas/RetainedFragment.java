package com.example.tusrecetas;

import android.os.Bundle;
import android.app.Fragment;

public class RetainedFragment<E> extends Fragment {
	 // data object we want to retain
    private E receta;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(E receta) {
        this.receta = receta;
    }

    public E getData() {
        return receta;
    }
}
