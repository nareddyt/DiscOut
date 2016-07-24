package com.outsidehacks.ohana.discout;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "discout-login://callback";
    private static final String CLIENT_ID = "6c6016da831348e18df08257e074c9c4";

    private Button spotifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        spotifyButton = (Button) findViewById(R.id.spotify_login_button);

        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.animate().alpha(0f).setDuration(350).setInterpolator(new OvershootInterpolator()).setListener
                        (new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                                        AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                                builder.setScopes(new String[]{"user-follow-read", "streaming", "user-read-private"});
                                AuthenticationRequest request = builder.build();

                                AuthenticationClient.openLoginActivity(LoginActivity.this, REQUEST_CODE, request);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .start();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams
                    .FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color
                    .colorPrimaryDark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color
                    .colorPrimaryDark));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    mainIntent.putExtra("AUTH_TOKEN", response.getAccessToken());
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
