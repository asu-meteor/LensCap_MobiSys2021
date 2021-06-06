package com.epicgames.ue4Network;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploader {
    private static int iii = 0;
    private static List<Long> nb_end_frame = new ArrayList<>();
    private static List<Long> nb_end_pose = new ArrayList<>();
    private static List<Long> nb_end_light = new ArrayList<>();
    private static List<Long> nb_end_point = new ArrayList<>();
    private static List<Long> nb_end_face = new ArrayList<>();
    public static void uploadFile(File file, Activity context) throws IOException {
        // create upload service client
        FileUploadService service = ((AppRetro)context.getApplication()).getRetrofit().create(FileUploadService.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
//    File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("text/plain; charset=utf-8"),
                        //MediaType.parse("image/png"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "nameOfFile.txt";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        //LocalDateTime myObj2 = LocalDateTime.now();
        //Log.v("Upload send time", String.valueOf(System.currentTimeMillis()));
        final long t_start = System.currentTimeMillis();
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                //Date currentDate = new Date();
                //Instant it = Instant.now();
                //Log.v("Upload Success receive time", String.valueOf(it));
                //Log.v("Upload Success receive time", String.valueOf();
                //Log.v("Upload", "success");
                try {
                    long t_end = System.currentTimeMillis();
                    /*
                    if (iii == 0){
                        nb_end_pose.add(t_end - t_start);
                        iii = 1;
                        long sum = 0;
                        for (long i : nb_end_pose) {
                            sum += i;
                        }
                        Log.v("Lenscap1 upload pose", "success" + " sum: " + sum);
                    } else if (iii == 1){
                        nb_end_light.add(t_end - t_start);
                        iii = 2;
                        long sum = 0;
                        for (long i : nb_end_light) {
                            sum += i;
                        }
                        Log.v("Lenscap1 upload light", "success" + " sum: " + sum);
                    } else if (iii == 2){
                        nb_end_point.add(t_end - t_start);
                        iii = 3;
                        long sum = 0;
                        for (long i : nb_end_point) {
                            sum += i;
                        }
                        Log.v("Lenscap1 upload pointcloud", "success" + " sum: " + sum);
                    } else if (iii == 3) {
                        nb_end_face.add(t_end - t_start);
                        iii = 4;
                        long sum = 0;
                        for (long i : nb_end_face) {
                            sum += i;
                        }
                        Log.v("Lenscap1 upload face", "success" + " sum: " + sum);
                    } else {
                        nb_end_frame.add(t_end - t_start);
                        iii = 0;
                        long sum = 0;
                        for (long i : nb_end_frame) {
                            sum += i;
                        }
                        Log.v("Lenscap1 upload frame", "success" + " sum: " + sum);
                    }*/
                    nb_end_face.add(t_end - t_start);
                    long sum = 0;
                    for (long i : nb_end_face) {
                        sum += i;
                    }
                    Log.v("Lenscap1 upload face", "success" + " sum: " + sum);
                    //Log.v("lenscap1 Upload Success roundtrip", String.valueOf(t_end - t_start));
                    response.body().string();
                    //Log.v("Upload", response.body().string());
                } catch (IOException e) {
                    Log.e("Upload", "failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    public static void writeImageInformation(Image image, String path, Context context) throws IOException {
        byte[] data = null;
        data = NV21toJPEG(YUV_420_888toNV21(image),
                image.getWidth(), image.getHeight());
        BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
        bos.write(data);
        bos.flush();
        bos.close();
    }

    public static void writeBitmapImageInfo (Bitmap bt, String path, Context context) throws IOException {
        int size = bt.getRowBytes()*bt.getHeight();
        ByteBuffer bb = ByteBuffer.allocate(size);
        bt.copyPixelsToBuffer(bb);
        byte[] bytes = new byte[size];
        try {
            bb.get(bytes, 0, bytes.length);
        } catch (BufferUnderflowException e) {
        }
    }

    public static void writeByteToTXT (byte[] ddd, String path, Context context) throws IOException {
        //BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
        //bos.write(ddd);
        //bos.flush();
        //bos.close();
        OutputStream os = new FileOutputStream(path);
        os.write(ddd);
        os.close();
    }

    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }
}
