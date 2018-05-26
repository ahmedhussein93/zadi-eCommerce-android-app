package com.cafateria.handler;

import android.os.Environment;

import java.io.File;

/**
 * Created  on 18/02/2018.
 * *  *
 */
public class Paths {

    public static String rootDir = Environment.getExternalStorageDirectory() + File.separator + ".cafateria/";
    public static String imagesDir = rootDir + File.separator + "images/";
    //public static String profileImagesDir = imagesDir + File.separator + "profile";
    //public static String ProductsImagesDir = imagesDir + File.separator + "products";


    public static void createPaths() {
        //root dir
        createDir(new File(rootDir));
        createDir(new File(imagesDir));
        //createDir(new File(profileImagesDir));
        //createDir(new File(ProductsImagesDir));
    }

    private static void createDir(File dir) {
        try {
            if (dir.mkdirs()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
