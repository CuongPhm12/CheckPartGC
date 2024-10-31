package com.example.checkpartgc;

import static com.example.checkpartgc.api.ApiService.gson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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


import com.example.checkpartgc.api.ApiService;
import com.example.checkpartgc.model.ApiResponse;
import com.example.checkpartgc.model.MI_Master;
import com.example.checkpartgc.model.PartItem;
import com.example.checkpartgc.model.PdaInsertHistoryResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtIssueNo, edtWO, edtModel, edtPart;
    TextView txtSTT, txtViTriCam, txtQuyCach, txtResult, txtCount, txtTotal;
    Button btnReset, btnExit, btnGenerateSpeech, btnShow;
    ApiService apiService;
    TextToSpeech t1;
    private boolean isTtsReady = false;  // Flag to check if TTS is ready
    List<String> partItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        edtIssueNo = findViewById(R.id.edtIssueNo);
        edtWO = findViewById(R.id.edtWO);
        edtModel = findViewById(R.id.edtModel);
        edtPart = findViewById(R.id.edtPart);

        txtSTT = findViewById(R.id.txtSTT);
        txtViTriCam = findViewById(R.id.txtViTriCam);
        txtQuyCach = findViewById(R.id.txtQuyCach);
        txtResult = findViewById(R.id.txtResult);
        txtCount = findViewById(R.id.txtCount);
        txtTotal = findViewById(R.id.txtTotal);

        btnReset = findViewById(R.id.btnReset);
        btnExit = findViewById(R.id.btnExit);
        btnShow = findViewById(R.id.btnShow);


        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = t1.setLanguage(Locale.US);

                    t1.setSpeechRate(1.0f);


                    // Check if the language is supported
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        isTtsReady = true;  // Mark TTS as ready
//                        speakResult();  // Automatically trigger speech after initialization
                        Log.d("TTS", "TextToSpeech is ready");
