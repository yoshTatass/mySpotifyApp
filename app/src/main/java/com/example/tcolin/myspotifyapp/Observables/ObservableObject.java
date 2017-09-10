package com.example.tcolin.myspotifyapp.Observables;

import java.util.Observable;

/**
 * Created by thibautcolin on 08/09/2017.
 */

public class ObservableObject extends Observable {

    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance() {
        return instance;
    }

    private ObservableObject() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
