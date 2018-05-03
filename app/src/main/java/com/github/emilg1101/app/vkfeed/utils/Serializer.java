package com.github.emilg1101.app.vkfeed.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static <T> void serialize(Context context, T object) {
        try {
            FileOutputStream fos = context.openFileOutput(object.getClass().getName()+".out", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static <T> T deserialize(Context context, T object) {
        try {
            FileInputStream fis = context.openFileInput(object.getClass().getName()+".out");
            ObjectInputStream is = new ObjectInputStream(fis);
            object = (T) is.readObject();
            is.close();
            fis.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            return object;
        }
    }
}
