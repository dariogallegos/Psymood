package com.example.psymood.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.psymood.Activities.MyTaskAdapter;
import com.example.psymood.Models.ItemTask;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.example.psymood.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String KEY_COUNTER_STATE = "COUNTER_STATE";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //little comment
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Elements to Homefragment
    ProgressBar progressBarDay;
    TextView percentageTextView;
    NestedScrollView scrollViewHome;

    RecyclerView recyclerViewTask;
    MyTaskAdapter taskAdapter;
    List<ItemTask> listItemTask;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadTaskList();

        scrollViewHome = view.findViewById(R.id.scrollViewHome);



        int percentage = porcentageToday();
        //TextView with porcentage of today
        percentageTextView = view.findViewById(R.id.percentageTextView);
        percentageTextView.setText(String.valueOf(percentage));

        //Progress bar to show the progress day. Look in share preferences if there are changes.
        progressBarDay = view.findViewById(R.id.progressBarDay);
        progressBarDay.setProgress(percentage);



        //recyclerView task
        recyclerViewTask = view.findViewById(R.id.recyclerViewTask);
        taskAdapter = new MyTaskAdapter(getContext(),listItemTask);


        //RecyclerView and Adpater
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        //recyclerViewTask.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTask.setAdapter(taskAdapter);

        final int initTopPositionScroll = scrollViewHome.getTop();
        if(initTopPositionScroll == 0){
            mListener.onFragmentInteraction(0);
        }

        scrollViewHome.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    mListener.onFragmentInteraction(0);
                }
                else{
                    mListener.onFragmentInteraction(16);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private int porcentageToday() {
        return ApplicationPreferences.loadNumState(KEY_COUNTER_STATE) *10;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
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
        void onFragmentInteraction(int elevation);
    }


    private void loadTaskList() {
        listItemTask = new ArrayList<>();
        listItemTask.add(new ItemTask("Prueba a hacerte un selfie","2",R.color.PurpleTask,R.drawable.ic_task_camera));
        listItemTask.add(new ItemTask("¿Grabamos un vídeo?","3",R.color.PinkTask,R.drawable.ic_task_video));
        listItemTask.add(new ItemTask("Hey, ¿como te encuentras?","6",R.color.GreenTask,R.drawable.ic_task_face));
        listItemTask.add(new ItemTask("Y si me dices algo?","10",R.color.YellowTask,R.drawable.ic_task_mic));
        listItemTask.add(new ItemTask("dario","8",R.color.VioletTask,R.drawable.ic_task_mood));

    }
}
