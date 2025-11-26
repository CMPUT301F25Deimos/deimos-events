package com.example.deimos_events.ui.createEvent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentCreateEventBinding;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.ui.createEvent.createViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class createFragment extends Fragment {
    private FragmentCreateEventBinding binding;

    private createViewModel viewModel;
    private Button upload;
    private ImageView image;
    private EditText title;
    private EditText Description;
    private EditText month;
    private EditText day;
    private EditText cap;
    private TextView desc;
    private TextView registration;
    private TextView capped;
    private TextView open;
    private TextView geo;
    private Switch location;
    private Button create;
    private EditText year;





    @Nullable
    @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_event, container,false);

        upload = view.findViewById(R.id.button);
        title = view.findViewById(R.id.title);
        Description = view.findViewById(R.id.editText);
        month = view.findViewById(R.id.month);
        day = view.findViewById(R.id.day);
        cap = view.findViewById(R.id.cap);
        desc = view.findViewById(R.id.textView);
        registration = view.findViewById(R.id.textView3);
        capped = view.findViewById(R.id.textView4);
        open = view.findViewById(R.id.textView5);
        geo = view.findViewById(R.id.textView6);
        location = view.findViewById(R.id.switch1);
        year = view.findViewById(R.id.year);
        create = view.findViewById(R.id.create);
        image = view.findViewById(R.id.imageView);
        final ActivityResultLauncher<String> pickImageLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                image.setImageURI(uri);
                            }
                        });
        viewModel = new ViewModelProvider(this).get(createViewModel.class);
        upload.setOnClickListener(v ->{
            pickImageLauncher.launch("image/*");
        });

        create.setOnClickListener(v->{
            String name = title.getText().toString();
            if(name.isEmpty()){
                title.setError("Title cannot be empty");
                return;
            }
            String d = day.getText().toString();
            if(d.isEmpty()){
                day.setError("Day cannot be empty");
                return;
            }
            String m = month.getText().toString();
            if(m.isEmpty()){
                month.setError("Month cannot be empty");
                return;
            }
            Integer capacity;
            String c = cap.getText().toString();
            if(c.isEmpty()){
                capacity = -1;
            }else {
                capacity = Integer.parseInt(cap.getText().toString());
            }
            Boolean loc = location.isChecked();

            String y = year.getText().toString();
            if(y.isEmpty()){
                year.setError("Year cannot be empty");
                return;
            }
            String decs = Description.getText().toString();
            if(decs.isEmpty()){
                Description.setError("Description cannot be empty");
                return;
            }
            Drawable drawable = image.getDrawable();
            if (!(drawable instanceof BitmapDrawable)) {
                Toast.makeText(getContext(), "Please upload a valid image", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
            String dateString = d +" "+ m+" "+ y;
            Date date;
            try {
                date =  formatter.parse(dateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Bitmap imageBit = ((BitmapDrawable) drawable).getBitmap();
            UUID id = UUID.randomUUID();
            String uniqueId = id.toString();
            BitMatrix qr;
            try {
               qr = new MultiFormatWriter().encode(uniqueId,BarcodeFormat.QR_CODE, 400,400);
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
            SessionManager SM = ((EventsApp)requireActivity().getApplicationContext()).getSessionManager();
            EventManager EM = new EventManager(SM);
            Event event = EM.createEvent(uniqueId,name,imageBit,decs,date,capacity,loc,qr);

            EM.insertEvent(event, result -> {
                if (result.isSuccess()){
                    Log.i("TAG", "Event created successfully");
                } else {
                    Log.i("TAG", "Event unsuccessfully created");
                }
            });
            NavController navController = NavHostFragment.findNavController(this);
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_organizers_events, false).build();
            Bundle arg = new Bundle();
            arg.putString("id", uniqueId);
            SM.getSession().setCurrentEvent(event);
            navController.navigate(R.id.navigation_edit, arg, navOptions);
        });

    return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
