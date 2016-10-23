package com.lin.news;



import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private String URL="http://www.imooc.com/api/teacher?type=4&num=30";//json数据接口
   //www.imooc.com/api/teacher?type=4&num=30   http://www.imooc.com/api/teacher?type=4&num=30
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mListView= (ListView) findViewById(R.id.listView);

        new NewsAsyncTask().execute(URL);

    }


    /*
    将URL对应的Json数据转化为我们需要的NewsBean类型
     */
    private List<NewsBean> getJsondata(String url) {  //自定义获取json数据函数
        List<NewsBean> newsBeanList=new ArrayList<>();
        JSONObject jsonObject;
        NewsBean newsBean;
        try {
            String jsonString=readStream(new URL(url).openStream()); //得到Json文件内容
            //new URL(url).openStream() 和 url.openConnection().getInputStream();相同 返回值是InputStream方式
            Log.i("json---->",jsonString);

            jsonObject=new JSONObject(jsonString);
            JSONArray jsonArray=jsonObject.getJSONArray("data"); //一个json文件里面有data data里面有多个jsonObject，每个obj里面又有 id  name(标题)  picSmall(小图) picBig(大图) description(文字信息)
            for(int i=0;i<jsonArray.length();i++){ //取出所有内容
                jsonObject=jsonArray.getJSONObject(i);
                newsBean=new NewsBean();
                newsBean.newsIconUrl=jsonObject.getString("picSmall");//json文件中图片的URL
                newsBean.newsTitle=jsonObject.getString("name");
                newsBean.newsContent=jsonObject.getString("description");
                newsBeanList.add(newsBean);//添加到数组
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }//getJsondata


    /*
    通过is解析网页返回的数据
     */
    private String readStream(InputStream is) { //自定义流函数
        InputStreamReader isr;
        String result="";

        try {
            String line="";//一行一行读取
            isr = new InputStreamReader(is, "utf-8"); //将字节流转化为字符流 //设置字符集格式 utf-8
            BufferedReader br=new BufferedReader(isr);  //将字符流以buffer缓冲方式读取出来
            while((line=br.readLine())!=null){//读取一行
                result+=line; //将读到的信息存到result
            }

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    } //readStream


    /*
    实现网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>>{
                                        //<传入参数类型，返回进度，返回数据类型>  //这里返回是一个Bean对象集合

        @Override
        protected List<NewsBean> doInBackground(String... strings) {
            return getJsondata(strings[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBean) {  //接收到数据存在newsBean中
            super.onPostExecute(newsBean);
            NewsAdapter adapter=new NewsAdapter(MainActivity.this,newsBean); //创建适配器

            mListView.setAdapter(adapter);//加载适配器

        }
    }
}
