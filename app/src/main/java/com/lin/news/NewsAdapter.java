package com.lin.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lin on 2016/10/22.
 */
//创建适配器
public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater mInflater; //用于加载Item
    private ImageLoader mImageLoader; //只用一个对对象保证了只会有一个cache缓存

    public NewsAdapter(Context context,List<NewsBean> data){
        mList=data;
        mInflater=LayoutInflater.from(context);
        mImageLoader=new ImageLoader(); //只用一个对对象保证了只会有一个cache缓存
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;//内部类对象
        if(view==null){
            viewHolder=new ViewHolder();
            view=mInflater.inflate(R.layout.item_layout,null); //通过mInflater得到View
            viewHolder.ivIcon= (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tvTitle= (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tvContent= (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder); //设置标签
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        //设置初始化是时默认值
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);//设置图片初始为android图标
        Log.i("iconURL---->",mList.get(position).newsIconUrl);
        String url=mList.get(position).newsIconUrl;
        viewHolder.ivIcon.setTag(url); //作为加载图片时验证而设置
        //new ImageLoader().showImageBythread(viewHolder.ivIcon,mList.get(position).newsIconUrl);//用多线程方式加载url对应的图片图标
        //new ImageLoader().showImageByAsyncTask(viewHolder.ivIcon,mList.get(position).newsIconUrl); //用AsyncTask的方式  由于每次都new 不能实现缓存效果
        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon,mList.get(position).newsIconUrl); //mImageLoader保证了只有一个cache缓存
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);//从newsBeanList中获取标题
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return view;
    }

    //内部类
    class ViewHolder{  //每一个Item对应一个ViewHolder
        public TextView tvTitle,tvContent;
        public ImageView ivIcon;

    }

}