//                        Toast.makeText(MainActivity.this, "TextToSpeech is ready", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TTS", "Initialization failed: " + status);
//                    Toast.makeText(MainActivity.this, "Initialization failed: " + status, Toast.LENGTH_SHORT).show();

                }
            }


        });
        edtIssueNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE) {
                    // Get values from input fields
                    String issueNo = "0" + edtIssueNo.getText().toString();
//                    if (issueNo.length() >= 3 && !issueNo.substring(0, 3).equals("018")){
//                        return false;}

                    ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Checking Issue No...", true);
                    ApiService.apiService_GetPartByIssueNo.GetPartByIssueNo(issueNo).enqueue(new Callback<List<String>>() {
                        @Override
                        public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                partItemList = response.body();
                                if (!partItemList.isEmpty()) {
                                    txtTotal.setText("/" + String.valueOf(partItemList.size()));
                                } else {
                                    Toast.makeText(MainActivity.this, "Issue No Not Found", Toast.LENGTH_SHORT).show();
                                    txtTotal.setText("/0");
                                    edtIssueNo.requestFocus();

                                }
                            } else {
                                // Extract detailed error message for debugging
                                String errorMessage = "No error message";
                                try {
                                    if (response.errorBody() != null) {
                                        errorMessage = response.errorBody().string(); // Read the error message from response
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Log.e("API_ERROR", "Response failed. Status code: " + response.code() + ", Error: " + errorMessage);
                                Toast.makeText(MainActivity.this, "Error: Invalid response. Status code: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<String>> call, Throwable t) {
                            progressDialog.dismiss(); // Dismiss the dialog in case of failure
                            // Log the error message for debugging
                            Log.e("API_ERROR", "API Call Failed: ", t);
                            Toast.makeText(MainActivity.this, "Call API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                return false;
            }
        });

        edtPart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE) {
                    // Get values from input fields
                    String partNo = edtPart.getText().toString();
                    String issueNo = "0" + edtIssueNo.getText().toString();
                    String model = edtModel.getText().toString();

                    if (validateInput(partNo, issueNo, model)) {
                        // Show a progress dialog
                        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Fetching part details...", true);
                        ApiService.pdaService.pdaGetPartGC(model, partNo, issueNo).enqueue(new Callback<List<MI_Master>>() {
                            @Override
                            public void onResponse(Call<List<MI_Master>> call, Response<List<MI_Master>> response) {
                                progressDialog.dismiss(); // Dismiss the dialog once we have a response

                                List<MI_Master> mi_Masters = response.body();

                                if (mi_Masters != null && !mi_Masters.isEmpty()) {
                                    txtResult.setText("OK");

                                    MI_Master mi_Master = mi_Masters.get(0);

                                    txtSTT.setText(mi_Master.getMI_INDEX());
                                    txtViTriCam.setText(mi_Master.getLOCATION_ID());
                                    txtQuyCach.setText(mi_Master.getSPEC());
                                    txtCount.setText(String.valueOf(mi_Master.getCOUNTOFCHECK()));
                                    speakResult();
                                    edtPart.setText("");
                                    edtPart.requestFocus();

                                } else {
                                    txtResult.setText("NG");

                                    speakResult();
                                    edtPart.setText("");
                                    txtSTT.setText("");
                                    txtViTriCam.setText("");
                                    txtQuyCach.setText("");

                                    edtPart.requestFocus();

                                }
                            }

                            @Override
                            public void onFailure(Call<List<MI_Master>> call, Throwable t) {
                                progressDialog.dismiss(); // Dismiss the dialog in case of failure
                                Log.e("API_ERROR", "API Call Failed: ", t);
                                Toast.makeText(MainActivity.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    }

                }
                return false;
            }
        });

        edtModel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE) {
                    // Get values from input fields
                    String issueNo = "0" + edtIssueNo.getText().toString();
                    String model = edtModel.getText().toString();

                    ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Insert Issue No...", true);

                    for (int j = 0; j < partItemList.size(); j++) {
                        String partNo = partItemList.get(j).toString();


                        ApiService.pdaService.pdaInsertHistory(model, partNo, issueNo).enqueue(new Callback<PdaInsertHistoryResponse>() {
                            @Override
                            public void onResponse(Call<PdaInsertHistoryResponse> call, Response<PdaInsertHistoryResponse> response) {
                                progressDialog.dismiss();

                                if (response.isSuccessful() && response.body() != null) {
                                    String pStatus = response.body().getpStatus();

                                    if ("OK".equals(pStatus) || "ER_DUPLICATE".equals(pStatus)) {
                                        // Save log or show success message
                                        Log.e("InsertHistory", "Status: " + pStatus);
                                    } else if ("ER".equals(pStatus)) {
                                        // Show error message and focus on edtModel
                                        Toast.makeText(MainActivity.this, "Error: Insertion failed", Toast.LENGTH_SHORT).show();
                                        edtModel.setError("Insert Fail " + partNo + "at position ");
                                        edtModel.requestFocus();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Error: Invalid response from server", Toast.LENGTH_SHORT).show();
                                    edtModel.setError("Invalid response from server");
                                    edtModel.requestFocus();
                                }

                            }

                            @Override
                            public void onFailure(Call<PdaInsertHistoryResponse> call, Throwable t) {
//                                progressDialog.dismiss();
                                Log.e("API call failed", "Status: " + t.getMessage());
                                Toast.makeText(MainActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                edtModel.setError("API call failed");
                                edtModel.requestFocus();

                            }
                        });
                    }

                }
                return false;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtIssueNo.setText("");
                edtModel.setText("");
                edtWO.setText("");
                edtPart.setText("");

                txtSTT.setText("");
                txtViTriCam.setText("");
                txtQuyCach.setText("");

                txtCount.setText("0");
                txtTotal.setText("/0");

                edtIssueNo.requestFocus();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String model = edtModel.getText().toString();
                String issueNo = "0" + edtIssueNo.getText().toString();

                Intent myIntent = new Intent(MainActivity.this, PartNoListActivity.class);
                ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Showing PartNo list Not checked...", true);

                ApiService.pdaService.Get_PartNo_NotChecked(model, issueNo).enqueue(new Callback<List<PartItem>>() {
                    @Override
                    public void onResponse(Call<List<PartItem>> call, Response<List<PartItem>> response) {
                        progressDialog.dismiss(); // Dismiss the dialog once we have a response

                        if (response.isSuccessful()) {
                            List<PartItem> partNoList = response.body();
                            String jsonList = gson.toJson(partNoList);
                            if (partNoList != null && !partNoList.isEmpty()) {
                                //ArrayList<PartItem> parcelablePartNoList = new ArrayList<>(partNoList); // Convert List to ArrayList
                                Bundle myBundle = new Bundle();
                                myBundle.putString("list", jsonList);
                                myIntent.putExtra("myPackage", myBundle);
                                startActivity(myIntent);
                            } else {
                                Toast.makeText(MainActivity.this, "Have no PartNo to check", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PartItem>> call, Throwable t) {
                        progressDialog.dismiss(); // Dismiss the dialog in case of failure
                        Log.e("API_ERROR", "API Call Failed: ", t);
                        Toast.makeText(MainActivity.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void speakResult() {
        String text = txtResult.getText().toString();
        if (isTtsReady) {
            if (!text.isEmpty()) {
                // Check if the text is "NG" and modify it
                if (text.equalsIgnoreCase("NG")) {
                    text = "N G";  // Separate the letters with a space

                } else if (text.equalsIgnoreCase("OK")) {
                    text = "O K";  // Separate the letters with a space

                }
                t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.e("TTS", "Text is empty, cannot speak");
//                Toast.makeText(MainActivity.this, "Text is empty, cannot speak", Toast.LENGTH_LONG).show();
            }

        } else {
//            Log.e("TTS", "TextToSpeech not ready or text is empty");
            Toast.makeText(MainActivity.this, "TextToSpeech not ready or text is empty", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Shutdown TextToSpeech to release resources
        if (t1 != null && isTtsReady) {
//        if (t1 != null ) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

    private boolean validateInput(String partNo, String issueNo, String model) {
        return !partNo.isEmpty() && !issueNo.isEmpty() && !model.isEmpty();
    }

}