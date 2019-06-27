package com.aldi.signingoogle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    public TextView accountName;
    private CardView profileCard;
    private ImageView accountImage;
    private SignInButton signInButton;
    private AppCompatButton signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.sign_in_button);



        profileCard = findViewById(R.id.profile_card);
        accountName = findViewById(R.id.account_name);
        accountImage = findViewById(R.id.account_image);
        signOutButton = findViewById(R.id.sign_out_button);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUi(account);
            profileCard.setVisibility(View.VISIBLE);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("esmeraldi", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public Bitmap getImageFromUrl(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bmp;
        } catch (Exception er) {
            Log.e("Error" , er.getStackTrace().toString());
            return null;
        }
    }

    private void updateUi(GoogleSignInAccount account) {
        accountName.setText(account.getDisplayName());
        signInButton.setVisibility(View.GONE);
        if(account.getPhotoUrl() != null) {
            accountImage.setImageBitmap(getImageFromUrl(account.getPhotoUrl()));
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
        signInButton.setVisibility(View.VISIBLE);
        profileCard.setVisibility(View.GONE);
        Log.d("sdsd", mGoogleSignInClient.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }
}
