package com.example.lkm.ms_termproject_001;

import android.*;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.navdrawer.SimpleSideDrawer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback
    {

        String name = "ERROR";    //카카오와 연동하기위해
        String mail = "ERROR";    //가장 위로 올림
        String profilePhotoURL = "";
        Bitmap bitmap;
        private SimpleSideDrawer mSlidingMenu;

        double longitude=0;  //경도
        double latitude=0;   //위도
        double altitude=0;   //고도

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_google_map);

            mSlidingMenu = new SimpleSideDrawer(this);
            mSlidingMenu.setLeftBehindContentView(R.layout.activiry_left_menu);

            requestMe();  //카카오 정보 load

            // ------- 구글맵 start ------- //
            FragmentManager fragmentManager = getFragmentManager();
            MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // ------- 구글맵 end ------- //
        }

        // ------- 구글맵 start ------- //
        @Override
        public void onMapReady(final GoogleMap map) {
            //아래부분은 지피에스
            ActivityCompat.requestPermissions(GoogleMapActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LatLng SEOUL = new LatLng(latitude, longitude);
                    if(longitude==0){
                        Toast.makeText(GoogleMapActivity.this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(SEOUL);
                        markerOptions.title("서울");
                        markerOptions.snippet("한국의 수도");
                        map.addMarker(markerOptions);

                        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                        map.animateCamera(CameraUpdateFactory.zoomTo(13));
                    }



                    FirebaseDatabase fd = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = fd.getReference().child("FoodTrucks");


                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()) {

                                String tempName="",simpleExplain="";
                                double tempLongitude=0,tempLatitude=0;
                                for (DataSnapshot childchild : child.getChildren()) {
                                    if (childchild.getKey().equals("name")) {
                                        //      Log.e("we do",childchild.getValue().toString());
                                        tempName = childchild.getValue().toString();

                                    }
                                    if (childchild.getKey().equals("simpleExplain")) {
                                        simpleExplain = childchild.getValue().toString();
                                    }
                                    if (childchild.getKey().equals("경도")) {
                                        tempLongitude = Double.parseDouble(childchild.getValue().toString());
                                    }
                                    if (childchild.getKey().equals("위도")) {
                                        tempLatitude = Double.parseDouble(childchild.getValue().toString());
                                    }

                                }
                                LatLng marker = new LatLng(tempLatitude, tempLongitude);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(marker);
                                markerOptions.title(tempName);
                                markerOptions.snippet(simpleExplain);
                                map.addMarker(markerOptions);


                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }, 3000);
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
        // ------- 구글맵 end ------- //

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
                    TextView bookmark = (TextView)findViewById(R.id.bookmark_btn); // 즐겨찾기
                    TextView point_btn = (TextView)findViewById(R.id.point_btn); // 적립내역
                    TextView alert_btn = (TextView)findViewById(R.id.alert_btn); // 알림
                    TextView map_btn = (TextView)findViewById(R.id.map_btn); // 구글 맵
                    TextView adjust_btn = (TextView)findViewById(R.id.adjust_btn); // 등록 수정
                    TextView logout_btn = (TextView)findViewById(R.id.logout_btn); // 로그아웃
                    // 로그아웃

                    bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GoogleMapActivity.this, BookmarkActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    });
                    point_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GoogleMapActivity.this, PointActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    });
                    alert_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GoogleMapActivity.this, AlertActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    });


                    map_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GoogleMapActivity.this, GoogleMapActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    });


                    adjust_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GoogleMapActivity.this, FoodtrcukRegistActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    });

                    // -- 왼쪽 메뉴바 버튼 클릭 시 이벤트 end -- //

                    break;
            }
        }
        // ------- 왼쪽 메뉴바 관련 코드 end ------- //

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
        // ------- 유저 프로필 설정 UI end ------- //

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
                    global.uuid = result.getUUID();

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
    }
