package com.andromeda.immicart.delivery.trackingorder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;
import com.andromeda.immicart.R;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TrackOrderBottomSheet extends BottomSheetDialogFragment {


    DonutProgress donutProgress;
    Button retry_btn;
    TextView countDownTxtView;
    AppCompatImageButton closeBtn;
    String TAG = "TrackOrderBottomSheet";
    public static TrackOrderBottomSheet newInstance() {
        TrackOrderBottomSheet fragment;
        fragment = new TrackOrderBottomSheet();
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return new BottomSheetDialog(requireContext(), getTheme());
//    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

//                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
//                bottomSheetBehavior.setHideable(false);//Important to add
//                bottomSheetBehavior.setSkipCollapsed(true);//Important to add

            }
        });



        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.loading_stk_push, container, false);

        donutProgress = view.findViewById(R.id.donut_progress);
        retry_btn = view.findViewById(R.id.retry_btn);
        countDownTxtView = view.findViewById(R.id.countdown);
        closeBtn = view.findViewById(R.id.closeBtn);
        startCountDown();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;

    }


    void startCountDown() {
        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                retry_btn.setVisibility(View.GONE);
                countDownTxtView.setVisibility(View.VISIBLE);

                double perc = (millisUntilFinished/30000.0);
                double percentage =  (perc * 100);
                double coveredPerc = 100 - percentage;

                int covered = 30000 - (int) millisUntilFinished;
//                int percentage = (((covered/30000) * 100));
                donutProgress.setProgress((float) coveredPerc);
                Log.d(TAG, "Percentage : " + coveredPerc);
//                donutProgress.setDonut_progress(percentage);

                int secondsRemaining = ((int) millisUntilFinished)/1000;
                String text = "Retry in " + "<b>" + secondsRemaining + "</b";
                countDownTxtView.setText(Html.fromHtml(text));

            }

            @Override
            public void onFinish() {
                retry_btn.setVisibility(View.VISIBLE);
                countDownTxtView.setVisibility(View.GONE);
                donutProgress.setProgress(100);
                retry_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCountDown();
                    }
                });

            }

//            onTick(millisUntilFinished: Long) {
//
//                resend_code.visibility = View.GONE
//
//                val secondsRemaining = millisUntilFinished.toInt() / 1000
//
//                val sourceString = "Code expires in <b>0:$secondsRemaining</b>"
//                countdown.setText(Html.fromHtml(sourceString))
//
//                //here you can have your logic to set text to edittext
//            }
//
//            override fun onFinish() {
//                resend_code.visibility = View.VISIBLE
//                countdown.text = "Code expired. Resend Code?"
//            }

        }.start();
    }
}
