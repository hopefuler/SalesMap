package com.example.lkm.ms_termproject_001;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailFoodtruckActivity extends AppCompatActivity {

    int imgCount=0;

    ViewFlipper flipper;
    ToggleButton toggle_Flipping;
    Boolean img_check=true;

    double myLongitude = 0;  //경도
    double myLatitude = 0;   //위도
    double myAltitude = 0;   //고도

    double truckLongitude = 0;  //경도
    double truckLatitude = 0;   //위도
    String foodTruckId = "";

    TextView foodTruckName;
    TextView foodTruckDetailExample;
    ImageView detailFoodTruckPhoto;
    String photo = "";
    Bitmap bitmap;
    String phoneNumber;

    TextView foodTruckDistance,detailCategory,detailGeocoding,detailReviewCount,ratingDouble;
    ImageView detailFoodTruckCallBtn,DetailBookmark;
    ImageView googleMapSearch;
    ImageView reviewBtn;
    RatingBar detailStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_foodtruck);


        // ------- 이미지 슬라이드 관련 코드 start ------- //
        flipper= (ViewFlipper)findViewById(R.id.flipper);

        //imgCount는 이미지의 수

        imgCount=0;




//        for(int i=0;i<imgCount;i++){
//            ImageView img= new ImageView(this);
//            img.setImageResource(R.drawable.test_img_01+i); // 이미지 삽입
//            flipper.addView(img);
//        }
//        Animation showIn= AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
//
//        flipper.setInAnimation(showIn);
//        flipper.setOutAnimation(this, android.R.anim.fade_out);
//
//        flipper.setFlipInterval(3000);
//        flipper.startFlipping();


        // ------- 이미지 슬라이드 관련 코드 end ------- //

        Intent intent = getIntent();
        myLongitude = Double.parseDouble(intent.getExtras().getString("longitude"));
        myLatitude = Double.parseDouble(intent.getExtras().getString("latitude"));
        myAltitude = Double.parseDouble(intent.getExtras().getString("altitude"));
        foodTruckId = intent.getExtras().getString("foodTruckId");

        foodTruckName = (TextView) findViewById(R.id.foodTruckName);
        foodTruckDetailExample = (TextView) findViewById(R.id.detailExplain);
        //detailFoodTruckPhoto = (ImageView) findViewById(R.id.detailFoodTruckPhoto);
        detailFoodTruckCallBtn = (ImageView) findViewById(R.id.detailFoodTruckCallBtn);
        DetailBookmark = (ImageView)findViewById(R.id.DetailBookmark);
        googleMapSearch =(ImageView)findViewById(R.id.googleMapSearch);
        foodTruckDistance = (TextView)findViewById(R.id.distance);
        detailCategory = (TextView)findViewById(R.id.detailCategory);
        detailGeocoding = (TextView)findViewById(R.id.detailGeocoding);
        detailReviewCount = (TextView)findViewById(R.id.detailReviewCount);
        detailStar = (RatingBar)findViewById(R.id.detailStar);
        ratingDouble=(TextView)findViewById(R.id.ratingDouble);

        reviewBtn = (ImageView)findViewById(R.id.reviewBtn);


        //파이어베이스 정보 가져오는 부분
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fd.getReference().child("FoodTrucks").child(foodTruckId);


        //푸드트럭 정보 가져오는부분
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("explain")) {
                        //      Log.e("we do",childchild.getValue().toString());
                        foodTruckDetailExample.setText(child.getValue().toString());
                    }
                    if (child.getKey().equals("1")) {
                        //이미지 삽입
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.smap_logo)
                                .showImageForEmptyUri(R.drawable.smap_logo)
                                .showImageOnFail(R.drawable.smap_logo)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .build();

                        ImageView img= new ImageView(DetailFoodtruckActivity.this);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //img.setImageResource(R.drawable.test_img_02); // 이미지 삽입
                        ImageLoader.getInstance().displayImage(child.getValue().toString(), img, options); //이미지 불러오는과정
                        flipper.addView(img);
                        imgCount++;
                        //ImageLoader.getInstance().displayImage(child.getValue().toString(), detailFoodTruckPhoto, options); //이미지 불러오는과정


                    }
                    if (child.getKey().equals("2")) {
                        //이미지 삽입
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.smap_logo)
                                .showImageForEmptyUri(R.drawable.smap_logo)
                                .showImageOnFail(R.drawable.smap_logo)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .build();

                        ImageView img= new ImageView(DetailFoodtruckActivity.this);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //img.setImageResource(R.drawable.test_img_02); // 이미지 삽입
                        ImageLoader.getInstance().displayImage(child.getValue().toString(), img, options); //이미지 불러오는과정
                        flipper.addView(img);
                        imgCount++;


                    }
                    if (child.getKey().equals("3")) {
                        //이미지 삽입
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.smap_logo)
                                .showImageForEmptyUri(R.drawable.smap_logo)
                                .showImageOnFail(R.drawable.smap_logo)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .build();

                        ImageView img= new ImageView(DetailFoodtruckActivity.this);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //img.setImageResource(R.drawable.test_img_02); // 이미지 삽입
                        ImageLoader.getInstance().displayImage(child.getValue().toString(), img, options); //이미지 불러오는과정
                        flipper.addView(img);
                        imgCount++;

                        //ImageLoader.getInstance().displayImage(child.getValue().toString(), detailFoodTruckPhoto, options); //이미지 불러오는과정

                    }
                    if (child.getKey().equals("name")) {
                        foodTruckName.setText(child.getValue().toString());
                    }
                    if (child.getKey().equals("phoneNumber")) {
                        phoneNumber = child.getValue().toString();
                    }
                    if(child.getKey().equals("위도")){
                        truckLatitude=Double.parseDouble(child.getValue().toString());

                    }
                    if(child.getKey().equals("경도")){
                        truckLongitude=Double.parseDouble(child.getValue().toString());
                    }
                    if(child.getKey().equals("업종")){
                        detailCategory.setText(child.getValue().toString());
                    }
                    if(child.getKey().equals("count")){
                        detailReviewCount.setText(child.getValue().toString()+"개의 리뷰");
                    }
                }

                Animation showIn= AnimationUtils.loadAnimation(DetailFoodtruckActivity.this, android.R.anim.fade_in);

                flipper.setInAnimation(showIn);
                flipper.setOutAnimation(DetailFoodtruckActivity.this, android.R.anim.fade_out);

                flipper.setFlipInterval(3000);
                flipper.startFlipping();


                // 미터(Meter) 단위
                double distanceMeter =
                        distance(myLatitude, myLongitude, truckLatitude, truckLongitude, "meter");

                if(myLatitude!=0){
                    foodTruckDistance.setText(Math.round(distanceMeter)+"m");
                }else{
                    foodTruckDistance.setText("");
                }



                final Geocoder geocoder = new Geocoder(getApplicationContext());

                String location="";
                List<Address> address = null; //주소


                try {
                    address = geocoder.getFromLocation(
                            truckLatitude, // 위도
                            truckLongitude, // 경도
                            10); // 얻어올 값의 개수
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (address != null) {
                    String cut[] = address.get(0).toString().split(" ");
                    for (int i = 0; i < cut.length; i++) {
                        System.out.println("cut[" + i + "] : " + cut[i]);
                    }
                    detailGeocoding.setText(cut[1] + " " + cut[2] + " " + cut[3]);
                    //Toast.makeText(getApplicationContext(), "주소"+cut[1] + " " + cut[2] + " " + cut[3], Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdfasdf", "Failed to read value.", error.toException());
            }

        });

        myRef=myRef.child("review");
        myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> ratingStar = new ArrayList<>();
                    int count=0;
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        for(DataSnapshot childchild : child.getChildren()){
                            if(childchild.getKey().equals("userStar")){
                                ratingStar.add(childchild.getValue().toString());
                                count++;
                            }

                        }
                    }
                    float starAverage=0;
                    for(int i=0;i<count;i++){
                        starAverage=Float.parseFloat(ratingStar.get(i))+starAverage;
                    }
                    detailStar.setRating(starAverage/count);

                    String str = String.format("%.2f", starAverage/count);

                    ratingDouble.setText(str+" / 5");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }
        );
                //파이어베이스 부분 종료


                //거리 계산해서 보여주는 부분


                detailFoodTruckCallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(DetailFoodtruckActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);


                        if (ActivityCompat.checkSelfPermission(DetailFoodtruckActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
                    }
                });
        DetailBookmark.setOnClickListener(new View.OnClickListener() {
            int count;
            @Override
            public void onClick(View v) {

               //
                 if(getPreferences("count").equals("")){
                    count=0;
                }else{
                     count=Integer.parseInt( getPreferences("count"));
                     count++;
                 }

               savePreferences("count",count);
               savePreferences(count,Integer.parseInt(foodTruckId));
                Toast.makeText(DetailFoodtruckActivity.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        googleMapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri ="http://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+truckLatitude+","+truckLongitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER );
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailFoodtruck = new Intent(DetailFoodtruckActivity.this,ReviewActivity.class);
                detailFoodtruck.putExtra("foodTruckId",foodTruckId);
                startActivity(detailFoodtruck);
            }
        });
        toggle_Flipping= (ToggleButton)findViewById(R.id.toggle_auto);
        toggle_Flipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    flipper.stopFlipping();
                    img_check=false;
                }else{
                    flipper.setFlipInterval(3000);
                    flipper.startFlipping();
                    img_check=true;
                }
            }
        });
    }

    // ------- 이미지 슬라이드 관련 코드 start ------- //
    public void mOnClick(View v){
        switch( v.getId() ){
            case R.id.btn_previous:
                flipper.stopFlipping();
                flipper.showPrevious();//이전 View로 교체
                if(img_check) {
                    flipper.setFlipInterval(3000);
                    flipper.startFlipping();
                }
                break;
            case R.id.btn_next:
                flipper.stopFlipping();
                flipper.showNext();//다음 View로 교체
                if(img_check) {
                    flipper.setFlipInterval(3000);
                    flipper.startFlipping();
                }
                break;
        }
    }
    // ------- 이미지 슬라이드 관련 코드 end ------- //

    //파일 저장 코드 (즐겨찾기 목록)
    private void savePreferences(int index,int value){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(index), String.valueOf(value));
        editor.commit();
    }
    //파일 저장 코드 (즐겨찾기 목록)
    private void savePreferences(String index,int value){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(index, String.valueOf(value));
        editor.commit();
    }
    //파일에서 값 가져오기 (즐겨찾기 목록)
    private String getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }






}
