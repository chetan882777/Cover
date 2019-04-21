package com.chetan.projects.cover.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.chetan.projects.cover.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BitmapUtils {

    public static Bitmap scalDownBitmap2k(Bitmap bitmap, DisplayMetrics metrics) {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        float reduction = width / bitmap.getWidth();

        Log.v("--------------","--------------------reduction:"+reduction);
        width = (int) (bitmap.getWidth() * reduction);

        height = (int) (bitmap.getHeight() * reduction);


        Log.v("--------------","--------------------width:"+width);

        Log.v("--------------","--------------------height:"+height);

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);


      //  Log.v(TAG  , "--------------------------------------------stringed bitmap : " + temp);
        return temp;
    }

    public static Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);


        //    Log.v(TAG  , "-------------------------------------------- stringToBitmap bitmap :" + bitmap);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private static Context appContext;

    public static String encodeTobase64(Bitmap image) {

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }


    public static Bitmap decodeBase64(String input,Context context) {
        byte[] decodedByte = Base64.decode(input, 0);

        appContext = context;
        Boolean isSDPresent = android.os.Environment
                .getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED);

        File sdCardDirectory;
        if (isSDPresent) {
            // yes SD-card is present
            sdCardDirectory = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "IMG");

            if (!sdCardDirectory.exists()) {
                if (!sdCardDirectory.mkdirs()) {
                    Log.d("MySnaps", "failed to create directory");

                }
            }
        } else {
            // Sorry
            sdCardDirectory = new File(context.getCacheDir(),"");
        }



        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((1000 - 0) + 1) + 0;

        String nw = "IMG_" + timeStamp + randomNum+".txt";
        File image = new File(sdCardDirectory, nw);



        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {


            outStream = new FileOutputStream(image);
            outStream.write(input.getBytes());

            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.i("Compress bitmap path", image.getPath());
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }catch(OutOfMemoryError e){
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.colored_back_logo);

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            bitmap=null;
        }

        return bitmap;//BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        //return decodeFile(image);
    }

    private static Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=70;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale>=REQUIRED_SIZE && o.outHeight/scale>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);


            Boolean isSDPresent = android.os.Environment
                    .getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED);
            File sdCardDirectory;
            if (isSDPresent) {
                // yes SD-card is present
                sdCardDirectory = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "IMG");

                if (!sdCardDirectory.exists()) {
                    if (!sdCardDirectory.mkdirs()) {
                        Log.d("MySnaps", "failed to create directory");

                    }
                }
            } else {
                // Sorry
                sdCardDirectory = new File(appContext.getCacheDir(),"");
            }



            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());

            Random rand = new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((1000 - 0) + 1) + 0;

            String nw = "IMG_" + timeStamp + randomNum+".png";
            File image = new File(sdCardDirectory, nw);



            FileOutputStream out = null;
            try {
                out = new FileOutputStream(image);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String pathNew =compressImage(image.getAbsolutePath());
            Uri uri = Uri.parse(pathNew);
            Bitmap bitmap=null ;
            try {
                bitmap     = MediaStore.Images.Media.getBitmap(appContext.getContentResolver(),uri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bitmap;
        } catch (FileNotFoundException e) {}
        return null;
    }


    public static String compressImage(String imageUri) {

        String filePath = imageUri;//getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String getFilename() {
        /*File file = new File(Environment.getExternalStorageDirectory().getPath(), "IMG/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        */


        Boolean isSDPresent = android.os.Environment
                .getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED);

        File sdCardDirectory;
        if (isSDPresent) {
            // yes SD-card is present
            sdCardDirectory = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(),
                    "IMG/Images");

            if (!sdCardDirectory.exists()) {
                if (!sdCardDirectory.mkdirs()) {
                    Log.d("MySnaps", "failed to create directory");

                }
            }
        } else {
            // Sorry
            sdCardDirectory = new File(appContext.getCacheDir(),"");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        Random rand = new Random();

// nextInt is normally exclusive of the top value,
// so add 1 to make it inclusive
        int randomNum = rand.nextInt((1000 - 0) + 1) + 0;

        String nw = "img_" + timeStamp + randomNum+".jpg";
        File image = new File(sdCardDirectory, nw);

        String uriSting1 = (sdCardDirectory.getAbsolutePath() + "/" + nw);//System.currentTimeMillis() + ".jpg");

        return uriSting1;

    }

    public static Bitmap scalDownBitmapByHeightWidth(Bitmap bitmap , int width, int height){
        Bitmap result = bitmap;

        int widthDiff;

        int newHeight = bitmap.getHeight();

        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        int widthToSet = width;

        if(bitmapHeight > height && bitmapWidth > widthToSet){

            widthDiff = bitmapWidth - widthToSet;

            newHeight = bitmapHeight - widthDiff;

            if(newHeight <= 0){

                newHeight = bitmapHeight;
            }
        }else{
            widthToSet = bitmap.getHeight();
        }

        if(widthToSet > 0 && newHeight > 0) {

            result = Bitmap.createScaledBitmap(bitmap, widthToSet, newHeight, true);


        }else{
            Log.v("BitmapUtils" , "width*height < 0");

        }

        return result;
    }


    public static Uri bitmapToUriConverter(Context context, Bitmap mBitmap) {
        Uri uri = null;
        try {

            File file = new File(context.getFilesDir(), "OriginalImage"
                    + new Date().getTime() + ".jpeg");
            FileOutputStream out = context.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

}

