package com.aaksoft.toar.localdb.utils;

/*
    Created By Aasharib
    on
    2 March, 2019
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/*
    This class makes Bitmap serializable hence help in passing Bitmap b/w fragments

    This is used once we capture image in bitmap format and pass it to picture preview
    fragment for saving or deleting.
*/

public class BitmapDataObject implements Serializable {

    private Bitmap currentImage;

    public BitmapDataObject(Bitmap bitmap)
    {
        currentImage = bitmap;
    }

    //Converting bitmap in byte array
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        out.writeInt(byteArray.length);
        out.write(byteArray);
    }

    //Converting byte array in bitmap
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int bufferLength = in.readInt();
        byte[] byteArray = new byte[bufferLength];
        int pos = 0;
        do {
            int read = in.read(byteArray, pos, bufferLength - pos);

            if (read != -1) {
                pos += read;
            } else {
                break;
            }
        } while (pos < bufferLength);
        currentImage = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
    }

    public Bitmap getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Bitmap currentImage) {
        this.currentImage = currentImage;
    }
}
