package utils;

import models.Product;

import java.io.*;
import java.nio.file.Paths;

public class FileUtils {
    static public void copyFile(Product product,String dirPath)
    {


        byte[] buffer = product.getImage();
        File newImage = new File(dirPath +((Product) product).getName() + ".png");
        if(newImage.exists())
        {
            return;
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(newImage);
            fos.write(buffer);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

