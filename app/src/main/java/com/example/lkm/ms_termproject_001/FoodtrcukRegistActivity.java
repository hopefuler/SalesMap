package com.example.lkm.ms_termproject_001;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class FoodtrcukRegistActivity extends AppCompatActivity {
    EditText foodTruckName,FoodtruckSimpleExplain,FoodtruckExplain,FoodtruckPhoneNumber;
    FirebaseDatabase fd;    //데이터베이스
    DatabaseReference Ref;
    ImageButton profileImg01,profileImg02,profileImg03;
    TextView tv;
    final int REQ_CODE_SELECT_IMAGE=100;
    int imgFlag=0;
    Uri TruckImg1,TruckImg2,TruckImg3;
    Uri[] Truck = new Uri[3];
    String[] url;
    boolean imgFlag1=true,imgFlag2=true,imgFlag3=true;
    //  ToggleButton tb;
    int count = 0 ;
    double longitude=0;  //경도
    double latitude=0;   //위도
    double altitude=0;   //고도
    String uuid="0";
    Button age_button;

    final Context context = this;
    String category="업종";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodtrcuk_regist);

        foodTruckName = (EditText)findViewById(R.id.FoodtruckName);
        FoodtruckSimpleExplain = (EditText)findViewById(R.id.FoodtruckSimpleExplain);
        FoodtruckExplain= (EditText)findViewById(R.id.FoodtruckExplain);
        FoodtruckPhoneNumber=(EditText)findViewById(R.id.FoodtruckPhoneNumber);
        profileImg01 =(ImageButton)findViewById(R.id.profileImg01);
        profileImg02 =(ImageButton)findViewById(R.id.profileImg02);
        profileImg03 =(ImageButton)findViewById(R.id.profileImg03);
        tv = (TextView) findViewById(R.id.textView2); //위도경도 표시

        Truck[0]=null;
        Truck[1]=null;
        Truck[2]=null;

        profileImg01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFlag=1;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        profileImg02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFlag=2;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        profileImg03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFlag=3;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        requestMe();

        //사용자에게 권한 물어봄
        ActivityCompat.requestPermissions(FoodtrcukRegistActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);





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
            //                lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.

        }catch(SecurityException ex){
        }




    }
    public void SellStartClick(View v){
        fd = FirebaseDatabase.getInstance();
        Ref = fd.getReference();
        Ref = Ref.child("FoodTrucks");

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference storageRef = fs.getReference();
        url = new String[3];
        for(int i = 2 ; i >= 0; i --){
            StorageReference riversRef = storageRef.child("images/"+uuid+"/"+i+".jpg");
            if(Truck[0]==null){
                Toast.makeText(getApplicationContext(),"첫번째 사진은 필수로 등록 하셔야 합니다.",Toast.LENGTH_LONG).show();
                break;
            }
            Toast.makeText(FoodtrcukRegistActivity.this, "업로드 성공 문구가 보일때까지 기다려주세요.", Toast.LENGTH_SHORT).show();
            if(Truck[i]==null){
                if(i==0){
                    imgFlag1=false;
                }
                if(i==1){
                    imgFlag2=false;
                }
                if(i==2){
                    imgFlag3=false;
                }
                count++;
                continue;
            }

            riversRef.putFile(Truck[i])
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            url[count++] = downloadUrl.toString();
                            if(count==3){
                                String key = uuid;

                                HashMap<String,String> data = new HashMap<String,String>();
                                data.put("name",foodTruckName.getText().toString());
                                data.put("simpleExplain",FoodtruckSimpleExplain.getText().toString());
                                data.put("explain",FoodtruckExplain.getText().toString());
                                data.put("phoneNumber",FoodtruckPhoneNumber.getText().toString());
                                data.put("openFlag","0");
                                data.put("경도",longitude+"");
                                data.put("위도",latitude+"");
                                data.put("고도",altitude+"");
                                data.put("업종",age_button.getText().toString());
                                if(imgFlag1){

                                    data.put("1",url[2]);
                                }
                                if(imgFlag2){

                                    data.put("2",url[1]);
                                }
                                if(imgFlag3){

                                    data.put("3",url[0]);
                                }


                                HashMap<String,Object> child = new HashMap<String,Object>();
                                child.put(key,data);

                                Ref.updateChildren(child);

                                Toast.makeText(FoodtrcukRegistActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
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
            tv.setText("위도 : " + longitude + "\n경도 : " + latitude);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {

                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageButton image;
                    //Uri에서 이미지 이름을 얻어온다.
                    if(imgFlag==1){
                        Truck[0] = data.getData();
                        TruckImg1 = data.getData();
                        image = (ImageButton)findViewById(R.id.profileImg01);
                    }else if(imgFlag==2){
                        Truck[1] = data.getData();
                        TruckImg2=data.getData();
                        image = (ImageButton)findViewById(R.id.profileImg02);
                    }else{
                        Truck[2] = data.getData();
                        TruckImg3=data.getData();
                        image = (ImageButton)findViewById(R.id.profileImg03);
                    }
                    image.setImageBitmap(image_bitmap);
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    //   Bitmap image_bitmap    = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //   ImageView image = (ImageView)findViewById(R.id.profile_img);
                    //배치해놓은 ImageView에 set
                    //  image.setImageBitmap(image_bitmap);
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
    public void requestMe() {
        //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                //    Log.d(TAG, "onNotSignedUp ");
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(UserProfile result) {

                uuid = result.getId()+"";

            }
        });
    }

    // -------------------------- 학년, 나이, 성별 선택 alertDialog 이벤트 Start -------------------------- //
    public void memSelectClick(View v) {

        switch (v.getId()) {
            case R.id.category_button:
                final CharSequence[] items = {"화장품","마트","과일","카페","술집"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // 제목셋팅
                alertDialogBuilder.setTitle("업종을 선택해주세요.");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // 프로그램을 종료한다
                                category =items[id]+""; // 전역변수 저장

                                age_button = (Button)findViewById(R.id.category_button);

                                if(id==0)
                                    age_button.setText("화장품");
                                else if(id==1)
                                    age_button.setText("마트");
                                else if(id==2)
                                    age_button.setText("과일");
                                else if(id==3)
                                    age_button.setText("카페");
                                else if(id==4)
                                    age_button.setText("술집");

                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
                break;

        }
    }
    // -------------------------- 학년, 나이, 성별 선택 alertDialog 이벤트 End -------------------------- //
}