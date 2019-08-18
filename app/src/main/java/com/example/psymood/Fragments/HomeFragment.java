package com.example.psymood.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.psymood.Activities.FirebaseInteractor;
import com.example.psymood.Activities.MyTaskAdapter;
import com.example.psymood.Models.InfoUser;
import com.example.psymood.Models.ItemData;
import com.example.psymood.Models.ItemGroup;
import com.example.psymood.Models.ItemTask;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.example.psymood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private static final String KEY_DATE = "DATE";
    private static final String KEY_COUNTER = "COUNTER";
    private static final String KEY_NUM_AUDIO = "NUM_AUDIO";
    private static final String KEY_STATES = "MOOD";
    private static final String KEY_NUM_STATES = "NUM_STATES";
    private static final String KEY_ID_USER = "ID_USER";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //little comment
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private MyTaskAdapter taskAdapter;
    private List<ItemTask> listItemTask;
    private List<ItemGroup> itemGroupList;
    private FirebaseUser currentUser;

    private TextView percentageTextView, nameUserHome;
    private ProgressBar progressBarDay;

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


    //TODO el homeFragment debe ser el que cargue las listas de estados por defecto o las del dia normal, y si la proxima vex que entra es un dia
    //TODO distito , entonces se ha de borrar todo.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //objects to home fragment view
        NestedScrollView scrollViewHome = view.findViewById(R.id.scrollViewHome);
        nameUserHome = view.findViewById(R.id.nameUserHome);
        percentageTextView = view.findViewById(R.id.percentageTextView);
        progressBarDay = view.findViewById(R.id.progressBarDay);


        //Firebase instace and obtain current
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //init to list lo task and group list items.
        initValuesToControl();

        //recyclerView task
        RecyclerView recyclerViewTask = view.findViewById(R.id.recyclerViewTask);
        taskAdapter = new MyTaskAdapter(getContext(), listItemTask, new MyTaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(ItemTask itemTask) {
                mListener.onChangeFragment(itemTask.getMenuItem());
            }
        });

        //RecyclerView and Adpater
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //recyclerViewTask.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTask.setAdapter(taskAdapter);


        final int initTopPositionScroll = scrollViewHome.getTop();
        if (initTopPositionScroll == 0) {
            mListener.onFragmentInteraction(0);
        }

        scrollViewHome.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    mListener.onFragmentInteraction(0);
                } else {
                    mListener.onFragmentInteraction(16);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    private void initValuesToControl() {

        InfoUser infoUser =  ApplicationPreferences.loadInfoUser(KEY_ID_USER);
        defaultTaskList();

        if (infoUser != null && infoUser.getEmailUser().equals(currentUser.getEmail()) && !differentDayToLastUses()) {
            if (ApplicationPreferences.loadListGroup(KEY_STATES) != null) {
                itemGroupList = ApplicationPreferences.loadListGroup(KEY_STATES);
            } else {
                defualtGroupList();
            }
            //update the number of pendent task
            loadCounterState();
            updateNumberTask();

            int percentage = porcentToday();
            //TextView with porcentage of today

            //TextView with name user
            nameUserHome.setText(currentUser.getDisplayName());
            percentageTextView.setText(String.valueOf(percentage));
            //Progress bar to show the progress day. Look in share preferences if there are changes.
            progressBarDay.setProgress(percentage);

        } else {
            ApplicationPreferences.saveNumState(KEY_NUM_STATES, 0);
            ApplicationPreferences.saveNumState(KEY_COUNTER, 0);
            ApplicationPreferences.saveNumState(KEY_NUM_AUDIO, 0);
            ApplicationPreferences.saveInfoUser(KEY_ID_USER,new InfoUser(currentUser.getEmail()));

            //TextView with name user
            nameUserHome.setText(currentUser.getDisplayName());
            percentageTextView.setText("0");
            progressBarDay.setProgress(0);
            defualtGroupList();

        }
    }

    private boolean differentDayToLastUses() {
        boolean isDifferent = false;

        Calendar calendar = Calendar.getInstance();

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat currentDay = new SimpleDateFormat("yyyy-MM-dd");
        String formatCurrentDay = currentDay.format(calendar.getTime());

        Log.e("homeFragment", "Current time => " + formatCurrentDay);
        String lastDay = ApplicationPreferences.loadDate(KEY_DATE);

        if (!lastDay.equals(formatCurrentDay)) {
            isDifferent = true;
            ApplicationPreferences.saveDate(KEY_DATE, formatCurrentDay);
        }
        return isDifferent;
    }

    //Se usa para mostrar el numero de tareas restantes de la parte de los estados.
    private void loadCounterState() {

        if (itemGroupList != null) {

            ApplicationPreferences.saveNumState(KEY_NUM_STATES, 0);
            //Comprobar las fechas.Si la fecha es hoy cargamos sharePreferences, si no cargarmos la lista default.

            for (int i = 0; i < itemGroupList.size(); i++) {
                for (int j = 0; j < itemGroupList.get(i).getItemList().size(); j++) {
                    ItemData itemData = itemGroupList.get(i).getItemList().get(j);
                    if (itemData.getClicked()) {
                        int numStates = ApplicationPreferences.loadNumState(KEY_NUM_STATES) + 1;
                        ApplicationPreferences.saveNumState(KEY_NUM_STATES, numStates);
                    }
                }
            }
        }
    }

    private void updateNumberTask() {
        int numAudio = ApplicationPreferences.loadNumState(KEY_NUM_AUDIO);
        int numStates = ApplicationPreferences.loadNumState(KEY_NUM_STATES);

        Log.e("Homefragment", "" + numStates);

        //sacar el contador de cada tarea

        for (int i = 0; i < listItemTask.size(); i++) {
            int numTask = Integer.parseInt(listItemTask.get(i).getNumTask());

            if (listItemTask.get(i).getMenuItem() == R.id.nav_audio) {
                listItemTask.get(i).setNumTask(String.valueOf(numTask - numAudio));
            }
            if (listItemTask.get(i).getMenuItem() == R.id.nav_add) {
                listItemTask.get(i).setNumTask(String.valueOf(numTask - numStates));
            }
        }

    }

    private int porcentToday() {
        return ApplicationPreferences.loadNumState(KEY_COUNTER) * 10;
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

        void onChangeFragment(int menuItem);
    }


    private void defaultTaskList() {
        listItemTask = new ArrayList<>();
        listItemTask.add(new ItemTask("Prueba a hacerte un selfie", "1", R.color.PurpleTask, R.drawable.ic_task_camera, R.id.nav_camera));
        listItemTask.add(new ItemTask("¿Grabamos un vídeo?", "1", R.color.PinkTask, R.drawable.ic_task_video, R.id.nav_camera));
        listItemTask.add(new ItemTask("Hey, ¿como te encuentras?", "4", R.color.GreenTask, R.drawable.ic_task_face, R.id.nav_add));
        listItemTask.add(new ItemTask("¿Y si me dices algo?", "1", R.color.YellowTask, R.drawable.ic_task_mic, R.id.nav_audio));
        listItemTask.add(new ItemTask("Una encuensta rápida", "1", R.color.VioletTask, R.drawable.ic_task_mood, R.id.nav_add));

    }

    //TODO CREAR UNA FACTORIA O ALGO DONDE GUARDAR ESTA INFORMACION O UNA CLASE .
    private void defualtGroupList() {
        itemGroupList = new ArrayList<>();

        List<ItemData> listItemData = new ArrayList<>();

        listItemData.add(new ItemData("Triste", R.drawable.ic_mood_unhappy));
        listItemData.add(new ItemData("Insatisfecho", R.drawable.ic_mood_very_dissatisfied));
        listItemData.add(new ItemData("Normal", R.drawable.ic_mood_neutro));
        listItemData.add(new ItemData("Satisfecho", R.drawable.ic_mood_very_satisfied));
        listItemData.add(new ItemData("Feliz", R.drawable.ic_mood_happy));

        List<ItemData> listItemData2 = new ArrayList<>();
        listItemData2.add(new ItemData("Exhausta", R.drawable.ic_energy_very_low));
        listItemData2.add(new ItemData("Baja", R.drawable.ic_energy_low));
        listItemData2.add(new ItemData("Normal", R.drawable.ic_energy_normal));
        listItemData2.add(new ItemData("Alta", R.drawable.ic_energy_high));
        listItemData2.add(new ItemData("Energizada", R.drawable.ic_energy_very_high));

        List<ItemData> listItemData3 = new ArrayList<>();
        listItemData3.add(new ItemData("Nada", R.drawable.ic_diana_vacia));
        listItemData3.add(new ItemData("Poco", R.drawable.ic_diana_cero));
        listItemData3.add(new ItemData("Suficiente", R.drawable.ic_diana_suficiente));
        listItemData3.add(new ItemData("Bastante", R.drawable.ic_diana_bastante));
        listItemData3.add(new ItemData("Mucha", R.drawable.ic_diana_mucho));

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
        ApplicationPreferences.saveListGroup(KEY_STATES, itemGroupList);
    }
}
