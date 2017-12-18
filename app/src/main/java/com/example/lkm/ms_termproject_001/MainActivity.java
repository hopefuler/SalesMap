package com.example.lkm.ms_termproject_001;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.navdrawer.SimpleSideDrawer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity {

    ViewFlipper flipper;
    ToggleButton toggle_Flipping;
    Boolean img_check=true;

    String name = "ERROR";    //카카오와 연동하기위해
    String mail = "ERROR";    //가장 위로 올림
    String profilePhotoURL = "";
    String catagory="";
    Bitmap bitmap;

    double longitude=0;  //경도
    double latitude=0;   //위도
    double altitude=0;   //고도
    MyAdapter mMyAdapter = new MyAdapter();// 리스트뷰 선언

    private SimpleSideDrawer mSlidingMenu;

    private ListView mListView;

    final int REQ_CODE_SELECT_IMAGE=100;

    final Context context = this;

    String category="모두보기";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 위젯과 멤버변수 참조 획득 */
        mListView = (ListView)findViewById(R.id.listView);

        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();

        onMapReady();//경도 위도 가져오기


        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.activiry_left_menu);

        // ------- 이미지 슬라이드 관련 코드 start ------- //
        flipper= (ViewFlipper)findViewById(R.id.flipper);

        for(int i=0;i<3;i++){
            ImageView img= new ImageView(this);
            img.setImageResource(R.drawable.smap_img_001+i);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            flipper.addView(img);
        }
        Animation showIn= AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        flipper.setInAnimation(showIn);
        flipper.setOutAnimation(this, android.R.anim.fade_out);

        flipper.setFlipInterval(3000);
        flipper.startFlipping();

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
        // ------- 이미지 슬라이드 관련 코드 end ------- //

        requestMe();  //카카오 정보 load

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY-2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

        //푸드트럭 자세한 페이지로 이동하는 부분
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailFoodtruck = new Intent(MainActivity.this,DetailFoodtruckActivity.class);
                detailFoodtruck.putExtra("foodTruckId",mMyAdapter.getItem(position).getId());
                detailFoodtruck.putExtra("longitude",longitude+"");
                detailFoodtruck.putExtra("latitude",latitude+"");
                detailFoodtruck.putExtra("altitude",altitude+"");
                startActivity(detailFoodtruck);
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

    // ------- 왼쪽 메뉴바 관련 코드 start ------- //
    public void topMenuClick(View v){
        switch( v.getId() ){


            case R.id.main_top_menu_left_btn:
                mSlidingMenu.toggleLeftDrawer();

                boolean setUserProfile = setUserProfile();

                if(setUserProfile){

                }else{
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }

                // -- 왼쪽 메뉴바 버튼 클릭 시 이벤트 start -- //


                // -- 왼쪽 메뉴바 버튼 클릭 시 이벤트 start -- //
                TextView main_list_btn = (TextView)findViewById(R.id.main_list_btn); // 즐겨찾기
                TextView bookmark = (TextView)findViewById(R.id.bookmark_btn); // 즐겨찾기
                TextView point_btn = (TextView)findViewById(R.id.point_btn); // 적립내역
                TextView alert_btn = (TextView)findViewById(R.id.alert_btn); // 알림
                TextView map_btn = (TextView)findViewById(R.id.map_btn); // 구글 맵

                TextView adjust_btn = (TextView)findViewById(R.id.adjust_btn); // 등록 수정
                TextView logout_btn = (TextView)findViewById(R.id.logout_btn); // 로그아웃

                main_list_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                logout_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickLogout();

                    }
                });

                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, BookmarkActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                point_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, PointActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                alert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, AlertActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                map_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, GoogleMapActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });


                adjust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        startActivity(new Intent(MainActivity.this, FoodtrcukRegistActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });



                // -- 왼쪽 메뉴바 버튼 클릭 시 이벤트 end -- //

                break;
            // ------- 왼쪽 메뉴바 관련 코드 end ------- //

            // ------- 필터링 관련 코드 start ------- //
            case R.id.main_top_menu_filter_btn:
                final CharSequence[] items = {"모두보기", "화장품","마트","과일","카페","술집","식당"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목셋팅
                alertDialogBuilder.setTitle("원하는 세일 종류");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // 프로그램을 종료한다

                                category =items[id]+""; // 전역변수 저장
                                /*
                                *       id[0] = 한식
                                *       id[1] = 중식
                                *       ...
                                * */
                                dataSetting();

                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
                break;
            // ------- 필터링 관련 코드 end ------- //
        }
    }

    // ------- 유저 프로필 설정 UI start ------- //
    public boolean setUserProfile(){

        // -- DB로부터 데이터 입력받음 -- //
        ImageView imageview = (ImageView)findViewById(R.id.profile_img);

        int point = 10100;
        if(profilePhotoURL.equals("")){
            imageview.setImageResource(R.drawable.profile_test_01); // 바꾸는 코드
        }else{

            imageview.setImageBitmap(bitmap);
        }

        TextView txt_name = (TextView)findViewById(R.id.profile_name);
        txt_name.setText(name);

        TextView txt_mail = (TextView)findViewById(R.id.profile_mail);
        txt_mail.setText(mail);

        TextView txt_point = (TextView)findViewById(R.id.profile_point);
        txt_point.setText("포인트 : "+point+" P"); // 1000원 이상시 쉼표 추가 하는 함수 만들 것.  ex) 10000  ->  10,000


        return true;
    }

    // ------- 카카오 유저정보 가져오기 start ------- //
    public void requestMe() {
        //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                //    Log.d(TAG, "onNotSignedUp ");
            }

            @Override
            public void onSuccess(UserProfile result) {
                GlobalApplication global = (GlobalApplication)getApplicationContext();
                global.uuid = result.getId()+"";

                name = result.getNickname();
                profilePhotoURL = result.getProfileImagePath();

                Thread mThread = new Thread(){
                    @Override
                    public void run(){
                        try{
                            URL url = new URL(profilePhotoURL);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);

                        }catch (IOException ex){
                        }
                    }
                };
                mThread.start();
                try{
                    mThread.join();
                    //profile_img.setImageBitmap(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // ------- 카카오 유저정보 가져오기 end ------- //

    // ------- 리스트 뷰 start ------- //

    String name_2="";
    String simpleExplain="기본글";
    String photo="";

    double tempLongitude=0;  //경도 푸드트럭의
    double tempLatitude=0;   //위도
   // double tempAltitude=0;   //고도
    boolean distanceFlag=false;

    private void dataSetting(){
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fd.getReference().child("FoodTrucks");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Log.d("asdfasdf", "Value is: " + dataSnapshot);
                mMyAdapter.removeAll();
                final Bitmap[] tempBitmap = new Bitmap[1];
                //String photo="";
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    boolean flag=false;
                    Log.e("key",child.getKey());
;                    for(DataSnapshot childchild : child.getChildren()){
                        if(childchild.getKey().equals("name")){
                      //      Log.e("we do",childchild.getValue().toString());
                            name_2 = childchild.getValue().toString();

                        }
                        if(childchild.getKey().equals("simpleExplain")){
                            simpleExplain = childchild.getValue().toString();
                        }
                        if(childchild.getKey().equals("1")){
                            photo = childchild.getValue().toString();

                        }
                        if(childchild.getKey().equals("경도")){
                            tempLongitude = Double.parseDouble(childchild.getValue().toString());
                        }
                        if(childchild.getKey().equals("위도")){
                            tempLatitude = Double.parseDouble(childchild.getValue().toString());
                        }
                        if(childchild.getKey().equals("업종")){
                            catagory = childchild.getValue().toString();
                            if(category.equals("모두보기")||category.equals("화장품")){
                                flag=true;

                            }else if(category.equals(childchild.getValue().toString())){
                                flag=true;
                            }
//                            if(category.equals("모두보기")){
//
//                            }else{
//                                if(category.equals(childchild.getValue().toString())){
//
//                                }else{
//                                    flag=false;
//                                }
//                            }
                        }


                    }


                    if(flag){
                    // 미터(Meter) 단위
                    double distanceMeter =
                            distance(tempLatitude, tempLongitude, latitude, longitude, "meter");

                        int index=0;
                        String tmpame[] = {"",};
                        String tmpSimpleExplain[] = {"",};
                        /*
                        if(catagory.equals("한식")){
                            catagory="화장품";
                        }else if(catagory.equals("중식")){
                            catagory="마트";
                        }else if(catagory.equals("양식")){
                            catagory="과일";
                        }else if(catagory.equals("일식")){
                            catagory="카페";
                        }else if(catagory.equals("기타")){
                            catagory="술집";
                        }else{*/
                        catagory="화장품";
                        //}

                        if(latitude!=0){
                            if(Math.round(distanceMeter)>1000) {
                                mMyAdapter.addItem(photo, name_2, simpleExplain, "1000m", child.getKey(), catagory);
                            }
                            else {
                                mMyAdapter.addItem(photo, name_2, simpleExplain, Math.round(distanceMeter) + "m", child.getKey(), catagory);
                            }
                        }else{
                            mMyAdapter.addItem(photo,name_2,simpleExplain,"",child.getKey(),catagory);
                        }

                    if(distanceFlag==true){

                        mMyAdapter.Sort();
                    }
                        mMyAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdfasdf", "Failed to read value.", error.toException());
            }
        });

        mMyAdapter.removeAll();
        mListView.setAdapter(mMyAdapter);



        /* 리스트뷰에 어댑터 등록 */


    }



    // ------- 리스트 뷰 end ------- //

    // ------- 갤러리 - 이미지 start ------- //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.profile_img);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //이미지 로더
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Toast.makeText(getBaseContext(), "imgPath : "+imgPath +" //  imgName: "+imgName , Toast.LENGTH_SHORT).show();
        return imgName;
    }
    // ------- 갤러리 - 이미지 end ------- //


    //로그아웃 - 모든 엑티비티에 복사해놓아야합니다
    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
//                                    redirectLoginActivity();
            }
        });
    }


//////////GPS부분 건드리지말것
    boolean gpsFlag=true;
    //내위치 가져오기
    public void onMapReady() {
        //아래부분은 지피에스
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(gpsFlag){
            try{
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
                //lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }catch(SecurityException ex){
            }
        }else{
            lm.removeUpdates(mLocationListener);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            Log.d("test", "onLocationChanged, location:" + location);
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            //tv.setText("위도 : " + longitude + "\n경도 : " + latitude);
            distanceFlag=true;
            dataSetting();
            gpsFlag=false;
            onMapReady();



        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };










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
/////////GPS부분 끝!!!!

}
