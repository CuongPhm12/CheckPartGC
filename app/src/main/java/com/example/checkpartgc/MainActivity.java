package com.example.checkpartgc;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkpartgc.api.ApiService;
import com.example.checkpartgc.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

EditText edtIssueNo, edtWO,edtModel,edtPart;
TextView txtSTT, txtViTriCam,txtQuyCach;
Button btnReset, btnExit;
ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

            edtIssueNo = findViewById(R.id.edtIssueNo);
            edtWO = findViewById(R.id.edtWO);
            edtModel = findViewById(R.id.edtModel);
            edtPart = findViewById(R.id.edtPart);

            txtSTT =  findViewById(R.id.txtSTT);
            txtViTriCam =  findViewById(R.id.txtViTriCam);
            txtQuyCach =  findViewById(R.id.txtQuyCach);

            btnReset = findViewById(R.id.btnReset);
            btnExit = findViewById(R.id.btnExit);

            edtPart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if(keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                        String partNo = edtPart.getText().toString();
                        String issueNo = edtIssueNo.getText().toString();
                        ApiService.apiService.checkPartExist("CA2A",partNo,issueNo).enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if(response.isSuccessful()&&response.body()!=null&& response.body().isStatus()){
                                    if(response.body().isResult()){
                                    Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();}else{
                                        Toast.makeText(MainActivity.this, "NG", Toast.LENGTH_SHORT).show();

                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "Response error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Call API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return false;
                }
            });


            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edtIssueNo.setText("");
                    edtWO.setText("");
                    edtModel.setText("");
                    edtPart.setText("");

                    txtSTT.setText("");
                    txtViTriCam.setText("");
                    txtQuyCach.setText("");


                    edtIssueNo.requestFocus();
                }
            });

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

    }
}