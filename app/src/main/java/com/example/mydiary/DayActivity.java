package com.example.mydiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Toast;

import com.example.mydiary.databinding.ActivityDay2Binding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class DayActivity extends AppCompatActivity {

    private ActivityDay2Binding binding;                                                                                                                                                    //1
    ActivityResultLauncher<Intent> activityResultLauncher;                                                                                                                          //aktivite sonucu başlatıcı yani izin verildiğinde nolcak  //2 //buradaki galeriye gitcek
    ActivityResultLauncher<String> permissionLauncher;                                                                                                                              //2 //görsel seçildiğinde ne olacak //geriye string getircek izinlerde string kullanılır
    Bitmap selectedImage;                                                                                                                                                                   //2
    SQLiteDatabase database;                                                                                                                                                            //3


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDay2Binding.inflate(getLayoutInflater());                                                                                                                             //1
        View view = binding.getRoot();                                                                                                                                                                  //1
        setContentView(view);                                                                                                                                                                                           //1
        //setContentView(R.layout.activity_day2);

        registerLauncher();                                                                                                                                                                         //2

        database = this.openOrCreateDatabase("Days",MODE_PRIVATE,null);                                                                                                             //database initilaze edildi


        Intent intent = getIntent();                                                                                                                                            //intent alındi    Bu satırda, bu aktiviteye geçirilen Intent nesnesini alır. getIntent() metodu, bu aktiviteye geçişte kullanılan intenti getirir.
        String info = intent.getStringExtra("info");                                                                                                                        //ve new mı old mu alınmış onu kontrol etcem

        if(info.equals("new")){
            binding.editTextTextMultiLine.setText("");                                                                                                                                      //bütün değerlerin boş olduğundan emin oluyoruz
            binding.editTextDate.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.select_image);
        }
        else {                                                                                                                                                                          //yeni bi şey eklemek istemiyorsa bana bir id verdi kullanıcı onu döndurmek istiyor demek oluyor
            int dayId = intent.getIntExtra("dayId",1);                                                                                                                                                              //bu 1 int değilse null vermesin ne kullansın diye
            binding.button.setVisibility(View.INVISIBLE);

            try {                                                                                                                                                                           //databaseden çekileceği için try catch

                Cursor cursor = database.rawQuery("SELECT * FROM days WHERE id =?",new String[] {String.valueOf(dayId)});                                                                                                   //çekilecek günün id'si alındı    Bu satırda, "days" adlı SQLite tablosundan "id" sütunu belirtilen dayId değerine eşit olan bir günün verilerini çekmek için bir SQL sorgusu yapılır. Sonuçlar bir Cursor nesnesine atanır.
                int dayNameIx = cursor.getColumnIndex("dayday");                                                                                                                                                                            //günün verileri alınıyor    Bu satırda, Cursor nesnesinden "dayday" sütununun indeksini alır. Bu indeks, ileride bu sütundan veri çekerken kullanılacaktır.
                int dateIx = cursor.getColumnIndex("date");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.editTextTextMultiLine.setText(cursor.getString(dayNameIx));                                                                                             //burada çektiği verileri andoid içindeki yerlere yazdı
                    binding.editTextDate.setText(cursor.getString(dateIx));

                    byte[] bytes = cursor.getBlob(imageIx);                                                                                                                                 //byte array vercek        Bu satırda, Cursor nesnesinden alınan "image" sütunundaki değeri bir byte dizisine çevirir. Bu, bir resmin byte dizisi olarak alınmasını sağlar.
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);                                                                                                                  //alınan bytearrayi bitmape çevirdi        Bu satırda, byte dizisinden bir Bitmap nesnesi oluşturulur. decodeByteArray metodu, byte dizisini bir bitmap'e çevirir.
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    //butonun tıklanma
    public void save (View view) {                                                                                                                                                          //1
        String date = binding.editTextDate.getText().toString();                                                                                                                                                        //2
        String multitext = binding.editTextTextMultiLine.getText().toString();                                                                                                                                                           //2

        Bitmap smallImage = makeSmallerImage(selectedImage,300);                                                                                                                                                            //2
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();                                                                                                                                   //2  //resmi kaydetmek için sql'e byte dizisine yani 0 ve 1e çevirmek lazım    Bu, sıkıştırılmış resmi depolamak için kullanılacak bir akıştır.
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);                                                                                                                                 //2     //çevirme byte
        byte[] byteArray = outputStream.toByteArray();                                                                                                                                  //2

        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS  days(id INTEGER PRIMARY KEY, dayday TEXT, date VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO days(dayday, date, image) VALUES(?, ?, ?)";                                                                                                                       //"days" tablosuna yeni bir kayıt eklemek için kullanılacak olan SQL sorgusunu oluşturulur. Sorgu, "dayday", "date" ve "image" sütunlarına değer ekler ve bu değerler yerine "?" yer tutucuları kullanır.
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);                                                                                                                     //databasede bağlama işlemleri yapan sınıf yani bu stringi databaseda çalıştır der           Bu satırda, oluşturulan SQL sorgusunu SQLite veritabanında çalıştırmak için kullanılacak olan SQLiteStatement nesnesi oluşturulur.

            sqLiteStatement.bindString(1,multitext);                                                                                                                                                //indexler ile değerleri bağlar    : Bu satırda, SQL sorgusundaki ilk "?" yerine multitext değeri bağlanır.
            sqLiteStatement.bindString(2,date);
            sqLiteStatement.bindBlob(3,byteArray);
            sqLiteStatement.execute();   //çalıştır

        }
        catch (Exception e){
            e.printStackTrace();
        }
                                                                                                                                                                                                                         //kayıt olduktan sonra main activitye gitmek istiyorum
        Intent intent = new Intent(DayActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);                                                                                                                                                                    //bundan önceki tüm aktiviteleri kapat sadece yeni açtığım aktivite açılsın
        startActivity(intent);
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize){                                                                                                          //2 hepsi
        int width = image.getWidth();                                                                                                                                               //Güncel görselin genişlik ve yükseklik alınsın
        int height = image.getHeight();

        float bitmapRatio = (float) width/(float) height;                                                                                                                                    //görsel yatay mı dikey mi

        if (bitmapRatio>1){
            //yatay image
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        }
        else {
            //dikey image
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return image.createScaledBitmap(image,width,height,true);                                                                                                             //yukarı ve aşağı doğru ölçeklendirilmiş resimi döndürür
    }

    //resmin tıklanması
    //2 hepsi
    public void selectImage(View view){ //1
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                                                                                                                                                                         //android 33 ve üstü --> readmediaimages
                                                                                                                                                                                             //izin kontrolü
                                                                                                                                                                                         //: Bu satırda, READ_MEDIA_IMAGES izni için izin kontrolü yapılır. Eğer bu izin daha önce verilmemişse, içeriğin çalıştırılacağı bir bloğa girilir.
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){                                                                                //hangi izni kontrol ediceğini soruyor ve kontrol ediyor
                                                                                                                                                                                                // Bu satırda, izin daha önce reddedilmişse ve kullanıcıya izin isteme nedenini açıklamak için bir neden gösterilmesi gerekiyorsa, içeriğin çalıştırılacağı bir bloğa girilir.
                                                                                                                                                                                            //izin isticez
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){                                                                           //izin isteme mantığını kullanıcıya göstereyim mi zorunda mıyım diye soruyor
                    //izin verilmemişse izin isticez yani kullanmak zorundaysak
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {                                            //alt taraftan çıkan tıklanabilir mesaj , view hangi görünümden çağırılıyor, kullanıcı butona basana kadar göster
                        //setaction yani izin vermişse butonu
                        @Override
                        public void onClick(View v) {
                            //request permission
                            //izin vermişse slackbardan
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    }).show();
                }
                else{
                                                                                                                                                                                                            //izin verildiyese direk galeriye gitcez
                                                                                                                                                                                        //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                }
            }
            else{
                                                                                                                                                                                                                             //galeriye gitcez
                                                                                                                                                                                                                    //Eğer izin daha önce verilmişse, cihazın galerisine gitmek için bir intent oluşturulur ve galeriden bir görsel seçmek için activityResultLauncher başlatılır.
                                                                                                                                                                                                                        //aktiviteden diğerine gitmek , uri klasorün olduğu yere gitmek oraya gidiyorum adresi bu yani, pick galeriden görsel tut al
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);                                                                                          //galeri açma intent'i oluşturulur. Bu intent, kullanıcının galeriden bir öğe seçmesini sağlayacaktır.
                activityResultLauncher.launch(intentToGallery);                                                                                                                     // Galeriden çekme fonksiyonu    Bu satırda, oluşturulan intent, activityResultLauncher adlı bir ActivityResultLauncher nesnesi kullanılarak başlatılır. Bu, kullanıcının galeriden bir öğe seçtikten sonra geri dönüş verilerini işlemek için kullanılır.

            }
        }

        else {
            //izin kontrolü
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin isticez
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); //gidip izinleri aldı

                        }
                    }).show();
                }
                else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }
            }
            else{
                //galeriye gitcez
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);

            }

        }
    }

                                                                                                                                                                                        //2 hepsi
                                                                                                                                                                                                 //activityresultlauncherin ne olduğunu ne yapacağını tanımlıcaz burada ve sonra oncreate altında çağırıcaz
    private void registerLauncher(){

                                                                                                                                                                                                 //galeriye gidiyoruz burada                                            activite başlatıyorum sonuçta uri nerede olduğu bilgisi                burada bana geri aktivite veriyor
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {                                                                                                                                          //activity resultt galeriye gidince kullanıcının verdiği tepkiler vazgeçmiş de olabilir
                if (o.getResultCode() == RESULT_OK){                                                                                                                                                //sonuç kodu kullannıcı bi şey seçtiyse
                    Intent intentFromResult = o.getData();                                                                                                                                            //cevaptan gelen intent yani veriyi al
                    if(intentFromResult != null){                                                                                                                                                           //geriye veri döndüyse
                        Uri imageData = intentFromResult.getData();                                                                                                                                         //kullanıcının seçtiği görselin nerede olduğu yani uri verdi bu da. Şİmdi alınan veriyi sonra kaydedebilmek için bitmape çevirmeliyiz
                        //binding.imageView2.setImageURI(imageData);
                        try {                                                                                                                                                       //bitmape çevir
                            ImageDecoder.Source source = ImageDecoder.createSource(DayActivity.this.getContentResolver(),imageData);                                                                                //source sınıfından source oluşturur, uriını alır
                            selectedImage = ImageDecoder.decodeBitmap(source);                                                                                                         //kaynağını bitmape çevir
                            binding.imageView.setImageBitmap(selectedImage);                                                                                                                                //oluşturduğunu verdi
                        }
                        catch (Exception e){
                            e.printStackTrace();                                                                                                                            //logcatte göstercek hataları
                        }
                    }
                }
            }
        });

                                                                                                                                                                                     //izin istemek ve cevap alıcaz                                  burada izin isticeğimizi söylüyoruz bu aktivite launcherla  ve callback alıcam sonuçta
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o){
                    //izin verdi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);                                                                                                                             //galeriden aldı yani
                }
                else{
                    Toast.makeText(DayActivity.this,"Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}