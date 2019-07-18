package com.example.psymood.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.psymood.Activities.FirebaseInteractor;
import com.example.psymood.Activities.MyItemGroupAdapter;
import com.example.psymood.Models.ItemData;
import com.example.psymood.Models.ItemGroup;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.example.psymood.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final String KEYNAME ="MOOD";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerViewGroups;
    private List<ItemGroup> itemGroupList;
    private FloatingActionButton buttonCompleteState;

    public StateFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static StateFragment newInstance(String param1, String param2) {
        StateFragment fragment = new StateFragment();
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


        View view = inflater.inflate(R.layout.fragment_state, container, false);
        itemGroupList = new ArrayList<>();


        //buttonCompleteState = view.findViewById(R.id.buttonCompleteState);
        recyclerViewGroups = view.findViewById(R.id.my_recycler_view);

        recyclerViewGroups.setHasFixedSize(true);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getContext()));


        loadGroupList();


        MyItemGroupAdapter adapter=new MyItemGroupAdapter(getContext(),itemGroupList);
        recyclerViewGroups.setAdapter(adapter);

        final int initialTopPosition = recyclerViewGroups.getTop();
        recyclerViewGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(recyclerViewGroups.getChildAt(0).getTop() < initialTopPosition ) {
                    mListener.onFragmentInteraction(16);
                }
                else{
                    mListener.onFragmentInteraction(0);
                }
            }
        });


        /*buttonCompleteState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("StateFragment","Button is clicked");
            }
        });*/

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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
    public void onDestroyView() {
        super.onDestroyView();
        uploadStateDatabase();
    }

    private void uploadStateDatabase() {

        for(int i = 0; i < itemGroupList.size(); i++){
            for(int j = 0; j < itemGroupList.get(i).getItemList().size(); j++){

                ItemData itemData = itemGroupList.get(i).getItemList().get(j);
                if(itemData.getClicked()){
                    FirebaseInteractor.saveMoodStateInDatabase(itemGroupList.get(i).getTitle(),itemData.getTitle());
                }
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //TODO Comprobar si es la primera vez que entre, si es asi cargarla de cero. Si no es asi entonces cargar la lista
    //TODO del sharepreferences.

    private void loadGroupList() {

        if(ApplicationPreferences.loadListGroup(KEYNAME) != null){

            //Comprobar las fechas.Si la fecha es hoy cargamos sharePreferences, si no cargarmos la lista default.

            itemGroupList = ApplicationPreferences.loadListGroup(KEYNAME);


        }else {
            defaultGroupList();
        }
    }

    private void defaultGroupList() {

        List<ItemData> listItemData = new ArrayList<>();
        listItemData.add(new ItemData("Triste", R.drawable.ic_mood_unhappy));
        listItemData.add(new ItemData("Insatisfecho", R.drawable.ic_mood_very_dissatisfied));
        listItemData.add(new ItemData("Normal", R.drawable.ic_mood_neutro));
        listItemData.add(new ItemData("Satisfecho", R.drawable.ic_mood_very_satisfied));
        listItemData.add(new ItemData("Feliz", R.drawable.ic_mood_happy));


        List<ItemData> listItemData2 = new ArrayList<>();
        listItemData2.add(new ItemData("Triste2", R.drawable.ic_mood_unhappy));
        listItemData2.add(new ItemData("Insatisfecho2", R.drawable.ic_mood_very_dissatisfied));
        listItemData2.add(new ItemData("Normal2", R.drawable.ic_mood_neutro));
        listItemData2.add(new ItemData("Satisfecho2", R.drawable.ic_mood_very_satisfied));
        listItemData2.add(new ItemData("Feliz2", R.drawable.ic_mood_happy));

        List<ItemData> listItemData3 = new ArrayList<>();
        listItemData3.add(new ItemData("Triste3", R.drawable.ic_mood_unhappy));
        listItemData3.add(new ItemData("Insatisfecho3", R.drawable.ic_mood_very_dissatisfied));
        listItemData3.add(new ItemData("Normal3", R.drawable.ic_mood_neutro));
        listItemData3.add(new ItemData("Satisfecho3", R.drawable.ic_mood_very_satisfied));
        listItemData3.add(new ItemData("Feliz3", R.drawable.ic_mood_happy));

        List<ItemData> listItemData4 = new ArrayList<>();
        listItemData4.add(new ItemData("Triste4", R.drawable.ic_mood_unhappy));
        listItemData4.add(new ItemData("Insatisfecho4", R.drawable.ic_mood_very_dissatisfied));
        listItemData4.add(new ItemData("Normal4", R.drawable.ic_mood_neutro));
        listItemData4.add(new ItemData("Satisfecho4", R.drawable.ic_mood_very_satisfied));
        listItemData4.add(new ItemData("Feliz4", R.drawable.ic_mood_happy));


        itemGroupList.add(new ItemGroup("emociones", listItemData));
        itemGroupList.add(new ItemGroup("energía", listItemData2));
        itemGroupList.add(new ItemGroup("concentración", listItemData3));
        itemGroupList.add(new ItemGroup("actividad", listItemData4));
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
        void onFragmentInteraction(int elevation);
    }
}
