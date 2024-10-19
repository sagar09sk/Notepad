package com.example.notepad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;

public class DateRangeFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    RecyclerView recyclerViewDays;
    FireBaseFireStoreHelper fireBaseFireStoreHelper;
    ArrayList<String> daysList,daysDiffList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;
    AdapterForDays adapterForDays;

    public DateRangeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_range, container, false);
        calendar = Calendar.getInstance();
        fireBaseFireStoreHelper = new FireBaseFireStoreHelper();
        daysList = new ArrayList<>();
        daysDiffList = new ArrayList<>();


        recyclerViewDays = view.findViewById(R.id.recyclerViewDays);
        recyclerViewDays.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterForDays= new AdapterForDays(getActivity(),daysList,daysDiffList);



        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Days of " + userID).get().addOnCompleteListener(task -> {
            if(task.isComplete()){
                for(QueryDocumentSnapshot document: task.getResult()){
                    daysList.add(document.getString("Day Date"));
                    if(document.getString("Day Date")!=null){
                        long dayDiff = calculateDays(document.getString("Day Date"));
                        daysDiffList.add(String.valueOf(dayDiff));
                    }else{
                        daysList.add(null);
                    }

                    adapterForDays.notifyDataSetChanged();
                }
            }
        });
        recyclerViewDays.setAdapter(adapterForDays);




        initDatePicker();
        FloatingActionButton buttonAddDay = view.findViewById(R.id.buttonAddDay);
        buttonAddDay.setOnClickListener(v->{
            datePickerDialog.show();
        });

        return view;

    }

    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            month = month + 1;
            fireBaseFireStoreHelper.addNewDayInFireStore(getContext(),"null",dayOfMonth+"-"+month+"-"+year);
        };

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(getContext(),style,dateSetListener,currentYear,currentMonth,currentDay);

    }
    private String getTodayDate() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }
    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + " " +getMonthFormat(month)+ " " +year;
    }
    private String getMonthFormat(int month) {
        if(month == 1) return "JAN";
        if(month == 2) return "FEB";
        if(month == 3) return "MAR";
        if(month == 4) return "APR";
        if(month == 5) return "MAY";
        if(month == 6) return "JUN";
        if(month == 7) return "JUL";
        if(month == 8) return "AUG";
        if(month == 9) return "SEP";
        if(month == 10) return "OCT";
        if(month == 11) return "NOV";
        if(month == 12) return "DEC";
        //default should never happen
        return "Null";
    }
    private long calculateDays(String dateString){

        String[] parts = dateString.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        currentMonth = currentMonth+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate date1 = LocalDate.of(currentYear, currentMonth, currentDay);
        LocalDate date2 = LocalDate.of(year, month, day);

        // Calculate the number of days between the two dates
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return daysBetween;

    }


}