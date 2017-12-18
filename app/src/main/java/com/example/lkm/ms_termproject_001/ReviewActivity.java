package com.example.lkm.ms_termproject_001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ReviewActivity extends AppCompatActivity {

    String name = "ERROR";    //카카오와 연동하기위해
    String mail = "ERROR";    //가장 위로 올림
    String profilePhotoURL = "";
    Bitmap bitmap;
    private SimpleSideDrawer mSlidingMenu;
    ListView reviewListView;
    String foodTruckId;
    ImageButton reviewWriteMoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.activiry_left_menu);
        reviewListView = (ListView)findViewById(R.id.reviewListView);
        reviewWriteMoveBtn=(ImageButton)findViewById(R.id.reviewWriteMoveBtn);
        final ReviewMyAdapter adapter = new ReviewMyAdapter();

        reviewListView.setAdapter(adapter);
        requestMe();  //카카오 정보 load

        Intent intent = getIntent();
        foodTruckId = intent.getExtras().getString("foodTruckId");

        reviewWriteMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeReviewIntent = new Intent(ReviewActivity.this,WriteReviewActivity.class);
                writeReviewIntent.putExtra("foodTruckId",foodTruckId);
                startActivity(new Intent(writeReviewIntent));
            }
        });

        //adapter.addItem("","123","!23");


        //리뷰 가져오는 부분
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fd.getReference().child("FoodTrucks").child(foodTruckId).child("review");



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.d("asdfasdf", "Value is: " + dataSnapshot);
                adapter.removeAll();
                //String photo="";
                String userName="";
                String userReview="";
                String userPhoto="";
                float rating=0;

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    for(DataSnapshot childchild : child.getChildren()){
                        if(childchild.getKey().equals("userName")){
                            userName = childchild.getValue().toString();
                        }
                        if(childchild.getKey().equals("userReview")){
                            userReview = childchild.getValue().toString();
                        }
                        if(childchild.getKey().equals("userPhoto")) {
                            userPhoto = childchild.getValue().toString();
                        }
                        if(childchild.getKey().equals("userStar")){
                            rating=Float.parseFloat(childchild.getValue().toString());
                        }
                    }
                    adapter.addItem(userPhoto,userName,userReview,rating);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdfasdf", "Failed to read value.", error.toException());
            }
        });
        adapter.removeAll();
        reviewListView.setAdapter(adapter);


        /* 리스트뷰에 어댑터 등록 */
    }

    // ------- 왼쪽 메뉴바 관련 코드 start ------- //
    public void topMenuClick(View v) {
        switch (v.getId()) {


            case R.id.main_top_menu_left_btn:
                mSlidingMenu.toggleLeftDrawer();

                boolean setUserProfile = setUserProfile();

                if (setUserProfile) {

                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }


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
                        startActivity(new Intent(ReviewActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });

                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReviewActivity.this, BookmarkActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                point_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReviewActivity.this, PointActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                alert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReviewActivity.this, AlertActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                map_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReviewActivity.this, GoogleMapActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });


                adjust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReviewActivity.this, FoodtrcukRegistActivity.class));
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
    public boolean setUserProfile() {

        // -- DB로부터 데이터 입력받음 -- //
        ImageView imageview = (ImageView) findViewById(R.id.profile_img);

        int point = 10100;
        if (profilePhotoURL.equals("")) {
            imageview.setImageResource(R.drawable.profile_test_01); // 바꾸는 코드
        } else {

            imageview.setImageBitmap(bitmap);
        }

        TextView txt_name = (TextView) findViewById(R.id.profile_name);
        txt_name.setText(name);

        TextView txt_mail = (TextView) findViewById(R.id.profile_mail);
        txt_mail.setText(mail);

        TextView txt_point = (TextView) findViewById(R.id.profile_point);
        txt_point.setText("포인트 : " + point + " P"); // 1000원 이상시 쉼표 추가 하는 함수 만들 것.  ex) 10000  ->  10,000


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
                GlobalApplication global = (GlobalApplication) getApplicationContext();
                global.uuid = result.getUUID();

                name = result.getNickname();
                profilePhotoURL = result.getProfileImagePath();

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(profilePhotoURL);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);

                        } catch (IOException ex) {
                        }
                    }
                };
                mThread.start();
                try {
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
