package kr.publicm.mask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private GPS gps;
    private Button btnAllowPermissions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gps = new GPS(this);

        if (gps.checkPermissions(gps.permissions)) {
            exitSplash();
        } else {
            setContentView(R.layout.activity_splash);

            btnAllowPermissions = findViewById(R.id.btnAllowPermissions);
            btnAllowPermissions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gps.getPermissions(gps.permissions);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == gps.permissionCode) {
            if(gps.checkPermissions(permissions)) {
                Toast.makeText(this, getString(R.string.TOAST_ALLOWED), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.TOAST_DISALLOWED), Toast.LENGTH_LONG).show();
            }

            exitSplash();
        }
    }

    private void exitSplash() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}
