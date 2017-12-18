package com.example.lkm.ms_termproject_001;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ReviewMyAdapter extends BaseAdapter {

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<MyItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_custom, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.userImg) ;
        TextView tv_name = (TextView) convertView.findViewById(R.id.userName) ;
        TextView tv_contents = (TextView) convertView.findViewById(R.id.userContents) ;
        RatingBar userRating = (RatingBar)convertView.findViewById(R.id.userRating);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        MyItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.kakao_default_profile_image)
                .showImageForEmptyUri(R.drawable.kakao_default_profile_image)
                .showImageOnFail(R.drawable.kakao_default_profile_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        ImageLoader.getInstance().displayImage(myItem.getIcon(), iv_img, options);

        tv_name.setText(myItem.getName());
        tv_contents.setText(myItem.getContents());
        userRating.setRating(myItem.getRating());
        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String img, String name, String contents,float rating) {

        MyItem mItem = new MyItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setIcon(img);
        mItem.setName(name);
        mItem.setContents(contents);
        mItem.setRating(rating);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
    public void removeAll(){
        mItems.clear();
    }
}