package nl.s132054.materialdesigntest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.*;
import com.firebase.client.utilities.Base64;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecondFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment implements View.OnClickListener, ValueEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Firebase firebase;
    private Button button;
    private String account;

    private OnFragmentInteractionListener mListener;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = (Button) getActivity().findViewById(R.id.FireBaseButton);

        Firebase.setAndroidContext(getActivity());
        account = AccountManager.get(getContext()).getAccountsByType("com.google")[0].name.replace('.', '_');
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String token = "";
                try {
                    String accountstr = params[0];
                    URL tokenURL = new URL("http://niels.xyz/DBLAPPDEV/key.php?in="+accountstr);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) tokenURL.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setChunkedStreamingMode(0);
                    Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                    token = scanner.nextLine();
                } catch (java.io.IOException e) { }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                firebase = new Firebase("https://flickering-heat-9387.firebaseio.com/");
                super.onPostExecute(token);
                firebase.authWithCustomToken(token, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        firebase.child("users").child(account).addValueEventListener(SecondFragment.this);
                        button.setOnClickListener(SecondFragment.this);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        firebaseError.toException().printStackTrace();
                    }
                });
                System.out.println("authenticated");
            }
        }.execute(account);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            EditText editText = (EditText) getActivity().findViewById(R.id.FireBaseInput);
            String text = editText.getText().toString();
            firebase.child("users").child(account).setValue(text);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        TextView textView = (TextView) getActivity().findViewById(R.id.FireBaseText);
        String value = dataSnapshot.getValue() ==  null ? "" : dataSnapshot.getValue().toString();
        textView.setText(value);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
