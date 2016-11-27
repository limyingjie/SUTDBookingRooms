package lol.wepekchek.istd.sutdbookingrooms.RoomSearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import lol.wepekchek.istd.sutdbookingrooms.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;


public class RoomSearchFragment extends Fragment {
    private DatabaseReference mDatabase;
    public HashMap<String,String> data;
    private Spinner daySpinner,monthSpinner,yearSpinner,hourSpinner,durationSpinner;
    private Button button;
    private String[] times;

    private OnFragmentInteractionListener mListener;

    public RoomSearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_search, container, false);

        //setupSpinner(view,daySpinner,R.array.days_array,R.id.spinner);

        daySpinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        daySpinner.setAdapter(adapter);
        //daySpinner.setOnItemSelectedListener(this);

        monthSpinner = (Spinner) view.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter2);
       // monthSpinner.setOnItemSelectedListener(this);

        yearSpinner = (Spinner) view.findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.years_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter3);
        //yearSpinner.setOnItemSelectedListener(this);

        hourSpinner = (Spinner) view.findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.hours_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(adapter4);
        //hourSpinner.setOnItemSelectedListener(this);

        durationSpinner = (Spinner) view.findViewById(R.id.spinner5);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.duration_array, android.R.layout.simple_spinner_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapter5);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        button=(Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v==v.findViewById(R.id.button))viewData(v);

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        //mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void viewData(View view) {
        String[] hours=new String[Integer.parseInt(durationSpinner.getSelectedItem().toString())];
        int hour=Integer.parseInt(hourSpinner.getSelectedItem().toString());
        for (int i=1;i<=Integer.parseInt(durationSpinner.getSelectedItem().toString());i++){
            if (hour==0)hours[i-1]="0000";
            else if (hour<=900)hours[i-1]="0"+String.valueOf(hour);
            else hours[i-1]=String.valueOf(hour);
            hour+=100;
        }
        //Toast.makeText(getActivity(), Arrays.toString(hours), Toast.LENGTH_SHORT).show();
        final String dateAndTime = daySpinner.getSelectedItem().toString()+
                monthSpinner.getSelectedItem().toString()+
                yearSpinner.getSelectedItem().toString()+
                hourSpinner.getSelectedItem().toString();
        times =new String[hours.length];
        for (int i=0;i<hours.length;i++){
            times[i]= daySpinner.getSelectedItem().toString()+
                    monthSpinner.getSelectedItem().toString()+
                    yearSpinner.getSelectedItem().toString()+ hours[i];
        }
        Toast.makeText(getActivity(), Arrays.toString(times), Toast.LENGTH_SHORT).show();
        Query myQuery = mDatabase.child("Rooms");
        try {
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        data = (HashMap) dataSnapshot.getValue();
                        String[] rooms={"55L2","55L9"};
                        String availableRooms="";
                        boolean isTaken=false;
                        for (String room:rooms){
                            isTaken=false;
                            for (String time:times){
                                if(dataSnapshot.child(room).hasChild(time)){
                                    isTaken=true;
                                    break;
                                }
                            }
                            if(!isTaken)availableRooms+=room+" ";
                        }
//                        if(dataSnapshot.hasChild(dateAndTime))Toast.makeText(getActivity(), "taken", Toast.LENGTH_SHORT).show();
//                        else{Toast.makeText(getActivity(), "55lvl2 available on chosen timing for 1 hr", Toast.LENGTH_SHORT).show();}
                        Toast.makeText(getActivity(), "Available rooms: "+availableRooms, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Exception", Toast.LENGTH_SHORT).show();
        }
    }


}
