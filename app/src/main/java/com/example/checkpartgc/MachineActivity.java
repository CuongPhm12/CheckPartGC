package com.example.checkpartgc;

import static com.example.checkpartgc.api.ApiService.gson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.checkpartgc.api.ApiService;
import com.example.checkpartgc.model.CommonResponse;
import com.example.checkpartgc.model.MI_Master;
import com.example.checkpartgc.model.PartItem;
import com.example.checkpartgc.model.PdaInsertHistoryResponse;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachineActivity extends AppCompatActivity {
    EditText edtPartNo, edtLocation, edtMachineNo, edtModelWO;
    TextView txtResult, txtHeader;
    Button btnReset, btnExit, btnGenerateSpeech;
    ApiService apiService;
    TextToSpeech t1;
    private boolean isTtsReady = false;  // Flag to check if TTS is ready
    String model, wo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_machine);

        edtPartNo = findViewById(R.id.edtPartNo);
        edtLocation = findViewById(R.id.edtLocation);
        edtMachineNo = findViewById(R.id.edtMachineNo);
        edtModelWO = findViewById(R.id.edtModelWO);


        txtResult = findViewById(R.id.txtResult);
        txtHeader = findViewById(R.id.txtHeader);

        btnReset = findViewById(R.id.btnReset);
        btnExit = findViewById(R.id.btnExit);


        txtHeader.setText("Check Machine GC " + "1.01");


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

        edtModelWO.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE) {
                    // Get values from input fields
                    String partNo = edtPartNo.getText().toString();
                    String location = edtLocation.getText().toString();
                    String machineNo = edtMachineNo.getText().toString();
                    String barcode = edtModelWO.getText().toString().trim();
                    String[] result = processString(barcode);

                    if (result == null) {

                        Toast.makeText(MachineActivity.this, "Error: Not correct format", Toast.LENGTH_SHORT).show();
                        edtModelWO.setText("");
                        edtModelWO.requestFocus();
                    } else {
                        wo = result[1];
                        model = result[0];

                    }

                    if (validateInput(partNo, location, machineNo)) {
                        // Show a progress dialog
                        ProgressDialog progressDialog = ProgressDialog.show(MachineActivity.this, "Please Wait", "Fetching part details...", true);
                        ApiService.pdaService.checkCorrectMachineNo( partNo, location, machineNo).enqueue(new Callback<CommonResponse>() {
                            @Override
                            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                                progressDialog.dismiss();

                                if (response.isSuccessful() && response.body() != null) {
                                    String pStatus = response.body().getpStatus();

                                    if ("Y".equals(pStatus)) {
                                        // Save log or show success message
                                        Log.e("checkCorrectMachineNo", "Status: " + pStatus);
                                        ApiService.pdaService.insertTblCheckMachineIDHistory( partNo, location, machineNo,model,wo,"OK").enqueue(new Callback<CommonResponse>() {
                                            @Override
                                            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                                                progressDialog.dismiss();

                                                if (response.isSuccessful() && response.body() != null) {
                                                    String pStatus = response.body().getpStatus();

                                                    if ("Y".equals(pStatus)) {


                                                    } else if ("N".equals(pStatus)) {
                                                        Toast.makeText(MachineActivity.this, "Error: Lịch sử chưa được lưu", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(MachineActivity.this, "Error: insertTblCheckMachineIDHistory -Invalid response from server", Toast.LENGTH_SHORT).show();
                                                    edtModelWO.setError("insertTblCheckMachineIDHistory-Invalid response from server");
                                                    edtModelWO.requestFocus();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<CommonResponse> call, Throwable t) {
                                                Log.e("API insertTblCheckMachineIDHistory call failed", "Status: " + t.getMessage());
                                                Toast.makeText(MachineActivity.this, "API insertTblCheckMachineIDHistory call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                edtModelWO.setError("API insertTblCheckMachineIDHistory call failed");
                                                edtModelWO.requestFocus();

                                            }
                                        });
                                        txtResult.setText("OK");
                                        txtResult.setTextColor(Color.parseColor("#6495ED"));
                                        txtResult.setBackgroundColor(Color.parseColor("#DAF7A6"));
                                        speakResult();
                                        edtModelWO.setText("");
                                        edtModelWO.requestFocus();

                                    } else if ("N".equals(pStatus)) {
                                        ApiService.pdaService.insertTblCheckMachineIDHistory( partNo, location, machineNo,model,wo,"NG").enqueue(new Callback<CommonResponse>() {
                                            @Override
                                            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                                                progressDialog.dismiss();

                                                if (response.isSuccessful() && response.body() != null) {
                                                    String pStatus = response.body().getpStatus();

                                                    if ("Y".equals(pStatus)) {


                                                    } else if ("N".equals(pStatus)) {
                                                        Toast.makeText(MachineActivity.this, "Error: Lịch sử chưa được lưu", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(MachineActivity.this, "Error: insertTblCheckMachineIDHistory -Invalid response from server", Toast.LENGTH_SHORT).show();
                                                    edtModelWO.setError("insertTblCheckMachineIDHistory-Invalid response from server");
                                                    edtModelWO.requestFocus();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<CommonResponse> call, Throwable t) {
                                                Log.e("API insertTblCheckMachineIDHistory call failed", "Status: " + t.getMessage());
                                                Toast.makeText(MachineActivity.this, "API insertTblCheckMachineIDHistory call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                edtModelWO.setError("API insertTblCheckMachineIDHistory call failed");
                                                edtModelWO.requestFocus();

                                            }
                                        });
                                        txtResult.setText("NG");
                                        txtResult.setTextColor(Color.WHITE);
                                        txtResult.setBackgroundColor(Color.parseColor("#cd6155"));
                                        speakResult();
                                        edtModelWO.setText("");
                                        edtModelWO.requestFocus();
                                    }
                                } else {
                                    Toast.makeText(MachineActivity.this, "Error: Invalid response from server", Toast.LENGTH_SHORT).show();
                                    edtModelWO.setError("Invalid response from server");
                                    edtModelWO.requestFocus();
                                }

                            }

                            @Override
                            public void onFailure(Call<CommonResponse> call, Throwable t) {
//                                progressDialog.dismiss();
                                Log.e("API call failed", "Status: " + t.getMessage());
                                Toast.makeText(MachineActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                edtModelWO.setError("API call failed");
                                edtModelWO.requestFocus();

                            }
                        });

                    } else {
                        Toast.makeText(MachineActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    }

                }
                return false;
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPartNo.setText("");
                edtLocation.setText("");
                edtMachineNo.setText("");
                edtModelWO.setText("");

                edtPartNo.requestFocus();
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
            Toast.makeText(MachineActivity.this, "TextToSpeech not ready or text is empty", Toast.LENGTH_LONG).show();
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

    private boolean validateInput(String partNo, String location, String machineNo) {
        return !partNo.isEmpty() && !location.isEmpty() && !machineNo.isEmpty();
    }

    private String[] processString(String barcode) {
        // Check if the input string contains the '|' delimiter
        if (!barcode.contains("|")) {
            return null; // Not correct format
        }

        // Split the string into two parts
        String[] parts = barcode.split("\\|");


        // Validate the second part: must be 10 characters long and contain only digits
        if (parts.length < 2 || parts[1].length() != 10 || !parts[1].matches("\\d{10}")) {
            return null; // Not correct format
        }

        return parts; // Return the two parts if format is correct
    }
}