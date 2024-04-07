package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mydiary.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;                                                                                                                                                     //1 //Bu tip bir değişken, View Binding kullanılarak oluşturulan sınıfın ismi (ActivityMainBinding gibi) ve
                                                                                                                                                                                                                      // geçirmek için binding değişkeni kullanılabilir hale gelir.
                                                                                                                                                                                         // bu sınıfın örneğini temsil eden bir değişkenin adı (binding gibi) ile tanımlanır. Bu sayede, layout dosyasındaki öğeleri etkileşime
    ArrayList<Day> dayArrayList;                                                                                                                                // Bu ifade, Day sınıfı türünden elemanları içerebilen bir dinamik dizi (ArrayList) oluşturur.
    DayAdapter dayAdapter;                                                                                                                                              //DayAdapter, RecyclerView'ın içeriğini yöneten ve veri kümesini bağlamak için kullanılan özel bir adaptör sınıfını temsil eder.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                                                                                                                                                                                                        //Bu kod bloğu, View Binding kullanarak XML tabanlı bir kullanıcı arayüzü öğelerine erişim sağlamak ve
                                                                                                                                                                                                         // bu öğeleri bir aktiviteye bağlamak için kullanılır.
        binding= ActivityMainBinding.inflate(getLayoutInflater());                                                                                                                          //1
        View view = binding.getRoot();                                                                                                                                                    //1 Bu değişken, XML dosyasındaki tüm arayüz öğelerine erişim sağlar.
        setContentView(view); //1
        //setContentView(R.layout.activity_main);

        dayArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayAdapter = new DayAdapter(dayArrayList);                                                                                                                                              //Bu adaptör, RecyclerView'ın içeriğini yönetmek ve veriyi görüntülemek için kullanılacak.
        binding.recyclerView.setAdapter(dayAdapter);                                                                                                                                                            // Bu işlem, RecyclerView'ın içeriğini yöneten bir adaptörün ayarlanmasını sağlar.
        getData();
    }

    private void  getData(){
        try {
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Days",MODE_PRIVATE,null);                                                                                       //Bu satırda, "Days" adlı bir SQLite veritabanını açar veya oluşturur. this ifadesi, bu işlemi gerçekleştiren aktiviteyi veya bağlamı temsil eder. "Days" ifadesi ise veritabanının adını belirtir
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM days",null);                                                                                                             //Bu satırda, "days" tablosundaki tüm verileri seçmek için bir SQL sorgusu kullanılarak bir Cursor nesnesi oluşturulur. Bu Cursor nesnesi, veritabanından alınan verilere erişim sağlar.
            int dateIx = cursor.getColumnIndex("date");                                                                                                                                         //id ve tarih verilerini çektik veritabanından
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){                                                                                                                                                //imleç ilerledikçe verileri okumaya devam et
                String date = cursor.getString(dateIx);
                int id = cursor.getInt(idIx);
                Day day = new Day(date ,id);                                                                                                                                    //ikisini ayrı ayrı kaydetmek yerine class oluşturduk
                dayArrayList.add(day);                                                                                                                                                                               //recycleviewda göstermek için diziye initilaze ettik
            }
            dayAdapter.notifyDataSetChanged();                                                                                                                                        //veriseti değiştikçe modifiye et
            cursor.close();                                                                                                                                                      //cursor kapandı

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                                                                                                                     //1 //activiteye menuyu bağladı
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {                                                                                                                          //1 //menudeki opsiyonlardan biri seçilirse ne olacak

        if(item.getItemId() == R.id.add_day){
            Intent intent = new Intent(this, DayActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}