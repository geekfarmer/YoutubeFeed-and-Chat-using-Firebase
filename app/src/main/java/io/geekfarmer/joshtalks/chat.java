package io.geekfarmer.joshtalks;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;

public class chat extends Fragment {
    public static final int SIGN_IN_REQUEST_CODE = 111;
    public FirebaseListAdapter<ChatMessage> adapter;
    public ListView listView;
    public String loggedInUserName = "";
    public static final String TITLE = "CHAT";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        //find views by Ids
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        final EditText input = (EditText) rootView.findViewById(R.id.input);
        listView = (ListView) rootView.findViewById(R.id.list);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, show list of messages
            showAllOldMessages();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(getActivity(), "Sign in failed, please try again later", Toast.LENGTH_LONG).show();

                // Close the app
                getActivity().finish();
            }
        }
    }

    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

        adapter = new MessageAdapter(this.getActivity(), ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference());
        listView.setAdapter(adapter);
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }
}