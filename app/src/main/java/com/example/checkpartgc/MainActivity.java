package com.example.checkpartgc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.example.checkpartgc.model.MI_Master;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtIssueNo, edtWO, edtModel, edtPart;
    TextView txtSTT, txtViTriCam, txtQuyCach;
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

        txtSTT = findViewById(R.id.txtSTT);
        txtViTriCam = findViewById(R.id.txtViTriCam);
        txtQuyCach = findViewById(R.id.txtQuyCach);

        btnReset = findViewById(R.id.btnReset);
        btnExit = findViewById(R.id.btnExit);

        edtPart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)||i == EditorInfo.IME_ACTION_DONE) {
                    // Get values from input fields
                    String partNo = edtPart.getText().toString();
                    String issueNo = edtIssueNo.getText().toString();
                    String model = edtModel.getText().toString();
                    
                    if(validateInput(partNo, issueNo, model)){
                        // Start the process to check the part and get details
                        checkPartAndFetchDetails("CA2A",partNo, issueNo, model);
                    }else{
                        Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    }

                }
                return false;
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPart.setText("");

                txtSTT.setText("");
                txtViTriCam.setText("");
                txtQuyCach.setText("");


                edtPart.requestFocus();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void checkPartAndFetchDetails(String whCode, String partNo, String issueNo, String model) {
        // Show a progress dialog
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"Please Wait", "Checking part...", true);
        ApiService.apiService.checkPartExist(whCode, partNo, issueNo).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();// Dismiss the dialog once we have a response

                if (response.isSuccessful() && response.body() != null ) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isStatus() && apiResponse.isResult()) {
                        Toast.makeText(MainActivity.this, "Part Exists", Toast.LENGTH_SHORT).show();
                        fetchPartDetails(model, partNo);


                    } else {

                        Toast.makeText(MainActivity.this, "Part Not Found", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(MainActivity.this, "Error: Invalid response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log the error message for debugging
                Log.e("API_ERROR", "API Call Failed: ", t);
                Toast.makeText(MainActivity.this, "Call API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPartDetails(String model, String partNo) {
        // Show a progress dialog
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Fetching part details...", true);
        ApiService.pdaService.pdaGetPartGC(model, partNo).enqueue(new Callback<List<MI_Master>>() {
            @Override
            public void onResponse(Call<List<MI_Master>> call, Response<List<MI_Master>> response) {
                progressDialog.dismiss(); // Dismiss the dialog once we have a response

                List<MI_Master> mi_Masters = response.body();

                if (!mi_Masters.isEmpty()) {
                    MI_Master mi_Master = mi_Masters.get(0);
                    txtSTT.setText(mi_Master.getMI_INDEX());
                    txtViTriCam.setText(mi_Master.getLOCATION_ID());
                    txtQuyCach.setText(mi_Master.getSPEC());

                }else{

                    Toast.makeText(MainActivity.this, "Error: Part details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MI_Master>> call, Throwable t) {
                progressDialog.dismiss(); // Dismiss the dialog in case of failure
                Log.e("API_ERROR", "API Call Failed: ", t);
                Toast.makeText(MainActivity.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String partNo, String issueNo, String model) {
        return !partNo.isEmpty() && !issueNo.isEmpty() && !model.isEmpty();
    }
}