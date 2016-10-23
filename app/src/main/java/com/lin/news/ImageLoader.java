package com.lin.news;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by lin on 2016/10/23.
 */
/*
多线程实现图片异步加载,也可以用AsyncTask
 */
public class ImageLoader  {

    private ImageView mImageView;
    private String murl;

    /*缓存代码开始*/
    private LruCache<String,Bitmap> mCache; //创建cache缓存
    public ImageLoader(){
        int MaxMemory= (int) Runtime.getRuntime().maxMemory();//获取手机最大可用空间
        int cacheSize=MaxMemory/4; //设置缓存空间为最大可用空间的四分之一
        mCache=new LruCache<String,Bitmap>(cacheSize){  //匿名内部类实现cache创建

            @Override
            protected int sizeOf(String key, Bitmap value) { //key为url
                //在每次存入缓存时调用   得到当前缓存图片大小
                return value.getByteCount();
            }
        };
    }
    public void addBitmapToCache(String url,Bitmap bitmap){  //添加到缓存
        if(getBitmapFromCache(url)==null) {
            mCache.put(url, bitmap);  //Map类型
        }
    }

    public Bitmap getBitmapFromCache(String url){ //从缓存中获取数据
        return mCache.get(url);
    }
/*缓存主体代码结束*/


    //以下是多线程实现加载
    private Handler mHandler=new Handler() {  //创建Handler对象实现通信 接收来自子线程的Bitmap
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mImageView.getTag().equals(murl)) { //验证图片，只有图片的tag与传如的图片Url相同时才在相应ImageView显示图片。防止图片错位
                mImageView.setImageBitmap((Bitmap) msg.obj); //设置图标
            }
        }
    };


    public void showImageBythread(ImageView imageView, final String url){

         mImageView=imageView; //将对应控件ImageView保留到成员变量 ImageView
        murl=url;

        new Thread(){  //创建子线程
            @Override
            public void run() {
                super.run();
                Bitmap bitmap=getBitmapFromUrl(url);  //调用自定义的getBitmapFromUrl 得到 bitmap
                Message message=Message.obtain();//获得通信 这种方式创建的message可以使用现有的message和回收到的message，可以提高messageMAnager的效率
                message.obj=bitmap;
                mHandler.sendMessage(message);//将bitmap发送给主线程的Handler

            }
        }.start(); //线程启动

    }


    public Bitmap getBitmapFromUrl(String urlString){
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url=new URL(urlString);
            URLConnection connection= (URLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);//解析URL指定的流成Bitmap

            //connection.disconnect();//释放资源 连接消除

            return bitmap;
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        } finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    //以下用AsyncTask实现异步
     public void showImageByAsyncTask(ImageView imageView,String url){
         Bitmap bitmap=getBitmapFromCache(url);//读取缓存中图片
         if(bitmap==null) {  //如果缓存中无url对应的图片则下载
             new NewsAsyncTask(imageView, url).execute(url); //启动线程下载
         }else{ //有则直接用
             imageView.setImageBitmap(bitmap); //由于这里在主线程可以直接修改UI
         }
    }

     private  class NewsAsyncTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView mImageView;
         private String murl;

         public NewsAsyncTask(ImageView imageView,String url){
             mImageView=imageView;
             murl=url;
         }

        @Override
        protected Bitmap doInBackground(String... strings) {
           // murl=strings[0];
            String url=strings[0];
            //从网络获取图片
            Bitmap bitmap= getBitmapFromUrl(url);
            if(bitmap!=null){
                addBitmapToCache(url ,bitmap); //存入缓存
            }
            return bitmap;
        }

         @Override
         protected void onPostExecute(Bitmap bitmap) {
             super.onPostExecute(bitmap);

             if(mImageView.getTag().equals(murl)){
             mImageView.setImageBitmap(bitmap); //检查图片放在正确位置
             }
         }
     }



}
