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

import com.example.psymood.Activities.MyTaskAdapter;
import com.example.psymood.Models.InfoUser;
import com.example.psymood.Models.ItemData;
import com.example.psymood.Models.ItemDays;
import com.example.psymood.Models.ItemGroup;
import com.example.psymood.Models.ItemTask;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.example.psymood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.majorik.sparklinelibrary.SparkLineLayout;

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

    private static final String KEY_ARRAY_CHART = "ARRAY_CHART";
    private static final String KEY_DATE_CHART = "DAY_CHART";
    private static final String KEY_ELEMENT_OF_WEEK = "ELEM_WEEK";

    private static final String KEY_DATE = "DATE";
    private static final String KEY_COUNTER = "COUNTER";
    private static final String KEY_NUM_AUDIO = "NUM_AUDIO";
    private static final String KEY_STATES = "MOOD";
    private static final String KEY_NUM_STATES = "NUM_STATES";
    private static final String KEY_ID_USER = "ID_USER";
    private static final String KEY_NUM_PHOTO = "NUM_PHOTO";

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

    private TextView day1, day2, day3, day4, day5, day6, day7;
    private TextView percentageTextView, nameUserHome, numTaskToday;
    private ProgressBar progressBarDay;
    private SparkLineLayout sparkLineLayout;

    private static final int tenTask = 10;
    private int taskRest;
    private int elementOfWeek;


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
        sparkLineLayout = view.findViewById(R.id.spark_line);
        numTaskToday = view.findViewById(R.id.numTaskToday);

        day1 = view.findViewById(R.id.day1);
        day2 = view.findViewById(R.id.day2);
        day3 = view.findViewById(R.id.day3);
        day4 = view.findViewById(R.id.day4);
        day5 = view.findViewById(R.id.day5);
        day6 = view.findViewById(R.id.day6);
        day7 = view.findViewById(R.id.day7);


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

        //RecyclerView and Adpater from Carousel
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

        paintSparkLinearChart();
        initDaysToLinearChart();

        // Inflate the layout for this fragment
        return view;
    }

    private void paintSparkLinearChart() {

        //Si nunca ha sido declarado entonces lo instanciamos
        int taskCompleted = ApplicationPreferences.loadNumState(KEY_COUNTER);
        ArrayList<String> arrayList = ApplicationPreferences.loadValuesToChart(KEY_ARRAY_CHART);

        if (arrayList == null) {
            arrayList = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                arrayList.add(i, "0");
            }
            elementOfWeek = 1;
        }

        if (isDifferentDayToSparkLine()) {
            if (elementOfWeek > 7) {
                ApplicationPreferences.saveValuesToChart(KEY_ARRAY_CHART, null);
                ApplicationPreferences.saveNumState(KEY_ELEMENT_OF_WEEK, 0);
                elementOfWeek = 1;
            }
        }

        ArrayList<Integer> values = convertArrayListStringToInteger(arrayList);

        //Update of  actual day
        values.set(elementOfWeek - 1, taskCompleted);
        sparkLineLayout.setData(values);
        sparkLineLayout.setSplitLine(true);

        float ratio = (float) 0.145 * elementOfWeek;
        sparkLineLayout.setSplitLineRatio(ratio);
        Log.e("home ratio ", "" + sparkLineLayout.getSplitLineRatio());

        arrayList = convertArrayListIntegerToString(values);

        ApplicationPreferences.saveValuesToChart(KEY_ARRAY_CHART, arrayList);
        ApplicationPreferences.saveNumState(KEY_ELEMENT_OF_WEEK, elementOfWeek);
    }

    private ArrayList<String> convertArrayListIntegerToString(ArrayList<Integer> values) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            arrayList.add(i, String.valueOf(values.get(i)));
        }
        return arrayList;
    }

    private ArrayList<Integer> convertArrayListStringToInteger(ArrayList<String> arrayList) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            values.add(i, Integer.parseInt(arrayList.get(i)));
        }
        return values;
    }

    private boolean isDifferentDayToSparkLine() {

        boolean isDifferent = false;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDay = new SimpleDateFormat("yyyy-MM-dd");
        String formatCurrentDay = currentDay.format(calendar.getTime());

        String lastDay = ApplicationPreferences.loadDate(KEY_DATE_CHART);
        elementOfWeek = ApplicationPreferences.loadNumState(KEY_ELEMENT_OF_WEEK);

        if (!lastDay.equals(formatCurrentDay)) {
            isDifferent = true;
            ApplicationPreferences.saveDate(KEY_DATE_CHART, formatCurrentDay);
            elementOfWeek += 1;
            ApplicationPreferences.saveNumState(KEY_ELEMENT_OF_WEEK, elementOfWeek);
        }
        return isDifferent;
    }

    private void initDaysToLinearChart() {

        ItemDays itemDays = new ItemDays();

        day1.setText(itemDays.getDay1());
        day2.setText(itemDays.getDay2());
        day3.setText(itemDays.getDay3());
        day4.setText(itemDays.getDay4());
        day5.setText(itemDays.getDay5());
        day6.setText(itemDays.getDay6());
        day7.setText(itemDays.getDay7());
    }

    private void initValuesToControl() {

        InfoUser infoUser = ApplicationPreferences.loadInfoUser(KEY_ID_USER);
        defaultTaskList();

        //TODO FALLO DE LA PRIMERA VUELTA
        if (infoUser != null && infoUser.getEmailUser().equals(currentUser.getEmail()) && isEqualsDayToLastDay()) {
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
            numTaskToday.setText(String.valueOf(taskRest));
            percentageTextView.setText(String.valueOf(percentage));
            //Progress bar to show the progress day. Look in share preferences if there are changes.
            progressBarDay.setProgress(percentage);

        } else {
            ApplicationPreferences.saveNumState(KEY_NUM_STATES, 0);
            ApplicationPreferences.saveNumState(KEY_COUNTER, 0);
            ApplicationPreferences.saveNumState(KEY_NUM_AUDIO, 0);
            ApplicationPreferences.saveNumState(KEY_NUM_PHOTO, 0);
            ApplicationPreferences.saveInfoUser(KEY_ID_USER, new InfoUser(currentUser.getEmail()));
            ApplicationPreferences.saveDate(KEY_DATE,calculateCurrentDay());
            //TextView with name user
            nameUserHome.setText(currentUser.getDisplayName());
            percentageTextView.setText("0");
            numTaskToday.setText(String.valueOf(tenTask));
            progressBarDay.setProgress(0);
            defualtGroupList();
        }
    }

    private boolean isEqualsDayToLastDay() {

        String currentDay = calculateCurrentDay();
        Log.e("homeFragment", "Current time => " + currentDay);
        String lastDay = ApplicationPreferences.loadDate(KEY_DATE);

        return lastDay.equals(currentDay);
    }

    private String calculateCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDay = new SimpleDateFormat("yyyy-MM-dd");
        return currentDay.format(calendar.getTime());
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
        int numPhoto = ApplicationPreferences.loadNumState(KEY_NUM_PHOTO);
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
            if (listItemTask.get(i).getMenuItem() == R.id.nav_camera) {
                listItemTask.get(i).setNumTask(String.valueOf(numTask - numPhoto));
            }
        }

    }

    private int porcentToday() {
        int taskCompleted = ApplicationPreferences.loadNumState(KEY_COUNTER);
        if (taskCompleted > tenTask) {
            taskCompleted = tenTask;
        }
        taskRest = tenTask - taskCompleted;
        return taskCompleted * 10;
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
        listItemTask.add(new ItemTask("Hey, ¿como te encuentras?", "4", R.color.GreenTask, R.drawable.ic_task_face, R.id.nav_add));
        listItemTask.add(new ItemTask("Prueba a hacerte un selfie", "1", R.color.PinkTask, R.drawable.ic_task_camera, R.id.nav_camera));
        listItemTask.add(new ItemTask("¿Y si me dices algo?", "1", R.color.PurpleTask, R.drawable.ic_task_mic, R.id.nav_audio));
        listItemTask.add(new ItemTask("Proximamente se podra grabar", "0", R.color.YellowTask, R.drawable.ic_task_video, -1));

    }


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
        listItemData4.add(new ItemData("0 a 3 horas", R.drawable.ic_one_pillow));
        listItemData4.add(new ItemData("3 a 6 horas", R.drawable.ic_two_pillow));
        listItemData4.add(new ItemData("6 a 9 horas", R.drawable.ic_tree_pillow));
        listItemData4.add(new ItemData("9 a 12 horas", R.drawable.ic_zz_pillow));
        listItemData4.add(new ItemData("más de 12", R.drawable.ic_moon_pillow));

        itemGroupList.add(new ItemGroup("emociones", listItemData));
        itemGroupList.add(new ItemGroup("energía", listItemData2));
        itemGroupList.add(new ItemGroup("concentración", listItemData3));
        itemGroupList.add(new ItemGroup("sueño", listItemData4));
        ApplicationPreferences.saveListGroup(KEY_STATES, itemGroupList);
    }
}
