package com.example.experiment_3;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.experiment_3.Model.Data;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private boolean isOpen = false;

    //Floating button's corresponding text view
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private Animation fadeOpen,fadeClose;

    //Dashboard Income and expense totals
    private TextView incomeTxtResult;
    private TextView expenseTxtResult;

    //PieChart
    private PieChart piechart;

    //Values for Piechart
    private int totalIncome;
    private int totalExpense;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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
        View myView = inflater.inflate(R.layout.fragment_dash_board, container, false);
        piechart = myView.findViewById(R.id.pie_chart);

        createPiechart();

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        //connect fab to layout
        fab_main = myView.findViewById(R.id.main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_btn);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);
        fab_income_txt = myView.findViewById(R.id.income_ft_text);

        //animaiton connections
        fadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        incomeTxtResult = myView.findViewById(R.id.income_result);
        expenseTxtResult = myView.findViewById(R.id.expense_result);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ftAnimation();
               addData();
            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncome=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalIncome+=data.getAmount();
                }
                incomeTxtResult.setText(String.valueOf(totalIncome));
                createPiechart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalExpense=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalExpense+=data.getAmount();
                }
                expenseTxtResult.setText(String.valueOf(totalExpense));
                createPiechart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myView;


    }

    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        }else{
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }
    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIncomeData();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertExpenseData();
            }
        });
    }
    public void insertIncomeData(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myView);
        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        final EditText edtAmount=myView.findViewById(R.id.amount_edt);
        final EditText edtType=myView.findViewById(R.id.type_edt);
        final EditText edtNote=myView.findViewById(R.id.note_edt);

        Button btnSave=myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = edtAmount.getText().toString().trim();
                String type = edtType.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field");
                    return;
                }
                int amountInt = Integer.parseInt(amount);
                String id = mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amountInt,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data Added",Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void insertExpenseData(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myView);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);
        final EditText edtAmount=myView.findViewById(R.id.amount_edt);
        final EditText edtType=myView.findViewById(R.id.type_edt);
        final EditText edtNote=myView.findViewById(R.id.note_edt);

        Button btnSave=myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = edtAmount.getText().toString().trim();
                String type = edtType.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field");
                    return;
                }
                int amountInt = Integer.parseInt(amount);
                String id = mExpenseDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amountInt,type,note,id,mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data Added",Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void createPiechart(){
        piechart.setUsePercentValues(true);

        piechart.getDescription().setEnabled(false);
        Legend legend = piechart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);


        piechart.setHoleRadius(25f);
        piechart.setTransparentCircleRadius(25f);


        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(totalIncome,"Income"));
        value.add(new PieEntry(totalExpense,"Expense"));

        PieDataSet pieDataSet = new PieDataSet(value,"Income-expense Distribution");
        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        piechart.animateXY(1400,1400);
    }
}