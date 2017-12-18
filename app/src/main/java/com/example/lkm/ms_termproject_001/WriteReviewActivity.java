package com.example.lkm.ms_termproject_001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    String name = "ERROR";    //카카오와 연동하기위해
    String mail = "ERROR";    //가장 위로 올림
    String profilePhotoURL = "";
    Bitmap bitmap;
    private SimpleSideDrawer mSlidingMenu;
    ImageView reviewWirteFoodTruckPhoto;
    TextView reviewWriteFoodTruckName;
    String userProfileURL="";
    String userName="";
    String userID="";
    String count="0";
    ImageButton reviewWriteImageBtn;
    RatingBar reviewWriteRatingBar;
    EditText reviewWirteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.activiry_left_menu);


        Intent intent = getIntent();
        final String foodTruckId = intent.getExtras().getString("foodTruckId");
        reviewWirteFoodTruckPhoto=(ImageView)findViewById(R.id.reviewWirteFoodTruckPhoto);
        reviewWriteFoodTruckName =(TextView)findViewById(R.id.reviewWriteFoodTruckName);
        reviewWriteImageBtn = (ImageButton)findViewById(R.id.reviewWriteImageBtn);
        reviewWriteRatingBar =(RatingBar)findViewById(R.id.reviewWriteRatingBar);
        reviewWirteReview = (EditText)findViewById(R.id.reviewWirteReview);


        requestMe();  //카카오 정보 load


        //푸드트럭 등록하는부분
        reviewWriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase fd;    //데이터베이스
                DatabaseReference Ref;

                fd = FirebaseDatabase.getInstance();
                Ref = fd.getReference();
                Ref = Ref.child("FoodTrucks").child(foodTruckId).child("review");

                String rating = String.valueOf(reviewWriteRatingBar.getRating());
                String review = reviewWirteReview.getText().toString();

                HashMap<String,String> data = new HashMap<String,String>();
                data.put("userStar",rating);
                data.put("userReview",review);
                data.put("userName",userName);
                data.put("userPhoto",userProfileURL);
                data.put("uesrID",userID);

                HashMap<String,Object> child = new HashMap<String,Object>();
                child.put(count,data);
                Ref.updateChildren(child);


                Ref = fd.getReference();
                Ref = Ref.child("FoodTrucks").child(foodTruckId);
                HashMap<String,Object> countData = new HashMap<>();
                count = String.valueOf(Integer.parseInt(count)+1);
                countData.put("count",count);
                Ref.updateChildren(countData);


                //Ref.setValue("reviewCount",String.valueOf(Integer.parseInt(count)+1));
               // HashMap<String,Object> count = new HashMap<String,Object>();
               // count.put("count",count);
               // Ref.

                finish();
                Intent writeReviewIntent = new Intent(WriteReviewActivity.this,ReviewActivity.class);
                writeReviewIntent.putExtra("foodTruckId",foodTruckId);
                startActivity(new Intent(writeReviewIntent));



            }
        });


        //파이어베이스 정보 가져오는 부분
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fd.getReference().child("FoodTrucks").child(foodTruckId);

        //푸드트럭 정보 가져오는 부분
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

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

                        ImageLoader.getInstance().displayImage(child.getValue().toString(), reviewWirteFoodTruckPhoto, options); //이미지 불러오는과정
                    }
                    if (child.getKey().equals("name")) {
                        reviewWriteFoodTruckName.setText(child.getValue().toString());
                    }
                    if(child.getKey().equals("count")){

                        count=String.valueOf(child.getValue().toString());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdfasdf", "Failed to read value.", error.toException());
            }

        });
    }

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
                        startActivity(new Intent(WriteReviewActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });

                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(WriteReviewActivity.this, BookmarkActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                point_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(WriteReviewActivity.this, PointActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                alert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(WriteReviewActivity.this, AlertActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                map_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(WriteReviewActivity.this, GoogleMapActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });


                adjust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(WriteReviewActivity.this, FoodtrcukRegistActivity.class));
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
                userProfileURL=profilePhotoURL;
                userName = result.getNickname();
                userID = String.valueOf(result.getId());

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
